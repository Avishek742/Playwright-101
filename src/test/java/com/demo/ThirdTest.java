package com.demo;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.AriaRole;

public class ThirdTest extends BaseTest {
	@Test
	public void demo() {
		
		page.navigate("https://www.testmuai.com/selenium-playground/");
		
		Locator loc = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Input Form Submit"));
		loc.click();
		page.waitForLoadState(LoadState.NETWORKIDLE);
		
		Locator nameField = page.locator("#name");
		Locator submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));
		submitButton.click();
		Assert.assertEquals(nameField.evaluate("el => el.validationMessage"), "Please fill out this field.");
		
		nameField.fill("Test User");
		page.locator("#inputEmail4").fill("user@example.com");
		page.locator("#inputPassword4").fill("User@123");
		page.locator("#company").fill("XYZ Co Pvt Ltd");
		page.locator("#websitename").fill("www.xyz.co");
		page.locator("select[name='country']").selectOption("US");
		page.locator("//input[@id='inputCity']").fill("Los Angeles");
		page.locator("#inputAddress1").fill("123 Example Street");
		page.locator("#inputAddress2").fill("Zone 8");
		page.locator("#inputState").fill("California");
		page.locator("#inputZip").fill("10001");
		submitButton.click();
		Locator successMessage = page.locator("p.success-msg");
		assertThat(successMessage).isVisible();
		assertThat(successMessage).hasText("Thanks for contacting us, we will get back to you shortly.");
		
	}
}
