package com.demo;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.util.regex.Pattern;

import org.testng.annotations.Test;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class FirstTest extends BaseTest {
	@Test
	public void demo()  {
		
		page.navigate("https://www.testmuai.com/selenium-playground/");
		Locator loc = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Simple Form Demo"));
		loc.click();
		
		
		assertThat(page).hasURL(Pattern.compile(".*simple-form-demo.*"));
		
		String msgValue = "Welcome to TestMu AI";
		
		Locator msgField = page.getByPlaceholder("Please enter your Message");
		msgField.fill(msgValue);
		page.waitForTimeout(2000);
		
		Locator getCheckedValueBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Get Checked Value"));
		getCheckedValueBtn.click();
		
		Locator message = page.locator("#message");
		assertThat(message).hasText(msgValue);
		
	}
}
