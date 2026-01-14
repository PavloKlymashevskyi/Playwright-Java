package com.serenitydojo.playwright;

import com.microsoft.playwright.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PlaywrightWaitsTest {

    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext browserContext;

    Page page;

    @BeforeAll
    static void setUpBrowser() {
        playwright = Playwright.create();
        playwright.selectors().setTestIdAttribute("data-test");
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

    @Nested
    class AutomaticWaits {
        @BeforeEach
        void openHomePage() {
            page.navigate("https://practicesoftwaretesting.com");
        }

        // Automatic wait
        @Test
        @DisplayName("Should wait for the filter checkbox options to apper before clicking")
        void shouldWaitForTheFilterCheckboxes() {
            var screwDriverFilter = page.getByLabel("Screwdriver");

            screwDriverFilter.click();

            assertThat(screwDriverFilter).isChecked();
        }
    }
}
