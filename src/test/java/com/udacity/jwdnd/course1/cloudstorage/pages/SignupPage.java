package com.udacity.jwdnd.course1.cloudstorage.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.io.FileNotFoundException;
import java.time.Duration;

public class SignupPage {
    @FindBy(id = "inputFirstName")
    private WebElement inputFirstName;

    @FindBy(id = "inputLastName")
    private WebElement inputLastName;

    @FindBy(id = "inputUsername")
    private WebElement inputUserName;

    @FindBy(id = "inputPassword")
    private WebElement inputPassword;

    @FindBy(id = "submit-button")
    private WebElement submitButton;

    @FindBy(id = "success-msg")
    private WebElement successMsg;

    @FindBy(id = "fail-msg")
    private WebElement failMsg;

    private final WebDriver driver;

    public SignupPage(WebDriver driver) {

        PageFactory.initElements(driver, this);
        this.driver = driver;
    }

    public void signup(String firstName, String lastName,
                       String userName, String password) {
        inputFirstName.sendKeys(firstName);
        inputLastName.sendKeys(lastName);
        inputUserName.sendKeys(userName);
        inputPassword.sendKeys(password);
        submitButton.click();
    }

    /**
     * Checks if the signup operation was successful
     * @return true if operation was successful, otherwise false
     */
    public boolean isSignupSuccessful() {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(10))
                .pollingEvery(Duration.ofSeconds(2));
        try {
            successMsg = driver.findElement(By.id("success-msg"));
            return successMsg.isDisplayed();
        } catch (NoSuchElementException e) {
            try {
                failMsg = driver.findElement(By.id("fail-msg"));
                return !failMsg.isDisplayed();
            } catch (NoSuchElementException f) {
                throw new AssertionError("Required elements could not be found");
            }
        }
    }
}
