package com.serenitydojo.playwright.toolshop.login;

import com.serenitydojo.playwright.toolshop.domain.User;
import com.serenitydojo.playwright.toolshop.fixtures.PlaywrightTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginWithRegisteredUserTest extends PlaywrightTestCase {

    @Test
    @DisplayName("Should be able to login with registered user")
    void shouldBeLoginWithRegisteredUser() {
        // Register a user via the API

        User user = User.randomUser();

        // Login via login page
        LoginPage loginPage = new LoginPage(page);
        loginPage.open();
        loginPage.loginAs(user);

        // Check that we are the right account
        assertThat(loginPage.title()).isEqualTo("My account");


    }
}
