package com.serenitydojo.playwright.login;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.RequestOptions;
import com.serenitydojo.playwright.domain.User;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@UsePlaywright
public class RegisterUserAPITest {

    private APIRequestContext request;

    @BeforeEach
    void setup(Playwright playwright) {
        request = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://api.practicesoftwaretesting.com/api/documentation")
        );
    }

    @AfterEach
    void teardown() {
        if (request != null) {
            request.dispose();
        }
    }

    @Test
    void shouldRegisterUser() {
        User validUser = User.randomUser();

        var response = request.post("/users/register",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(validUser)
        );

        //assertThat(response.status()).isEqualTo(201);

        String responseBody = response.text();
        Gson gson = new Gson();
        User createUser = gson.fromJson(responseBody, User.class);

        //assertThat(createUser).isEqualTo(validUser.withPassword(null));

        JsonObject responseObject = gson.fromJson(responseBody, JsonObject.class);

        // Using SoftAssertions
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.status())
                    .as("Registration should 201 status code")
                    .isEqualTo(201);
            softly.assertThat(createUser)
                    .as("Created user should match the specified user without the password")
                    .isEqualTo(validUser.withPassword(null));

            assertThat(responseObject.has("password"))
                    .as("No password should be returned")
                    .isFalse();

            softly.assertThat(responseObject.get("id").getAsString())
                    .as("Registered user should have an id")
                    .isNotEmpty();

            softly.assertThat(
                    response.headers().get("content-type")
            ).contains("application/json");
        });
    }
}
