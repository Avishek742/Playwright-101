package com.demo;

import java.util.regex.Pattern;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/** Covers the three playground scenarios using the shared LambdaTest session. */
public class AllTest extends BaseTest {

	@Test
	public void shouldDisplayEnteredMessage() {

		page.navigate("https://www.testmuai.com/selenium-playground/");
		Locator simpleFormLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Simple Form Demo"));
		simpleFormLink.click();
		
		
		assertThat(page).hasURL(Pattern.compile(".*simple-form-demo.*"));
		
		String expectedMessage = "Welcome to TestMu AI";
		
		Locator messageInput = page.getByPlaceholder("Please enter your Message");
		messageInput.fill(expectedMessage);
		page.waitForTimeout(2000);
		
		Locator showMessageButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Get Checked Value"));
		showMessageButton.click();
		
		Locator displayedMessage = page.locator("#message");
		assertThat(displayedMessage).hasText(expectedMessage);
		
	}

	@Test
	public void shouldDragSliderTo95() {

		page.navigate("https://www.testmuai.com/selenium-playground/");
		
		Locator slidersLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Drag & Drop Sliders"));
		slidersLink.click();
		
		int targetValue = 95;
		Locator slider = page.locator("#slider3 input[type='range']");
		int minimumValue = Integer.parseInt(slider.getAttribute("min"));
		int maximumValue = Integer.parseInt(slider.getAttribute("max"));
		slider.scrollIntoViewIfNeeded();
		double sliderLeft = ((Number) slider.evaluate("element => element.getBoundingClientRect().x")).doubleValue();
		double sliderTop = ((Number) slider.evaluate("element => element.getBoundingClientRect().y")).doubleValue();
		double sliderWidth = ((Number) slider.evaluate("element => element.getBoundingClientRect().width")).doubleValue();
		double sliderHeight = ((Number) slider.evaluate("element => element.getBoundingClientRect().height")).doubleValue();
		// Approximate the thumb diameter with the input height to find the usable track.
		double thumbOffset = sliderHeight / 2;
		double trackWidth = sliderWidth - sliderHeight;
		double currentThumbX = sliderLeft + thumbOffset + (Integer.parseInt(slider.inputValue()) - minimumValue) * trackWidth / (maximumValue - minimumValue);
		// Preserve the existing one-step adjustment; its effect depends on browser rendering.
		double targetThumbX = sliderLeft + thumbOffset + (targetValue - minimumValue - 1) * trackWidth / (maximumValue - minimumValue);
		double sliderCenterY = sliderTop + sliderHeight / 2;
		page.mouse().move(currentThumbX, sliderCenterY);
		page.mouse().down();
		page.mouse().move(targetThumbX, sliderCenterY);
		page.mouse().up();
		
		assertThat(page.locator("#slider3 output")).hasText(String.valueOf(targetValue));
		
	}

	@Test
	public void shouldSubmitInputForm() {
		
		page.navigate("https://www.testmuai.com/selenium-playground/");
		
		Locator inputFormLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Input Form Submit"));
		inputFormLink.click();
		page.waitForLoadState(LoadState.NETWORKIDLE);
		
		Locator nameField = page.locator("#name");
		Locator submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));
		// Check native required-field validation before providing any form data.
		submitButton.click();
		Assert.assertEquals(nameField.evaluate("element => element.validationMessage"), "Please fill out this field.");
		
		nameField.fill("John Doe");
		page.locator("#inputEmail4").fill("john@mail.com");
		page.locator("#inputPassword4").fill("John@12345");
		page.locator("#company").fill("John Pvt Ltd");
		page.locator("#websitename").fill("www.john.in");
		page.locator("select[name='country']").selectOption("US");
		page.locator("//input[@id='inputCity']").fill("Los Angeles");
		page.locator("#inputAddress1").fill("123 Street");
		page.locator("#inputAddress2").fill("Lane 123");
		page.locator("#inputState").fill("California");
		page.locator("#inputZip").fill("10001");
		submitButton.click();
		Locator successMessage = page.locator("p.success-msg");
		assertThat(successMessage).isVisible();
		assertThat(successMessage).hasText("Thanks for contacting us, we will get back to you shortly.");
		
	}
}
