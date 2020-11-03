package com.udacity.jwdnd.course1.cloudstorage.pages;

import com.udacity.jwdnd.course1.cloudstorage.entity.Credential;
import com.udacity.jwdnd.course1.cloudstorage.entity.Note;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class HomePage {
    @FindBy(id = "logout-button")
    private WebElement logoutButton;

    @FindBy(id = "addNote-button")
    private WebElement addNoteButton;

    @FindBy(id = "note-title")
    private WebElement inputNoteTitle;

    @FindBy(id = "note-description")
    private WebElement inputNoteDescription;

    @FindBy(id = "noteSubmit-button")
    private WebElement noteSubmitButton;

    @FindBy(id = "userTable")
    private WebElement noteTable;

    @FindBy(id = "note-rows")
    private WebElement notesTableBody;

    @FindBy(id = "nav-notes-tab")
    private WebElement notesTab;

    //@FindBy(id = "nav-notes")
    //private WebElement navNotes;

    @FindBy(id = "addCredential-button")
    private WebElement addCredentialButton;

    @FindBy(id = "credential-url")
    private WebElement inputCredentialUrl;

    @FindBy(id = "credential-username")
    private WebElement inputCredentialUserName;

    @FindBy(id = "credential-password")
    private WebElement inputCredentialPassword;

    @FindBy(id = "credentialSubmit-button")
    private WebElement credentialSubmitButton;

    @FindBy(id = "credentialClose-button")
    private WebElement credentialCloseButton;

    @FindBy(id = "nav-credentials-tab")
    private WebElement credentialsTab;

    @FindBy(id = "credential-rows")
    private WebElement credentialTableBody;

    @FindBy(id = "nav-files-tab")
    private WebElement filesTab;



    /*
    @FindBy(id = "editNote-button")
    private WebElement editNoteButton;

    @FindBy(id = "deleteNote-button")
    private WebElement deleteNoteButton;
    */

    private final WebDriver driver;

    public HomePage(WebDriver driver) {
        PageFactory.initElements(driver, this);
        this.driver = driver;
    }

    public void logout() {
        WebDriverWait wait = new WebDriverWait(driver, 1000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logout-button")));
        logoutButton.click();
    }

    public void setFilesTab() {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofSeconds(2));
        filesTab.click();
    }

    public void setNotesTab() {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofSeconds(2));
        //wait.until(ExpectedConditions.visibilityOf(notesTab));
        notesTab.click();
    }

    public void setCredentialsTab() {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofSeconds(2));
        credentialsTab.click();
    }

    public void addNote(Note note) {
        WebDriverWait wait = new WebDriverWait(driver, 2000);
        wait.until(ExpectedConditions.elementToBeClickable(addNoteButton));
        addNoteButton.click();

        driver.switchTo().activeElement();
        wait.until(ExpectedConditions.visibilityOf(inputNoteTitle));
        inputNoteTitle.sendKeys(note.getNoteTitle());
        inputNoteDescription.sendKeys(note.getNoteDescription());

        noteSubmitButton.click();
    }

    public List<Note> getNotes() {
        List<Note> notes = new ArrayList<>();

        WebDriverWait wait = new WebDriverWait(driver, 2000);
        wait.until(ExpectedConditions.visibilityOf(notesTableBody));

        List<WebElement> rows = notesTableBody.findElements(By.tagName("tr"));
        rows.forEach(row -> {
            //Integer noteId = Integer.parseInt(row.findElement(By.name("noteId")).getText());
            String noteTitle = row.findElement(By.name("noteTitle")).getText();
            String noteDescription = row.findElement(By.name("noteDescription")).getText();
            notes.add(new Note(null, noteTitle, noteDescription, null));
        });
        return notes;
    }

    public void editNote(Note editedNote) {
        List<WebElement> rows = notesTableBody.findElements(By.tagName("tr"));
        rows.forEach(row -> {
            String noteTitle = row.findElement(By.name("noteTitle")).getText();
            if (noteTitle.equals(editedNote.getNoteTitle())) {
                row.findElement(By.name("edit-button")).click();
            }
        });

        driver.switchTo().activeElement();
        WebDriverWait wait = new WebDriverWait(driver, 2000);
        wait.until(ExpectedConditions.visibilityOf(inputNoteTitle));

        inputNoteTitle.clear();
        inputNoteTitle.sendKeys(editedNote.getNoteTitle());
        inputNoteDescription.clear();
        inputNoteDescription.sendKeys(editedNote.getNoteDescription());

        noteSubmitButton.click();
    }

    public void deleteNote(Note note) {
        List<WebElement> rows = notesTableBody.findElements(By.tagName("tr"));
        rows.forEach(row -> {
            String noteTitle = row.findElement(By.name("noteTitle")).getText();
            if (noteTitle.equals(note.getNoteTitle())) {
                row.findElement(By.name("delete-button")).click();
            }
        });
    }

    private List<WebElement> getCredentialRows() {
        WebDriverWait wait = new WebDriverWait(driver, 2000);
        wait.until(ExpectedConditions.visibilityOf(credentialTableBody));

        return credentialTableBody.findElements(By.tagName("tr"));
    }

    public List<Credential> getCredentials() {
        List<Credential> credentials = new ArrayList<>();

        List<WebElement> rows = getCredentialRows();
        rows.forEach(row -> {
            String url = row.findElement(By.name("url")).getText();
            String userName = row.findElement(By.name("userName")).getText();
            String password = row.findElement(By.name("password")).getText();
            credentials.add(new Credential(null,
                    url, userName, null, password, null));
        });

        return credentials;
    }

    // create credential
    public void addCredential(Credential credential) {
        WebDriverWait wait = new WebDriverWait(driver, 2000);
        wait.until(ExpectedConditions.elementToBeClickable(addCredentialButton));
        addCredentialButton.click();

        driver.switchTo().activeElement();
        wait.until(ExpectedConditions.visibilityOf(inputCredentialUrl));
        inputCredentialUrl.clear();
        inputCredentialUrl.sendKeys(credential.getUrl());
        inputCredentialUserName.clear();
        inputCredentialUserName.sendKeys(credential.getUserName());
        inputCredentialPassword.clear();
        inputCredentialPassword.sendKeys(credential.getPassword());

        credentialSubmitButton.click();
    }

    /**
     * Returns a credential corresponding to the Credential currently displayed in the modal.
     *
     * @param displayUrl    the Url of the credential to view, as displayed in the table
     * @param displayUsername  the userName of the credential to view, as displayed in the table
     * @return  Credential object with url, userName and password fields as displayed on the modal
     *          form.
     */
    public Credential viewCredential(String displayUrl, String displayUsername) {
        List<WebElement> rows = getCredentialRows();
        rows.forEach(row -> {
            String url = row.findElement(By.name("url")).getText();
            String userName = row.findElement(By.name("userName")).getText();
            if (url.equals(displayUrl)
                    && userName.equals(displayUsername)) {
                row.findElement(By.name("edit-button")).click();
            }
        });
        driver.switchTo().activeElement();
        WebDriverWait wait = new WebDriverWait(driver, 2000);
        wait.until(ExpectedConditions.visibilityOf(inputCredentialUrl));
        String url = inputCredentialUrl.getAttribute("value");
        String username = inputCredentialUserName.getAttribute("value");
        String password = inputCredentialPassword.getAttribute("value");

        return new Credential(null,
                url,
                username,
                null,
                password,
                null);
    }

    public void closeCredentialModal() {
        WebDriverWait wait = new WebDriverWait(driver, 2000);
        wait.until(ExpectedConditions.visibilityOf(credentialCloseButton));
        credentialCloseButton.click();
    }

    // edit credential
    public void editCredential(String url, String username, Credential editedCredential) {
        viewCredential(url, username);
        inputCredentialUrl.clear();
        inputCredentialUrl.sendKeys(editedCredential.getUrl());
        inputCredentialUserName.clear();
        inputCredentialUserName.sendKeys(editedCredential.getUserName());
        inputCredentialPassword.clear();
        inputCredentialPassword.sendKeys(editedCredential.getPassword());

        credentialSubmitButton.click();
    }

    // delete credential
    public void deleteCredential(Credential credential) {
        getCredentialRows().forEach(row -> {
            String url = row.findElement(By.name("url")).getText();
            String userName = row.findElement(By.name("userName")).getText();
            if (url.equals(credential.getUrl()) &&
            userName.equals(credential.getUserName())) {
                driver.findElement(By.name("delete-button")).click();
            }
        });
    }
}
