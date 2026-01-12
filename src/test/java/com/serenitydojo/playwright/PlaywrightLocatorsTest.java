package com.serenitydojo.playwright;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Arrays;
import java.util.List;

@Execution(ExecutionMode.SAME_THREAD)
public class PlaywrightLocatorsTest {
    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext browserContext;

    Page page;

    @BeforeAll
    static void setUpBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true)
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

    private void openPage() {
        page.navigate("https://practicesoftwaretesting.com");
    }


    @DisplayName("Locating elements  by text")
    @Nested
    class LocateElementsBy {
        @BeforeEach
        void openTheCatalogPage() {
            openPage();
        }

        @DisplayName("Locating an element by text contents")
        @Test
        void byText() {
            page.getByText("Bolt Cutters").click();

            PlaywrightAssertions.assertThat(page.getByText("MightyCraft Hardware")).isVisible();
        }

        @DisplayName("Using alt text")
        @Test
        void byAltText() {
            page.getByAltText("Combination Pliers").click();

            PlaywrightAssertions.assertThat(page.getByText("ForgeFlex Tools")).isVisible();
        }

        @DisplayName("Using title")
        @Test
        void byTitle() {
            page.getByAltText("Combination Pliers").click();

            page.getByTitle("Practice Software Testing - Toolshop").click();
        }

        @DisplayName("Using label")
        @Test
        void byLabel() {
            page.getByText("Sign in").click();
            page.getByLabel("Email address *").fill("example@example.com");
        }


        @DisplayName("Using elements by role")
        @Test
        // ARIA - Accessible Rich Internet Application
        void byRole() {
            page.getByRole(AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName("Search ")).click();
        }

        @DisplayName("Using Test IDs")
        @Test
            // A specific attribute("e.g.: "data-testid"), but we have data-test
        void testID() {
            playwright.selectors().setTestIdAttribute("data-test");
            page.getByTestId("nav-sign-in").click(); //Hand Tools
        }

        @DisplayName("Working with collections")
        @Test
        void collections() {
            int itemsOnThePage = page.locator(".card").count();
            page.locator(".card").first().count();
            page.locator(".card").nth(1).count();
            page.locator(".card").last().count();

            playwright.selectors().setTestIdAttribute("data-test");
            List<String> ItemNames = page.getByTestId("product-name").allTextContents();
        }

        @DisplayName("Working with css Selectors")
        @Test
        void cssSelectors() {
            page.getByText("Sign in").click();
            page.locator("form input[type='email']").fill("admin@example.com");
            page.locator(".btnSubmit").click();
        }
    }

    @DisplayName("Locating elements using CSS")
    @Nested
    class LocateElementsByCss {
        @BeforeEach
        void openContactPage() {
            page.navigate("https://practicesoftwaretesting.com/contact");
        }

        @DisplayName("By id")
        @Test
        void locateTheFirstNameFieldByID() {
            page.locator("#first_name").fill("John");
            PlaywrightAssertions.assertThat(page.locator("#first_name")).hasValue("John");
        }

        @DisplayName("By CSS class")
        @Test
        void locateTheSendButtonByCSSClass() {
            page.locator("#first_name").fill("John");
            page.locator(".btnSubmit").click();
            List<String> alertMessages = page.locator(".alert").allTextContents();
            Assertions.assertTrue(!alertMessages.isEmpty());
        }

        @DisplayName("By attribute")
        @Test
        void locateTheSendButtonByAttribute() {
            page.locator("input[placeholder='Your last name *']").fill("Test");
            PlaywrightAssertions.assertThat(page.locator("#last_name")).hasValue("Test");

        }
    }
}
