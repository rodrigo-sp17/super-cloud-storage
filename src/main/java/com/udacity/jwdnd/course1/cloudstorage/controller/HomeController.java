package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.entity.Credential;
import com.udacity.jwdnd.course1.cloudstorage.entity.File;
import com.udacity.jwdnd.course1.cloudstorage.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.service.*;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

@Controller
@RequestMapping("/home")
public class HomeController {
    private final NoteService noteService;
    private final UserService userService;
    private final FileService fileService;
    private final CredentialService credentialService;
    private final EncryptionService encryptionService;

    public HomeController(NoteService noteService, UserService userService, FileService fileService,
                          CredentialService credentialService, EncryptionService encryptionService) {
        this.noteService = noteService;
        this.userService = userService;
        this.fileService = fileService;
        this.credentialService = credentialService;
        this.encryptionService = encryptionService;
    }

    @GetMapping
    public String homeView(Authentication authentication, @ModelAttribute Note note, Model model) {
        Integer userId = userService.getUser(authentication.getName()).getUserId();

        model.addAttribute("files", fileService.getAllFiles(userId));
        model.addAttribute("notes", noteService.getAllNotes(userId));
        model.addAttribute("credentials", credentialService.getAllCredentials(userId));
        model.addAttribute("encryptionService", encryptionService);

        return "home";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("fileUpload") MultipartFile file,
                             Authentication authentication,
                             Model model) {
        String uploadSuccess = null;
        String uploadError = null;
        if (fileService.isFilenameAvailable(file.getOriginalFilename())) {
            String username = authentication.getName();
            Integer userId = userService.getUser(username).getUserId();
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

        if (uploadError == null) {
            model.addAttribute("fileSuccess", uploadSuccess);
        } else {
            model.addAttribute("fileError", uploadError);
        }

        return "redirect:/home";
    }

    @GetMapping(value = "/file", params = "view")
    @ResponseBody
    public ResponseEntity<byte[]> viewFile(@RequestParam("view") Integer fileId,
                                           Authentication authentication,
                                           Model model) {
        Integer userId = userService.getUser(authentication.getName()).getUserId();

        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());

        File file = fileService.getFile(fileId);
        if (file == null) {
            model.addAttribute("fileError", "Ops! Error retrieving file. Please, try again!");
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
                             Authentication authentication,
                             Model model) {
        Integer userId = userService.getUser(authentication.getName()).getUserId();
        String deleteSuccess = null;
        String deleteError = null;

        Integer fileUserId = fileService.getFile(fileId).getUserId();
        if (userId != fileUserId) {
            throw new AccessDeniedException("You do not have access for this operation");
        }
        int rowsDeleted = fileService.deleteFile(fileId);
        if (rowsDeleted < 1) {
            deleteError = "Ops! Could not delete file. Please, try again!";
        } else {
            deleteSuccess = "File deleted successfully!";
        }

        if (deleteError == null) {
            model.addAttribute("fileSuccess", deleteSuccess);
        } else {
            model.addAttribute("fileError", deleteError);
        }
        //model.addAttribute("files", fileService.getAllFiles());

        return "redirect:/home";
    }

    @PostMapping("/submit-note")
    public String submitNote(Authentication authentication,
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
            model.addAttribute("noteSuccess", noteSuccess);
        } else {
            model.addAttribute("noteError", noteError);
        }

        return "redirect:/home";
    }

    @GetMapping(value = "/note", params = "delete")
    public String deleteNote(@RequestParam("delete") Integer noteId,
                             Authentication authentication,
                             Model model) {
        Integer userId = userService.getUser(authentication.getName()).getUserId();
        String deleteError = null;

        Integer noteUserId = noteService.getNote(noteId).getUserId();
        if (userId != noteUserId) {
            throw new AccessDeniedException("You do not have access for this operation");
        }

        int rowsDeleted = noteService.deleteNote(noteId);
        if (rowsDeleted < 1) {
            deleteError = "Ops! Could not delete note. Please, try again!";
        }

        if (deleteError == null) {
            model.addAttribute("noteSuccess", "Note deleted successfully!");
        } else {
            model.addAttribute("noteError", deleteError);
        }

        return "redirect:/home";
    }

    @PostMapping("/submit-credential")
    public String submitCredential(Authentication authentication,
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
            model.addAttribute("credentialSuccess", credentialSuccess);
        } else {
            model.addAttribute("credentialError", credentialError);
        }

        return "redirect:/home";
    }

    @PostMapping("/delete-credential")
    public String deleteCredential(@ModelAttribute Credential credential,
                                   Authentication authentication,
                                   Model model) {
        Integer userId = userService.getUser(authentication.getName()).getUserId();
        String deleteError = null;

        Integer credentialUserId = credentialService
                .getCredential(credential.getCredentialId())
                .getUserId();
        if (userId != credentialUserId) {
            throw new AccessDeniedException("You do not have access for this operation");
        }
        int rowsDeleted = credentialService.deleteCredential(credential.getCredentialId());
        if (rowsDeleted < 1) {
            deleteError = "Oh snap! Could not delete the credential! Please, try again!";
        }

        if (deleteError == null) {
            String deleteSuccess = "Credential deleted successfully!";
            model.addAttribute("credentialSuccess", deleteSuccess);
        } else {
            model.addAttribute("credentialError", deleteError);
        }

        return "redirect:/home";
    }
}
