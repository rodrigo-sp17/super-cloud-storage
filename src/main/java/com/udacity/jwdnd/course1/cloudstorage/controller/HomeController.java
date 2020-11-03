package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.entity.Credential;
import com.udacity.jwdnd.course1.cloudstorage.entity.File;
import com.udacity.jwdnd.course1.cloudstorage.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.service.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public String homeView(@ModelAttribute Note note, Model model) {
        model.addAttribute("files", fileService.getAllFiles());
        model.addAttribute("notes", noteService.getAllNotes());
        model.addAttribute("credentials", credentialService.getAllCredentials());
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
                                           Model model) {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());

        File file = fileService.getFile(fileId);
        if (file == null) {
            model.addAttribute("fileError", "Ops! Error retrieving file. Please, try again!");
        }

        headers.setContentType(MediaType.parseMediaType(file.getContentType()));
        headers.setContentDisposition(ContentDisposition.builder("inline")
                .filename(file.getFileName())
                .build());

        return ResponseEntity.ok().headers(headers).body(file.getFileData());
    }

    @GetMapping(value = "/file", params = "delete")
    public String deleteFile(@RequestParam("delete") Integer fileId, Model model) {
        String deleteSuccess = null;
        String deleteError = null;

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
        model.addAttribute("files", fileService.getAllFiles());

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

        // TODO - attempt to redirect to a fragment - the tab does not change, however
        return "redirect:/home#nav-notes";
    }

    @GetMapping(value = "/note", params = "delete")
    public String deleteNote(@RequestParam("delete") Integer noteId, Model model) {
        String deleteError = null;
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
    public String deleteCredential(@ModelAttribute Credential credential, Model model) {
        String deleteError = null;
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
