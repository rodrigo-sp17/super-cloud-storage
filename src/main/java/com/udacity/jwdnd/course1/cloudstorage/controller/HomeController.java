package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.config.UploadConfig;
import com.udacity.jwdnd.course1.cloudstorage.entity.Credential;
import com.udacity.jwdnd.course1.cloudstorage.entity.File;
import com.udacity.jwdnd.course1.cloudstorage.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.service.*;
import org.apache.ibatis.mapping.Environment;
import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Controller
@ControllerAdvice
@RequestMapping("/home")
public class HomeController {
    @Autowired
    private NoteService noteService;
    @Autowired
    private UserService userService;
    @Autowired
    private FileService fileService;
    @Autowired
    private CredentialService credentialService;
    @Autowired
    private EncryptionService encryptionService;

    @ModelAttribute
    public void addAttributes(Authentication auth, Model model) {
        if (auth != null) {
            Integer userId = userService.getUser(auth.getName()).getUserId();
            model.addAttribute("files", fileService.getAllFiles(userId));
            model.addAttribute("notes", noteService.getAllNotes(userId));
            model.addAttribute("credentials", credentialService.getAllCredentials(userId));
            model.addAttribute("encryptionService", encryptionService);
        }
    }

    @GetMapping
    public String homeView(@ModelAttribute Note note, Model model) {
        return "home";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("fileUpload") MultipartFile file,
                             RedirectAttributes redirectAttributes,
                             Authentication authentication,
                             Model model) {
        String username = authentication.getName();
        Integer userId = userService.getUser(username).getUserId();
        String uploadSuccess = null;
        String uploadError = null;

        if (file.getOriginalFilename().isBlank()) {
            uploadError = "There is nothing to upload!";
        } else {
            if (fileService.isFilenameAvailable(file.getOriginalFilename())) {
                int rowsAdded = fileService.createFile(file, userId);
                if (rowsAdded < 0) {
                    uploadError = "Ops! Error uploading file. Please, try again!";
                } else {
                    uploadSuccess = "File uploaded successfully!";
                }
            } else {
                uploadError = "Ops! There is already a file with this name!" +
                        " Please, choose another file!";
            }
        }

        if (uploadError == null) {
            redirectAttributes.addFlashAttribute("fileSuccess", uploadSuccess);
        } else {
            redirectAttributes.addFlashAttribute("fileError", uploadError);
        }

        return "redirect:/home";
    }

    // See more at https://www.baeldung.com/spring-maxuploadsizeexceeded
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ModelAndView handleUploadException(MaxUploadSizeExceededException e,
                                              HttpServletRequest request,
                                              HttpServletResponse response) {

        String uploadError = String.format("Maximum file size for uploading is %d MB!",
                UploadConfig.MAX_UPLOAD_SIZE.toMegabytes());

        ModelAndView mav = new ModelAndView("home");
        mav.addObject("fileError", uploadError);

        return mav;
    }

    @GetMapping(value = "/file", params = "view")
    @ResponseBody
    public ResponseEntity<byte[]> viewFile(@RequestParam("view") Integer fileId,
                                           RedirectAttributes redirectAttributes,
                                           Authentication authentication,
                                           Model model) {
        Integer userId = userService.getUser(authentication.getName()).getUserId();

        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());

        File file = fileService.getFile(fileId);
        if (file == null) {
            redirectAttributes.addFlashAttribute("fileError",
                    "Ops! Error retrieving file. Please, try again!");
            return ResponseEntity.badRequest().body(null);
        }

        if (!file.getUserId().equals(userId)) {
            throw new AccessDeniedException("You do not have access for this operation");
        }

        headers.setContentType(MediaType.parseMediaType(file.getContentType()));
        headers.setContentDisposition(ContentDisposition.builder("inline")
                .filename(file.getFileName())
                .build());

        return ResponseEntity.ok().headers(headers).body(file.getFileData());
    }

    @GetMapping(value = "/file", params = "delete")
    public String deleteFile(@RequestParam("delete") Integer fileId,
                             RedirectAttributes redirectAttributes,
                             Authentication authentication,
                             Model model) {
        Integer userId = userService.getUser(authentication.getName()).getUserId();
        String deleteSuccess = null;
        String deleteError = null;

        Integer fileUserId = fileService.getFile(fileId).getUserId();
        if (!Objects.equals(userId, fileUserId)) {
            throw new AccessDeniedException("You do not have access for this operation");
        }
        int rowsDeleted = fileService.deleteFile(fileId);
        if (rowsDeleted < 1) {
            deleteError = "Ops! Could not delete file. Please, try again!";
        } else {
            deleteSuccess = "File deleted successfully!";
        }

        if (deleteError == null) {
            redirectAttributes.addFlashAttribute("fileSuccess", deleteSuccess);
        } else {
            redirectAttributes.addFlashAttribute("fileError", deleteError);
        }

        return "redirect:/home";
    }

    @PostMapping("/submit-note")
    public String submitNote(Authentication authentication,
                             RedirectAttributes redirectAttributes,
                             @ModelAttribute Note note,
                             Model model) {
        String noteSuccess = null;
        String noteError = null;
        Integer noteId = note.getNoteId();
        String username = authentication.getName();
        Integer userId = userService.getUser(username).getUserId();

        // Ensures the note is set with the current user id as needed
        note.setUserId(userId);

        if (noteId == null) {
            // adds note
            int rowsAdded = noteService.createNote(note);
            if (rowsAdded < 0) {
                noteError = "Ops! Could not add the note. Please, try again!";
            } else {
                noteSuccess = "Note added successfully!";
            }

        } else {
            // edits note
            int rowsEdited = noteService.updateNote(note);
            if (rowsEdited < 0) {
                noteError = "Ops! Could not edit the note. Please, try again!";
            } else {
                noteSuccess = "Note edited successfully!";
            }
        }

        if (noteError == null) {
            redirectAttributes.addFlashAttribute("noteSuccess", noteSuccess);
        } else {
            redirectAttributes.addFlashAttribute("noteError", noteError);
        }

        return "redirect:/home";
    }

    @GetMapping(value = "/note", params = "delete")
    public String deleteNote(@RequestParam("delete") Integer noteId,
                             RedirectAttributes redirectAttributes,
                             Authentication authentication,
                             Model model) {
        Integer userId = userService.getUser(authentication.getName()).getUserId();
        String deleteError = null;

        Integer noteUserId = noteService.getNote(noteId).getUserId();
        if (!Objects.equals(userId, noteUserId)) {
            throw new AccessDeniedException("You do not have access for this operation");
        }

        int rowsDeleted = noteService.deleteNote(noteId);
        if (rowsDeleted < 1) {
            deleteError = "Ops! Could not delete note. Please, try again!";
        }

        if (deleteError == null) {
            redirectAttributes.addFlashAttribute("noteSuccess", "Note deleted successfully!");
        } else {
            redirectAttributes.addFlashAttribute("noteError", deleteError);
        }

        return "redirect:/home";
    }

    @PostMapping("/submit-credential")
    public String submitCredential(Authentication authentication,
                                   RedirectAttributes redirectAttributes,
                                   @ModelAttribute Credential credential,
                                   Model model) {
        String credentialError = null;
        String credentialSuccess = null;
        Integer userId = userService.getUser(authentication.getName()).getUserId();
        credential.setUserId(userId);

        if (credential.getCredentialId() == null) {
            // add
            int rowsAdded = credentialService.createCredential(credential);
            if (rowsAdded < 0) {
                credentialError = "Could not add the credential. Please, try again!";
            } else {
                credentialSuccess = "Credential created successfully!";
            }
        } else {
            // edit
            int rowsEdited = credentialService.editCredential(credential);
            if (rowsEdited < 0) {
                credentialError = "Could not edit the credential. Please, try again!";
            } else {
                credentialSuccess = "Credential edited successfully!";
            }
        }

        if (credentialError == null) {
            redirectAttributes.addFlashAttribute("credentialSuccess", credentialSuccess);
        } else {
            redirectAttributes.addFlashAttribute("credentialError", credentialError);
        }

        return "redirect:/home";
    }

    @PostMapping("/delete-credential")
    public String deleteCredential(@ModelAttribute Credential credential,
                                   RedirectAttributes redirectAttributes,
                                   Authentication authentication,
                                   Model model) {
        Integer userId = userService.getUser(authentication.getName()).getUserId();
        String deleteError = null;

        Integer credentialUserId = credentialService
                .getCredential(credential.getCredentialId())
                .getUserId();
        if (!Objects.equals(userId, credentialUserId)) {
            throw new AccessDeniedException("You do not have access for this operation");
        }
        int rowsDeleted = credentialService.deleteCredential(credential.getCredentialId());
        if (rowsDeleted < 1) {
            deleteError = "Oh snap! Could not delete the credential! Please, try again!";
        }

        if (deleteError == null) {
            String deleteSuccess = "Credential deleted successfully!";
            redirectAttributes.addFlashAttribute("credentialSuccess", deleteSuccess);
        } else {
            redirectAttributes.addFlashAttribute("credentialError", deleteError);
        }

        return "redirect:/home";
    }
}
