package com.demo;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import org.testng.annotations.Test;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class SecondTest extends BaseTest {
	@Test
	public void demo() {
		page.navigate("https://www.testmuai.com/selenium-playground/");
		
		Locator loc = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Drag & Drop Sliders"));
		loc.click();
		
		int target = 95;
		Locator slider = page.locator("#slider3 input[type='range']");
		int min = Integer.parseInt(slider.getAttribute("min"));
		int max = Integer.parseInt(slider.getAttribute("max"));
		slider.scrollIntoViewIfNeeded();
		double x = ((Number) slider.evaluate("el => el.getBoundingClientRect().x")).doubleValue();
		double y = ((Number) slider.evaluate("el => el.getBoundingClientRect().y")).doubleValue();
		double width = ((Number) slider.evaluate("el => el.getBoundingClientRect().width")).doubleValue();
		double height = ((Number) slider.evaluate("el => el.getBoundingClientRect().height")).doubleValue();
		double thumbOffset = height / 2;
		double trackWidth = width - height;
		double currentPosition = x + thumbOffset + (Integer.parseInt(slider.inputValue()) - min) * trackWidth / (max - min);
		double targetPosition = x + thumbOffset + (target - min - 1) * trackWidth / (max - min);
		double centerY = y + height / 2;
		page.mouse().move(currentPosition, centerY);
		page.mouse().down();
		page.mouse().move(targetPosition, centerY);
		page.mouse().up();
		
		assertThat(page.locator("#slider3 output")).hasText(String.valueOf(target));
		
	}
}
