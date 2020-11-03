package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.entity.Credential;
import com.udacity.jwdnd.course1.cloudstorage.entity.File;
import com.udacity.jwdnd.course1.cloudstorage.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.service.*;
import org.springframework.boot.Banner;
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
        String uploadError = null;
        if (fileService.isFilenameAvailable(file.getOriginalFilename())) {
            String username = authentication.getName();
            Integer userId = userService.getUser(username).getUserId();
            fileService.createFile(file, userId);
        } else {
            uploadError = "Oops, there is already a file with this name!" +
                    " Please, choose another file name!";
        }

        model.addAttribute("files", fileService.getAllFiles());

        if (uploadError == null) {
            model.addAttribute("uploadSuccess", true);
        } else {
            model.addAttribute("uploadError", uploadError);
        }

        return "home";
    }

    @GetMapping(value = "/file", params = "view")
    @ResponseBody
    public ResponseEntity<byte[]> viewFile(@RequestParam("view") Integer fileId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().getHeaderValue());

        File file = fileService.getFile(fileId);

        headers.setContentType(MediaType.parseMediaType(file.getContentType()));
        headers.setContentDisposition(ContentDisposition.builder("inline")
                .filename(file.getFileName())
                .build());

        return ResponseEntity.ok().headers(headers).body(file.getFileData());
    }

    @GetMapping(value = "/file", params = "delete")
    public String deleteFile(@RequestParam("delete") Integer fileId, Model model) {
        fileService.deleteFile(fileId);

        model.addAttribute("files", fileService.getAllFiles());
        return "home";
    }

    @PostMapping("/submit-note")
    public String submitNote(Authentication authentication,
                             @ModelAttribute Note note,
                             Model model) {
        Integer noteId = note.getNoteId();
        String username = authentication.getName();
        Integer userId = userService.getUser(username).getUserId();

        // Ensures the note is set with the current user id as needed
        note.setUserId(userId);

        if (noteId == null) {
            // adds note
            Integer id = noteService.createNote(note);
            note.setNoteId(id);
        } else {
            // edits note
            noteService.updateNote(note);
        }

        model.addAttribute("notes", noteService.getAllNotes());
        // attempt to redirect to a fragment - the tab does not change, however
        return "redirect:/home#nav-notes";
    }

    @GetMapping(value = "/note", params = "delete")
    public String deleteNote(@RequestParam("delete") Integer noteId, Model model) {
        noteService.deleteNote(noteId);

        model.addAttribute("notes", noteService.getAllNotes());

        return "home";
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

        model.addAttribute("credentials", credentialService.getAllCredentials());
        model.addAttribute("encryptionService", encryptionService);
        return "home";
    }

    @PostMapping("/delete-credential")
    public String deleteCredential(@ModelAttribute Credential credential, Model model) {
        String deleteError = null;
        int rowsDeleted = credentialService.deleteCredential(credential.getCredentialId());
        if (rowsDeleted < 0) {
            deleteError = "Oh snap! Could not delete the credential! Please, try again!";
        }

        if (deleteError == null) {
            String deleteSuccess = "Credential deleted successfully!";
            model.addAttribute("credentialSuccess", deleteSuccess);
        } else {
            model.addAttribute("credentialError", deleteError);
        }

        model.addAttribute("credentials", credentialService.getAllCredentials());
        model.addAttribute("encryptionService", encryptionService);
        return "home";
    }
}
