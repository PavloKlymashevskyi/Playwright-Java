package com.serenitydojo.playwright.examples;

import com.microsoft.playwright.*;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.SelectOption;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;


@UsePlaywright(HeadlessChromeOptions.class)
public class PlaywrightFormsTest {
    @DisplayName("Interacting with text fields")
    @Nested
    class WhenInteractingWithTextFields {
        @BeforeEach
        void openContactPage(Page page) {
            page.navigate("https://practicesoftwaretesting.com/contact");
        }

        @DisplayName("Complete the form")
        @Test
        void completeForm(Page page) throws URISyntaxException {
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

        @DisplayName("mandatory fields")
        @ParameterizedTest
        @ValueSource(strings = {"First Name", "Last Name", "Email", "Message"})
        void mandatoryFields(String fieldName, Page page) {
            var firstNameField = page.getByLabel("First Name");
            var lastNameField = page.getByLabel("Last Name");
            var emailNameField = page.getByLabel("Email");
            var messageField = page.getByLabel("Message");
            var sendButton = page.getByText("Send");

            // Fill in the field values
            firstNameField.fill("Sara-Jane");
            lastNameField.fill("Smith");
            emailNameField.fill("example@example.com");
            messageField.fill("Hello World");

            // Clear one of the fields
            page.getByLabel(fieldName).clear();

            sendButton.click();

            // Check the error message for that field
            var errorMessage = page.getByRole(AriaRole.ALERT).getByText(fieldName + " is required");
            assertThat(errorMessage).isVisible();

        }
    }

}
