package com.demo;

import com.google.gson.JsonObject;
import com.microsoft.playwright.*;

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

public class BaseTest {
	Playwright playwright;
	Browser browser;
	BrowserContext context;
	Page page;
	
	Boolean sessionFailed = false;
	
	@BeforeClass
	@Parameters({"browserName", "platform"})
    public void setup(Method method, String browserName, String platform) throws UnsupportedEncodingException {
		
		String username = System.getenv("LT_USERNAME");
		String accessKey = System.getenv("LT_ACCESS_KEY");
		
		if(username == null || username.isEmpty() || accessKey == null || accessKey.isEmpty()) {
			throw new IllegalStateException("Please provide username and password");
		}
		
		playwright = Playwright.create();
		
        JsonObject capabilities = new JsonObject();
        JsonObject options = new JsonObject();


        capabilities.addProperty("browsername", browserName); 
        capabilities.addProperty("browserVersion", "latest");
        options.addProperty("platform", platform);
        options.addProperty("build", "Playwright 101 Java TestNG");
        options.addProperty("user", username);
        options.addProperty("accessKey", accessKey);
        options.addProperty("name", getClass().getSimpleName() + "-" + browserName + "-" + platform);
        
        options.addProperty("network", true);
        options.addProperty("video", true);
        options.addProperty("visual", true);
        options.addProperty("console", true);
        
        capabilities.add("LT:Options", options);
        
		String endpoint = "wss://cdp.lambdatest.com/playwright?capabilities=" + URLEncoder.encode(capabilities.toString(), StandardCharsets.UTF_8.name());
        
        browser = playwright.chromium().connect(endpoint);

        
    }

	@BeforeMethod
    public void createPage() {
    	context = browser.newContext();
    	page = context.newPage();
    }
	
	@AfterMethod
	public void closePage(ITestResult result) {
		if(result.getStatus() == ITestResult.FAILURE || result.getStatus() == ITestResult.SKIP) {
			sessionFailed = true;
		}
		
		if(page!=null) {
			try {
				JsonObject action = new JsonObject();
				action.addProperty("action", "setTestStatus");
				
				JsonObject arguments = new JsonObject();
				arguments.addProperty("status", sessionFailed? "failed" : "passed");
				
				arguments.addProperty("remark", sessionFailed ? "One or more tests failed or were skipped" : "All test passed");
				
				action.add("arguments", arguments);
				
				page.evaluate("_=>{}", "lambdatest_action:" + action);
				
			} catch (Exception e) {
				System.err.println("Could not report cloud status: " + e.getMessage());
			}
		}
		
		if(context != null) {
			context.close();
			context = null;
			page = null;
		}
		
	}
		
	@AfterClass(alwaysRun = true)
	public void disconnectFromTest() {
		if(browser!=null) {
			browser.close();
		}
		
		if(playwright != null) {
			playwright.close();
		}
	}
		
		
}
