package com.serenitydojo.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.SelectOption;
import org.junit.jupiter.api.*;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PlaywrightFormsTest {
    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext browserContext;

    Page page;

    @BeforeAll
    static void setUpBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
                        .setArgs(Arrays.asList("--no-sandbox", "--disable-extensions", "--disable-gpu"))
        );
    }

    @BeforeEach
    void setUp() {
        browserContext = browser.newContext();
        page = browserContext.newPage();
    }

    @AfterEach
    void closeContext() {
        browserContext.close();
    }

    @AfterAll
    static void tearDown() {
        browser.close();
        playwright.close();
    }

    @DisplayName("Interacting with text fields")
    @Nested
    class WhenInteractingWithTextFields {
        @BeforeEach
        void openContactPage() {
            page.navigate("https://practicesoftwaretesting.com/contact");
        }

        @DisplayName("Complete the form")
        @Test
        void completeForm() throws URISyntaxException {
            var firstNameField = page.getByLabel("First Name");
            var lastNameField = page.getByLabel("Last Name");
            var emailNameField = page.getByLabel("Email");
            var messageField = page.getByLabel("Message");
            var subjectField = page.getByLabel("Subject");

            var uploadField = page.getByLabel("Attachment");

            firstNameField.fill("Sara-Jane");
            lastNameField.fill("Smith");
            emailNameField.fill("example@example.com");
            messageField.fill("Hello World");
            subjectField.selectOption(new SelectOption().setIndex(2));

            Path fileToUpload = Paths.get(ClassLoader.getSystemResource("data/sample-data.txt").toURI());
            page.setInputFiles("#attachment", fileToUpload);

            assertThat(firstNameField).hasValue("Sara-Jane");
            assertThat(lastNameField).hasValue("Smith");
            assertThat(emailNameField).hasValue("example@example.com");
            assertThat(subjectField).hasValue("webmaster");

            String uploadedFile = uploadField.inputValue();
            org.assertj.core.api.Assertions.assertThat(uploadedFile).endsWith("sample-data.txt");
        }

    }

}
