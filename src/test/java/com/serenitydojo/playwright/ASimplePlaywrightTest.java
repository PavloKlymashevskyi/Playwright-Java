package com.serenitydojo.playwright;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ASimplePlaywrightTest {
    @Test
    public void shouldShowThePageTitle() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch();
        Page page = browser.newPage();

        page.navigate("https://practicesoftwaretesting.com/");

        String title = page.title();

        Assertions.assertTrue(title.contains("Practice Software Testing "));

        browser.close();
        playwright.close();
    }

    @Test
    public void shouldSearchByKeyword() {
        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch();
        Page page = browser.newPage();

        page.navigate("https://practicesoftwaretesting.com/");
        page.locator("#search-query").fill("Pliers");
        page.locator("button:has-text('Search')").click();

        int matchingSearchResults = page.locator(".card").count();
        Assertions.assertTrue(matchingSearchResults > 0);

        browser.close();
        playwright.close();
    }
}
