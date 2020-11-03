package com.udacity.jwdnd.course1.cloudstorage.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    @FindBy(id = "inputUsername")
    private WebElement inputUserName;

    @FindBy(id = "inputPassword")
    private WebElement inputPassword;

    @FindBy(id = "submit-button")
    private WebElement submitButton;

    @FindBy(id = "error-msg")
    private WebElement errorMsg;

    @FindBy(id = "logout-msg")
    private WebElement logoutMsg;

    private final WebDriver driver;

    public LoginPage(WebDriver driver) {
        PageFactory.initElements(driver, this);
        this.driver = driver;
    }

    public void login(String userName, String password ) {
        inputUserName.sendKeys(userName);
        inputPassword.sendKeys(password);
        submitButton.click();
    }

    public boolean isLoginSuccessful() {
        Wait<WebDriver> wait = new WebDriverWait(driver, 1500);
        wait.until(ExpectedConditions.visibilityOf(errorMsg));
        try {
            errorMsg = driver.findElement(By.id("error-msg"));
            return !errorMsg.isDisplayed();
        } catch (NoSuchElementException e) {
            return true;
        }
    }

    public boolean isLogoutSuccessful() {
        Wait<WebDriver> wait = new WebDriverWait(driver, 1500);
        wait.until(ExpectedConditions.visibilityOf(logoutMsg));
        try {
            logoutMsg = driver.findElement(By.id("logout-msg"));
            return logoutMsg.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
