package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.entity.Credential;
import com.udacity.jwdnd.course1.cloudstorage.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.entity.User;
import com.udacity.jwdnd.course1.cloudstorage.pages.HomePage;
import com.udacity.jwdnd.course1.cloudstorage.pages.LoginPage;
import com.udacity.jwdnd.course1.cloudstorage.pages.SignupPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

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
            //driver.quit();
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
        //assertEquals("Home", driver.getTitle());
    }

    final User user1 = new User(null, "charlie-1", null,
            "123charlie", "Charlie", "Waffles");
    private boolean signedUpUser1 = false;

    final User user2 = new User(null, "BRAVO-2", null,
            "bravo@@##", "Bravo", "Smith");
    final User user3 = new User(null, "delta@", null,
            "bravo@@##", "Delta", "Joe");

    private void signup(User user) {
        getSignupPage();
        SignupPage signupPage = new SignupPage(driver);
        signupPage.signup(user.getFirstName(), user.getLastName(),
                user.getUserName(), user.getPassword());
    }

    private void login(User user) {
        if (!signedUpUser1) {
            signup(user1);
        }
        getLoginPage();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(user.getUserName(), user.getPassword());
    }

    @Test
    public void testUnauthorizedAccess() {
        getHomePage();
        assertEquals(driver.getTitle(), "Login");
    }

    @Test
    public void testLogout() {
        login(user1);

        getHomePage();
        HomePage homePage = new HomePage(driver);
        homePage.logout();

        assertTrue(driver.getCurrentUrl().endsWith("logout"));

        getHomePage();
        assertEquals(driver.getTitle(), "Login");

    }

    @Test
    public void testNewUser() {
        getSignupPage();
        SignupPage signupPage = new SignupPage(driver);

        signupPage.signup(user1.getFirstName(), user1.getLastName(),
                user1.getUserName(), user1.getPassword());
        assertTrue(driver.findElement(By.id("success-msg")).isDisplayed());
        signedUpUser1 = true;
        assertTrue(signupPage.isSignupSuccessful());

        getLoginPage();
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(user1.getUserName(), user1.getPassword());
        assertTrue(driver.getCurrentUrl().endsWith("/home"));
    }

    @Test
    public void testWrongLogin() {
        getLoginPage();
        LoginPage loginPage = new LoginPage(driver);

        loginPage.login(user2.getUserName(), user2.getPassword());
        assertFalse(loginPage.isLoginSuccessful());
    }

    @Test
    public void testSignupSameUsername() {
        getSignupPage();
        SignupPage signupPage = new SignupPage(driver);

        signupPage.signup(user3.getFirstName(), user3.getLastName(),
                user3.getUserName(), user3.getPassword());
        assertTrue(driver.findElement(By.id("success-msg")).isDisplayed());
        assertTrue(signupPage.isSignupSuccessful());

        getSignupPage();
        signupPage = new SignupPage(driver);
        signupPage.signup(user3.getFirstName(), user3.getLastName(),
                user3.getUserName(), user3.getPassword());
        assertTrue(driver.findElement(By.id("fail-msg")).isDisplayed());
        assertFalse(signupPage.isSignupSuccessful());
    }

    @Test
    public void testNotes() {
        if (!signedUpUser1) {
            signup(user1);
        }
        login(user1);
        getHomePage();
        HomePage homePage = new HomePage(driver);

        Note note1 = new Note(null, "Title1",
                "description1", null);
        Note note2 = new Note(null, "title2",
                "description2", null);

        // Adds note
        homePage.setNotesTab();
        homePage.addNote(note1);

        homePage.setNotesTab();
        List<Note> notes = homePage.getNotes();
        Set<String> noteTitles = notes.stream()
                .map(Note::getNoteTitle)
                .collect(Collectors.toSet());

        assertTrue(noteTitles.contains(note1.getNoteTitle()));

        // Edits note
        homePage.setNotesTab();
        note1.setNoteDescription("edited note");
        homePage.editNote(note1);

        homePage.setNotesTab();
        String displayedDescription = homePage.getNotes().get(0).getNoteDescription();
        assertEquals(displayedDescription, "edited note");

        // Delete note
        homePage.setNotesTab();
        homePage.deleteNote(note1);
        assertTrue(homePage.getNotes().isEmpty());
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
    public void testCreateCredential() {
        login(user1);
        getHomePage();
        HomePage homePage = new HomePage(driver);
        homePage.setCredentialsTab();

        homePage.addCredential(credential1);

        homePage.setCredentialsTab();
        List<Credential> credentials = homePage.getCredentials();
        Set<String> usernames = credentials.stream().map(Credential::getUserName)
                .collect(Collectors.toSet());
        Set<String> urls = credentials.stream().map(Credential::getUrl)
                .collect(Collectors.toSet());
        Set<String> passwords = credentials.stream().map(Credential::getPassword)
                .collect(Collectors.toSet());

        homePage.addCredential(credential2);
        homePage.setCredentialsTab();
        credentials = homePage.getCredentials();
        usernames = credentials.stream().map(Credential::getUserName)
                .collect(Collectors.toSet());
        urls = credentials.stream().map(Credential::getUrl)
                .collect(Collectors.toSet());
        passwords = credentials.stream().map(Credential::getPassword)
                .collect(Collectors.toSet());
        assertEquals(2, credentials.size());
        assertTrue(usernames.contains(credential1.getUserName()));
        assertTrue(usernames.contains(credential2.getUserName()));
        assertTrue(urls.contains(credential1.getUrl()));
        assertTrue(urls.contains(credential2.getUrl()));
        assertFalse(passwords.contains(credential1.getPassword()));
        assertFalse(passwords.contains(credential2.getPassword()));
    }

    @Test
    public void testEditCredential() {
        login(user1);
        getHomePage();
        HomePage homePage = new HomePage(driver);

        homePage.setCredentialsTab();
        homePage.addCredential(credential3);

        String originalUrl = credential3.getUrl();
        String originalUsername = credential3.getUserName();
        String originalPassword = credential3.getPassword();

        credential3.setUrl("http://mockurl.com");
        credential3.setUserName("changed username");
        credential3.setPassword("neweditedpassword");

        homePage.editCredential(originalUrl, originalUsername, credential3);

        homePage.setCredentialsTab();
        List<Credential> credentials = homePage.getCredentials();
        Set<String> usernames = credentials.stream().map(Credential::getUserName)
                .collect(Collectors.toSet());
        Set<String> urls = credentials.stream().map(Credential::getUrl)
                .collect(Collectors.toSet());
        Set<String> passwords = credentials.stream().map(Credential::getPassword)
                .collect(Collectors.toSet());
        assertTrue(usernames.contains(credential3.getUserName()));
        assertFalse(usernames.contains(originalUsername));

        assertTrue(urls.contains(credential3.getUrl()));
        assertFalse(urls.contains(originalUrl));

        // Ensures the list does not have plain passwords on site
        assertFalse(passwords.contains(originalPassword));
        assertFalse(passwords.contains(credential3.getPassword()));

        homePage.setCredentialsTab();
        Credential displayedCredential = homePage.viewCredential(credential3.getUrl(),
                credential3.getUserName());
        assertEquals(displayedCredential.getUrl(), credential3.getUrl());
        assertEquals(displayedCredential.getUserName(), credential3.getUserName());
        // Password should be decrypted in window
        assertEquals(displayedCredential.getPassword(), credential3.getPassword());
    }

    @Test
    public void testDeleteCredential() {
        login(user1);getHomePage();
        HomePage homePage = new HomePage(driver);

        Credential credential4 = new Credential(
                null,
                "https://google.com/",
                "anotherone",
                null,
                "decryptedpassword4",
                null
        );

        homePage.setCredentialsTab();
        homePage.addCredential(credential4);
        homePage.setCredentialsTab();

        homePage.setCredentialsTab();
        List<Credential> credentials = homePage.getCredentials();
        Set<String> usernames = credentials.stream().map(Credential::getUserName)
                .collect(Collectors.toSet());
        Set<String> urls = credentials.stream().map(Credential::getUrl)
                .collect(Collectors.toSet());
        Set<String> passwords = credentials.stream().map(Credential::getPassword)
                .collect(Collectors.toSet());

        assertTrue(usernames.contains(credential4.getUserName()));
        assertTrue(urls.contains(credential4.getUrl()));
        // Ensures the list does not have plain passwords on site
        assertFalse(passwords.contains(credential4.getPassword()));

        homePage.setCredentialsTab();
        homePage.deleteCredential(credential4);

        homePage.setCredentialsTab();
        credentials = homePage.getCredentials();
        usernames = credentials.stream().map(Credential::getUserName)
                .collect(Collectors.toSet());
        urls = credentials.stream().map(Credential::getUrl)
                .collect(Collectors.toSet());
        assertFalse(usernames.contains(credential4.getUserName()) &&
                urls.contains(credential4.getUrl()));

    }
}
