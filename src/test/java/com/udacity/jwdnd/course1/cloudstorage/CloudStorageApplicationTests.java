package com.udacity.jwdnd.course1.cloudstorage;

import com.udacity.jwdnd.course1.cloudstorage.entity.Note;
import com.udacity.jwdnd.course1.cloudstorage.entity.User;
import com.udacity.jwdnd.course1.cloudstorage.pages.HomePage;
import com.udacity.jwdnd.course1.cloudstorage.pages.LoginPage;
import com.udacity.jwdnd.course1.cloudstorage.pages.SignupPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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
		if (!signedUpUser1) {
			signup(user1);
		}
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

}
