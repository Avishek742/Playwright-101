# Playwright Assignment

This project automates three scenarios on the [TestMu AI Selenium Playground](https://www.testmuai.com/selenium-playground/) using Java, Playwright, and TestNG. Tests run on LambdaTest cloud browsers rather than a locally launched browser.

## Technology Stack

| Tool | Version | Purpose |
| --- | --- | --- |
| Playwright for Java | 1.63.0 | Browser interactions and assertions |
| TestNG | 7.12.0 | Test methods, lifecycle hooks, and parallel suite execution |
| Gson | 2.14.0 | LambdaTest capabilities and status payloads |
| Maven Compiler Plugin | 3.13.0 | Compilation with `release` set to `8` |
| Maven Surefire Plugin | 3.5.4 | Test execution and reports |

Dependency and plugin versions are defined in [pom.xml](pom.xml).

## Test Scenarios

The scenarios are implemented in [AllTest.java](src/test/java/com/demo/AllTest.java).

| Scenario | Test Method | Checks |
| --- | --- | --- |
| Simple Form Demo | `shouldDisplayEnteredMessage` | Opens the correct page, enters `Welcome to TestMu AI`, and verifies the displayed message. |
| Drag & Drop Sliders | `shouldDragSliderTo95` | Drags the third slider using the mouse and verifies that its output is `95`. |
| Input Form Submit | `shouldSubmitInputForm` | Checks required-field validation, fills the form, selects the United States, and verifies the submission confirmation. |

## Browser Coverage

[testng.xml](testng.xml) runs `com.demo.AllTest` with these configurations:

| Browser | Version | Platform |
| --- | --- | --- |
| Chrome | Latest | Windows 10 |
| Microsoft Edge | Latest | Windows 11 |

The suite uses `parallel="tests"` with two threads. The browser configurations run in parallel; methods within each configuration are not configured for parallel execution. A complete run executes three scenarios on each browser, for six test executions.

## Prerequisites

- A current JDK, such as JDK 17 or later, is recommended. The Java 8 compiler release setting is not a guarantee that all dependencies can run on JDK 8.
- Apache Maven available on `PATH`.
- A LambdaTest account with Playwright access and enough available capacity for two concurrent sessions.
- Internet access to LambdaTest and the playground website.

Check the tools from your terminal:

```powershell
java -version
mvn -version
```

## Configure Credentials

In PowerShell, replace the placeholders with your LambdaTest username and access key:

```powershell
$env:LT_USERNAME = 'your-lambdatest-username'
$env:LT_ACCESS_KEY = 'your-lambdatest-access-key'
```

Run Maven from that same terminal. These variables apply only to the current PowerShell session and processes started from it. Single quotes preserve special characters such as `$` literally.

For Bash, use:

```bash
export LT_USERNAME='your-lambdatest-username'
export LT_ACCESS_KEY='your-lambdatest-access-key'
```

Keep credentials private. Do not put real access keys in source files, documentation, or commits.

## Run the Tests

Open a terminal in the project root, where [pom.xml](pom.xml) and [testng.xml](testng.xml) are located, and run:

```powershell
mvn clean test "-Dsurefire.suiteXmlFiles=testng.xml"
```

Pass the suite file explicitly because it supplies the required `browserName` and `platform` parameters. The current Maven configuration does not select this suite automatically.

To compile the tests without creating LambdaTest sessions:

```powershell
mvn test-compile
```

## Results and Cloud Sessions

- Maven prints the test summary and build result in the terminal.
- Surefire writes local reports to `target/surefire-reports/` after test execution.
- LambdaTest sessions use the build name `Playwright 101 Java TestNG`.
- Session names include the class, browser, and platform, such as `AllTest-Chrome-Windows 10`.
- Network, video, visual, and console capture options are enabled in the cloud capabilities.

The `clean` goal deletes previous `target/` output, including local reports.

## Project Structure

```text
PlaywrightAssignment/
|-- pom.xml
|-- README.md
|-- testng.xml
`-- src/test/java/com/demo/
	|-- AllTest.java      # Three playground scenarios
	`-- BaseTest.java     # Cloud connection, page creation, status, and cleanup
```

## Test Lifecycle

[BaseTest.java](src/test/java/com/demo/BaseTest.java) manages the shared setup:

1. Before the class runs, read `LT_USERNAME` and `LT_ACCESS_KEY`, build the capabilities, and connect to LambdaTest.
2. Before each method, create a fresh browser context and page to isolate cookies and storage.
3. After each method, report the cumulative session status and close the context. Once a failure or skip is recorded by this hook, a later pass does not reset it.
4. After the class, close the browser connection and Playwright instance.

Each browser configuration has its own test class instance and cloud session.

## Known Assumptions

- Slider coordinates approximate the thumb size using the input height and include a one-step adjustment. Browser rendering changes may require recalibration.
- The form validation assertion expects the English message `Please fill out this field.` A different browser language may produce different text.
- The message scenario includes a fixed two-second wait, and the form scenario waits for network idle. Website timing or background traffic can affect execution.
- The suite uses the latest browser versions and a public website, so browser updates or site changes can affect results.

## Troubleshooting

| Issue | What to Check |
| --- | --- |
| `mvn` is not recognized | Install Maven, add its `bin` directory to `PATH`, and open a new terminal. |
| Missing credential error | Set both environment variables in the same terminal used to start Maven. |
| Missing `browserName` or `platform` parameter | Run the documented command with `-Dsurefire.suiteXmlFiles=testng.xml`. |
| Cloud connection or authentication failure | Check credentials, account access, available concurrency, and network connectivity. |
| Slider assertion failure | Inspect the cloud recording and compare the slider position with the expected output of `95`. |
| Required-field assertion failure | Check the browser language and the current validation message. |
