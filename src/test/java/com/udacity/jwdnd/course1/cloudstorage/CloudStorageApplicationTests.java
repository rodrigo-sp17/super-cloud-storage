package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.entity.Credential;
import com.udacity.jwdnd.course1.cloudstorage.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.entity.User;
import com.udacity.jwdnd.course1.cloudstorage.pages.HomePage;
import com.udacity.jwdnd.course1.cloudstorage.pages.LoginPage;
import com.udacity.jwdnd.course1.cloudstorage.pages.SignupPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {

    @LocalServerPort
    private int port;

    private WebDriver driver;

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void beforeEach() {
        this.driver = new ChromeDriver();
    }

    @AfterEach
    public void afterEach() {
        if (this.driver != null) {
            driver.quit();
        }
    }

    @Test
    public void getLoginPage() {
        driver.get("http://localhost:" + this.port + "/login");
        assertEquals("Login", driver.getTitle());
    }

    @Test
    public void getSignupPage() {
        driver.get("http://localhost:" + this.port + "/signup");
        assertEquals("Sign Up", driver.getTitle());
    }

    private void getHomePage() {
        driver.get("http://localhost:" + this.port + "/home");
    }

    final User user1 = new User(null, "charlie-1", null,
            "123charlie", "Charlie", "Waffles");
    private boolean signedUpUser1 = false;

    final User user2 = new User(null, "BRAVO-2", null,
            "bravo@@##", "Bravo", "Smith");
    final User user3 = new User(null, "delta@", null,
            "bravo@@##", "Delta", "Joe");

    private SignupPage signup(User user) {
        getSignupPage();
        SignupPage signupPage = new SignupPage(driver);
        signupPage.signup(user.getFirstName(), user.getLastName(),
                user.getUserName(), user.getPassword());
        return signupPage;
    }

    private LoginPage login(User user) {
        getLoginPage();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(user.getUserName(), user.getPassword());
        return loginPage;
    }

    @Test
    @Order(1)
    public void testUnauthorizedAccess() {
        getHomePage();
        assertEquals(driver.getTitle(), "Login");
    }

    @Test
    @Order(2)
    public void testNewUser() {
        // signup test
        SignupPage signupPage = signup(user1);
        assertTrue(signupPage.isSignupSuccessful());

        // login test
        LoginPage loginPage = login(user1);
        assertTrue(loginPage.isLoginSuccessful());
        assertEquals("Home", driver.getTitle());

        // logout test
        getHomePage();
        HomePage homePage = new HomePage(driver);
        homePage.logout();

        getLoginPage();
        loginPage = new LoginPage(driver);
        assertTrue(loginPage.isLogoutSuccessful());

        getHomePage();
        assertNotEquals("Home", driver.getTitle());
    }

    @Test
    public void testWrongLogin() {
        LoginPage loginPage = login(user2);
        assertFalse(loginPage.isLoginSuccessful());
        getHomePage();
        assertNotEquals("Home", driver.getTitle());
    }

    @Test
    @Order(3)
    public void testSignupSameUsername() {
        SignupPage signupPage = signup(user3);
        assertTrue(signupPage.isSignupSuccessful());

        signupPage = signup(user3);
        assertFalse(signupPage.isSignupSuccessful());
    }

    @Test
    @Order(4)
    public void testCreateNote() {
        login(user1);
        getHomePage();
        HomePage homePage = new HomePage(driver);
        homePage.setNotesTab();

        Note note1 = new Note(null, "Title1",
                "description1", null);
        Note note2 = new Note(null, "title2",
                "description2", null);

        // Adds note
        homePage.addNote(note1);

        homePage.setNotesTab();
        List<Note> notes = homePage.getNotes();
        Set<String> titles = notes.stream()
                .map(Note::getNoteTitle)
                .collect(Collectors.toSet());
        Set<String> descriptions = notes.stream()
                .map(Note::getNoteDescription)
                .collect(Collectors.toSet());
        assertTrue(titles.contains(note1.getNoteTitle()));
        assertTrue(descriptions.contains(note1.getNoteDescription()));

        // Checks if modal view is OK
        Note displayedNote = homePage.viewNote(note1.getNoteTitle(), note1.getNoteDescription());
        assertEquals(note1.getNoteTitle(), displayedNote.getNoteTitle());
        assertEquals(note1.getNoteDescription(), displayedNote.getNoteDescription());
    }

    @Test
    @Order(5)
    public void testEditNote() {
        login(user1);
        getHomePage();
        HomePage homePage = new HomePage(driver);
        homePage.setNotesTab();

        // Gets displayed notes, selects 1st one for edition
        Note displayedNote = homePage.getNotes().get(0);
        String originalTitle = displayedNote.getNoteTitle();
        String originalDescription = displayedNote.getNoteDescription();
        displayedNote.setNoteTitle("EDITED TITLE");
        displayedNote.setNoteDescription("EDITED DESCRIPTION");

        // Edits note
        homePage.editNote(originalTitle, originalDescription, displayedNote);

        homePage.setNotesTab();
        List<Note> notes = homePage.getNotes();

        Set<String> titles = notes.stream()
                .map(Note::getNoteTitle)
                .collect(Collectors.toSet());

        Set<String> descriptions = notes.stream()
                .map(Note::getNoteDescription)
                .collect(Collectors.toSet());

        assertTrue(titles.contains(displayedNote.getNoteTitle()));
        assertTrue(descriptions.contains(displayedNote.getNoteDescription()));
    }

    @Test
    @Order(6)
    public void testDeleteNote() {
        login(user1);
        getHomePage();
        HomePage homePage = new HomePage(driver);
        homePage.setNotesTab();

        // Gets displayed notes, selects 1st one for deletion
        List<Note> notes = homePage.getNotes();
        int originalNotesSize = notes.size();
        Note displayedNote = notes.get(0);

        // Deletes note
        homePage.deleteNote(displayedNote);

        homePage.setNotesTab();
        notes = homePage.getNotes();
        int finalNotesSize = notes.size();

        Set<String> titles = notes.stream()
                .map(Note::getNoteTitle)
                .collect(Collectors.toSet());

        Set<String> descriptions = notes.stream()
                .map(Note::getNoteDescription)
                .collect(Collectors.toSet());

        assertEquals(1, originalNotesSize - finalNotesSize);
        assertFalse(titles.contains(displayedNote.getNoteTitle()));
        assertFalse(descriptions.contains(displayedNote.getNoteDescription()));
    }

    Credential credential1 = new Credential(
            null,
            "https://www.facebook.com/",
            "John@DOE123",
            null,
            "decryptedpassword1",
            null
    );
    Credential credential2 = new Credential(
            null,
            "https://twitter.com/login?lang=pt",
            "JANE_d03",
            null,
            "decryptedpassword2",
            null
    );
    Credential credential3 = new Credential(
            null,
            "https://linkedin.com/",
            "krazy",
            null,
            "decryptedpassword3",
            null
    );

    @Test
    @Order(7)
    public void testCreateCredential() {
        login(user1);
        getHomePage();
        HomePage homePage = new HomePage(driver);
        homePage.setCredentialsTab();

        // Adds credentials
        homePage.addCredential(credential1);
        homePage.setCredentialsTab();
        homePage.addCredential(credential2);

        homePage.setCredentialsTab();
        List<Credential> credentials = homePage.getCredentials();
        Set<String> usernames = credentials.stream().map(Credential::getUserName)
                .collect(Collectors.toSet());

        Set<String> urls = credentials.stream().map(Credential::getUrl)
                .collect(Collectors.toSet());

        Set<String> passwords = credentials.stream().map(Credential::getPassword)
                .collect(Collectors.toSet());

        assertEquals(2, credentials.size());
        assertTrue(usernames.contains(credential1.getUserName()));
        assertTrue(usernames.contains(credential2.getUserName()));
        assertTrue(urls.contains(credential1.getUrl()));
        assertTrue(urls.contains(credential2.getUrl()));
        // Ensures decrypted password is not displayed
        assertFalse(passwords.contains(credential1.getPassword()));
        assertFalse(passwords.contains(credential2.getPassword()));
    }

    @Test
    @Order(8)
    public void testEditCredential() {
        login(user1);
        getHomePage();
        HomePage homePage = new HomePage(driver);
        homePage.setCredentialsTab();

        // credential2 should have been added by testCreateCredential
        String originalUrl = credential2.getUrl();
        String originalUsername = credential2.getUserName();
        String originalPassword = credential2.getPassword();

        credential2.setUrl("http://mockurl.com");
        credential2.setUserName("changed username");
        credential2.setPassword("neweditedpassword");

        // Edits credential
        homePage.editCredential(originalUrl, originalUsername, credential2);

        homePage.setCredentialsTab();
        List<Credential> credentials = homePage.getCredentials();
        Set<String> usernames = credentials.stream().map(Credential::getUserName)
                .collect(Collectors.toSet());

        Set<String> urls = credentials.stream().map(Credential::getUrl)
                .collect(Collectors.toSet());

        Set<String> passwords = credentials.stream().map(Credential::getPassword)
                .collect(Collectors.toSet());

        assertTrue(urls.contains(credential2.getUrl()));
        assertFalse(urls.contains(originalUrl));
        assertTrue(usernames.contains(credential2.getUserName()));
        assertFalse(usernames.contains(originalUsername));
        // Ensures the list does not have plain passwords visible
        assertFalse(passwords.contains(originalPassword));
        assertFalse(passwords.contains(credential2.getPassword()));

        // Ensures the new password is decrypted when viewed
        homePage.setCredentialsTab();
        Credential displayedCredential = homePage.viewCredential(credential2.getUrl(),
                credential2.getUserName());

        assertEquals(displayedCredential.getUrl(), credential2.getUrl());
        assertEquals(displayedCredential.getUserName(), credential2.getUserName());
        // Password should be decrypted in window
        assertEquals(displayedCredential.getPassword(), credential2.getPassword());
    }

    @Test
    @Order(9)
    public void testDeleteCredential() {
        login(user1);
        getHomePage();
        HomePage homePage = new HomePage(driver);
        homePage.setCredentialsTab();

        // deletes credential
        homePage.deleteCredential(credential2);

        homePage.setCredentialsTab();
        List<Credential> credentials = homePage.getCredentials();
        Set<String> usernames = credentials.stream().map(Credential::getUserName)
                .collect(Collectors.toSet());

        Set<String> urls = credentials.stream().map(Credential::getUrl)
                .collect(Collectors.toSet());

        assertFalse(usernames.contains(credential2.getUserName()) &&
                urls.contains(credential2.getUrl()));
    }
}
