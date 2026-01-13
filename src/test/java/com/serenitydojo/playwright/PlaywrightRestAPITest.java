package com.serenitydojo.playwright;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;


public class PlaywrightRestAPITest {

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
    class WaitingForState {
        @BeforeEach
        void openHomePage() {
            page.navigate("https://practicesoftwaretesting.com");
            page.waitForSelector(".card-img-top");
        }

        @Test
        void shouldShowAllProductNames() {
            List<String> productNames = page.getByTestId("product-name").allInnerTexts();
            Assertions.assertThat(productNames).contains("Pliers", "Bolt Cutters", "Hammer");
        }

        @Test
        void shouldShowAllProductImages() {
            List<String> productImageTitles = page.locator(".card-img-top").all()
                    .stream()
                    .map(img -> img.getAttribute("alt"))
                    .toList();

            Assertions.assertThat(productImageTitles).contains("Pliers", "Bolt Cutters", "Hammer");
        }
    }
}
