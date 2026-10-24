package com.demo;

import com.google.gson.JsonObject;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;

/** Reuses one cloud browser per test class and creates a fresh context for each method. */
public class BaseTest {
	Playwright playwright;
	Browser browser;
	BrowserContext context;
	Page page;
	
	boolean sessionFailed = false;
	
	@BeforeClass
	@Parameters({"browserName", "platform"})
	public void setup(Method method, String browserName, String platform) throws UnsupportedEncodingException {
		
		String lambdaTestUsername = System.getenv("LT_USERNAME");
		String lambdaTestAccessKey = System.getenv("LT_ACCESS_KEY");
		
		if (lambdaTestUsername == null || lambdaTestUsername.isEmpty()
				|| lambdaTestAccessKey == null || lambdaTestAccessKey.isEmpty()) {
			throw new IllegalStateException("Set LT_USERNAME and LT_ACCESS_KEY before running the tests.");
		}
		
		playwright = Playwright.create();
		
		JsonObject capabilities = new JsonObject();
		JsonObject lambdaTestOptions = new JsonObject();

		capabilities.addProperty("browsername", browserName);
		capabilities.addProperty("browserVersion", "latest");
		lambdaTestOptions.addProperty("platform", platform);
		lambdaTestOptions.addProperty("build", "Playwright 101 Java TestNG");
		lambdaTestOptions.addProperty("user", lambdaTestUsername);
		lambdaTestOptions.addProperty("accessKey", lambdaTestAccessKey);
		lambdaTestOptions.addProperty("name", getClass().getSimpleName() + "-" + browserName + "-" + platform);

		lambdaTestOptions.addProperty("network", true);
		lambdaTestOptions.addProperty("video", true);
		lambdaTestOptions.addProperty("visual", true);
		lambdaTestOptions.addProperty("console", true);

		capabilities.add("LT:Options", lambdaTestOptions);

		String cloudEndpoint = "wss://cdp.lambdatest.com/playwright?capabilities="
				+ URLEncoder.encode(capabilities.toString(), StandardCharsets.UTF_8.name());
		browser = playwright.chromium().connect(cloudEndpoint);
	}

	@BeforeMethod
	public void createPage() {
		// Isolate cookies and storage while keeping the cloud browser session open.
		context = browser.newContext();
		page = context.newPage();
	}
	
	@AfterMethod
	public void closePage(ITestResult testResult) {
		// A later passing test must not overwrite a failure in the shared session.
		if (testResult.getStatus() == ITestResult.FAILURE || testResult.getStatus() == ITestResult.SKIP) {
			sessionFailed = true;
		}
		
		if (page != null) {
			try {
				JsonObject statusAction = new JsonObject();
				statusAction.addProperty("action", "setTestStatus");
				
				JsonObject statusDetails = new JsonObject();
				statusDetails.addProperty("status", sessionFailed ? "failed" : "passed");
				
				statusDetails.addProperty("remark", sessionFailed ? "One or more tests failed or were skipped" : "All tests passed");
				
				statusAction.add("arguments", statusDetails);
				
				page.evaluate("_=>{}", "lambdatest_action:" + statusAction);
				
			} catch (Exception reportingFailure) {
				System.err.println("Could not report cloud status: " + reportingFailure.getMessage());
			}
		}
		
		if (context != null) {
			context.close();
			context = null;
			page = null;
		}
		
	}
		
	@AfterClass(alwaysRun = true)
	public void disconnectFromTest() {
		if (browser != null) {
			browser.close();
		}
		
		if (playwright != null) {
			playwright.close();
		}
	}
		
		
}
