package com.serenitydojo.playwright.examples;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.Comparator;

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

        @Test
        @DisplayName("Should filter products by category")
        void shouldFilterProductsByCategory() {
            page.getByRole(AriaRole.MENUBAR).getByText("Categories").click();
            page.getByRole(AriaRole.MENUBAR).getByText("Power Tools").click();

            page.waitForSelector(".card",
                    new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE)
            );

            var filteredProducts = page.getByTestId("product-name").allInnerTexts();
            Assertions.assertThat(filteredProducts).contains("Sheet Sander", "Belt Sander", "Circular Saw");

        }
    }

    @Nested
    class WaitingForElementsToAppearAndDisappear {
        @BeforeEach
        void openHomePage() {
            page.navigate("https://practicesoftwaretesting.com");
        }

        @Test
        @DisplayName("It should display a toaster message when an item is added to the cart")
        void shouldDisplayToasterMessage() {
            page.getByText("Bolt Cutter").click();
            page.getByText("Add to cart").click();

            // wait for the toaster message to appear
            assertThat(page.getByRole(AriaRole.ALERT)).isVisible();
            assertThat(page.getByRole(AriaRole.ALERT)).hasText("Product added to shopping cart.");

            // check disappear
            // page.waitForSelector(() -> page.getByRole(AriaRole.ALERT).isHidden());


        }

        @Test
        @DisplayName("Should update the cart item count")
        void shouldUpdateCartItemCount() {
            page.getByText("Bolt Cutter").click();
            page.getByText("Add to cart").click();

            page.waitForCondition(() -> page.getByTestId("cart-quantity").textContent().equals("1"));
            page.waitForSelector("[data-test=cart-quantity]:has-text('1')");
        }

    }

    @Nested
    class WaitingForAPICalls {
        @BeforeEach
        void openHomePage() {
            page.navigate("https://practicesoftwaretesting.com");
        }

        @Test
        void sortByDescendingPrice() {

            // Sort by descending
            // https://api.practicesoftwaretesting.com/products?sort=name,asc&between=price,1,100&page=0
//            page.waitForResponse("**/products?sort**",
//                    () -> {
//                        page.getByTestId("sort").selectOption("Price (High - Low)");
//                        //page.getByTestId("product-price").first().waitFor();
//                    });

            page.getByTestId("sort").selectOption("Price (High - Low)");
            page.getByTestId("product-price").first().waitFor();

            // Find all the prices on the page
            var productPrices = page.getByTestId("product-price")
                    .allInnerTexts()
                    .stream()
                    .map(WaitingForAPICalls::extractPrice)
                    .toList();

            // Are the prices in the correct order
            System.out.println("ProductPrices: " + productPrices);
            Assertions.assertThat(productPrices)
                    .isNotEmpty()
                    .isSortedAccordingTo(Comparator.reverseOrder());
        }

        private static double extractPrice(String price) {
            return Double.parseDouble(price.replace("$", ""));
        }
    }
}
