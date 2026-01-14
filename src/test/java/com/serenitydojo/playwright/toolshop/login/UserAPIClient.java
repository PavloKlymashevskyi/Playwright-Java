package com.serenitydojo.playwright.toolshop.login;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.RequestOptions;
import com.serenitydojo.playwright.toolshop.domain.User;

public class UserAPIClient {
    private final Page page;
    private final String REGISTER_USER = "https://api.practicesoftwaretesting.com/users/register";


    public UserAPIClient(Page page) {
        this.page = page;
    }

    public void registerUser(User user) {
        var response = page.request().post(
                REGISTER_USER,
                RequestOptions.create()
                        .setData(user)
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Accept", "application/json")
        );

        if(response.status() != 201){
            throw new IllegalStateException("Couldn't create user: " + response.text());
        }
    }
}
