package com.serenitydojo.playwright.examples;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.serenitydojo.playwright.toolshop.domain.User;
import com.serenitydojo.playwright.toolshop.login.LoginPage;
import com.serenitydojo.playwright.toolshop.login.UserAPIClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class PageObjectTest {
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

    @BeforeEach
    void openHomePage() {
//        // Login via login page // rolland.rath@yahoo.com
//        page.navigate("https://practicesoftwaretesting.com/auth/login");
//        page.getByPlaceholder("Your email").fill("rolland.rath@yahoo.com");
//        page.getByPlaceholder("Your password").fill("!123Test");
//        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click();

        page.navigate("https://practicesoftwaretesting.com");
    }


    @DisplayName("Without Page Objects")
    @Test
    void withoutPageObjects() {
        // Search for pliers
        page.waitForResponse("**/products/search?q=pliers", () -> {
            page.getByPlaceholder("Search").fill("pliers");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
        });
        // Show details page
        page.locator(".card").getByText("Combination Pliers").click();

        // Increase cart quanity
        page.getByTestId("increase-quantity").click();
        page.getByTestId("increase-quantity").click();
        // Add to cart
        page.getByText("Add to cart").click();
        page.waitForCondition(() -> page.getByTestId("cart-quantity").textContent().equals("3"));

        // Open the cart
        page.getByTestId("nav-cart").click();

        // check cart contents
        assertThat(page.locator(".product-title").getByText("Combination Pliers")).isVisible();
        assertThat(page.getByTestId("cart-quantity").getByText("3")).isVisible();
    }


    @DisplayName("Without Page Object")
    @Test
    void withoutPageObject() {
        page.waitForResponse("**/products/search?q=tape", () -> {
            page.getByPlaceholder("Search").fill("tape");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
        });

        List<String> matchingProducts = page.getByTestId("product-name").allInnerTexts();
        Assertions.assertThat(matchingProducts)
                .contains("Tape Measure 7.5m", "Measuring Tape", "Tape Measure 5m");
    }

    @DisplayName("With Page Objects")
    @Test
    void withPageObjects() {
        SearchComponent searchComponent = new SearchComponent(page);
        ProductList productList = new ProductList(page);
        searchComponent.searchBy("tape");

        var matchingProducts = productList.getProductNames();

        Assertions.assertThat(matchingProducts).contains("Tape Measure 7.5m", "Measuring Tape", "Tape Measure 5m");
    }


    @Test
    void withPageObjectsPliers() {
        // login before run
        SearchComponent searchComponent = new SearchComponent(page);
        ProductList productList = new ProductList(page);
        ProductDetails productDetails = new ProductDetails(page);
        NavBar navbar = new NavBar(page);
        CheckoutCart checkoutCart = new CheckoutCart(page);

        searchComponent.searchBy("pliers");
        productList.viewProductDetails("Combination Pliers");

        productDetails.increaseQuantityTo(2);
        productDetails.addToCard();
        navbar.openCart();

        List<CartLineItem> lineItems = checkoutCart.getLineItems();

        Assertions.assertThat(lineItems)
                .hasSize(1)
                .first()
                .satisfies(item -> {
                    Assertions.assertThat(item.title()).contains("Combination Pliers");
                    Assertions.assertThat(item.quantity).isEqualTo(3);
                    Assertions.assertThat(item.total).isEqualTo(item.quantity * item.price());
                });


    }

    class SearchComponent {
        private final Page page;

        SearchComponent(Page page) {
            this.page = page;
        }

        public void searchBy(String keyword) {
            page.waitForResponse("**/products/search?q=" + keyword, () -> {
                page.getByPlaceholder("Search").fill(keyword);
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Search")).click();
            });
        }
    }

    class ProductList {
        private final Page page;

        ProductList(Page page) {
            this.page = page;
        }


        public List<String> getProductNames() {
            return page.getByTestId("product-name").allInnerTexts();
        }

        public void viewProductDetails(String productName) {
            page.locator(".card").getByText(productName).click();
        }
    }

    class ProductDetails {
        private final Page page;

        ProductDetails(Page page) {
            this.page = page;
        }

        public void increaseQuantityTo(int increment) {
            for(int i = 1; i <= increment; i++) {
                page.getByTestId("increase-quantity").click();
            }
        }

        public void     addToCard() {
            // https://api.practicesoftwaretesting.com/carts
            page.waitForResponse(response -> response.url().contains("/carts") && response.request().method().equals("POST"),
                    () -> page.getByText("Add to cart"));
            page.getByText("Add to cart").click();
//            page.waitForCondition(() -> page.getByTestId("cart-quantity").textContent().equals("3"));
        }
    }

    class NavBar {
        private final Page page;
        NavBar(Page page) {
            this.page = page;
        }

        public void openCart() {
            page.getByTestId("nav-cart").click();
        }
    }

    record CartLineItem(String title, int quantity, double price, double total) {}

    class CheckoutCart {
        private final Page page;

        CheckoutCart(Page page) {
            this.page = page;
        }
    public List<CartLineItem> getLineItems() {
        page.locator("app-cart body tr").waitFor();
        return page.locator("app-cart body tr")
            .all()
            .stream()
            .map(
                    row -> {
                        String title = row.getByTestId("product-title").innerText();
                        int quantity = Integer.parseInt(row.getByTestId("product-quantity").inputValue());
                        double price = Double.parseDouble(price(row.getByTestId("product-price").innerText()));
                        double linePrice = Double.parseDouble(price(row.getByTestId("line-price").innerText()));
                        return new CartLineItem(title, quantity, price, linePrice);
                    }
            ).toList();
        }
    }

    private String price(String value) {
        return value.replace("$", "");
    }
}
