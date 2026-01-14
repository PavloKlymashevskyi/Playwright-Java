package com.serenitydojo.playwright.domain;

import com.google.gson.annotations.SerializedName;
import net.datafaker.Faker;

public record Address(
        String street,
        String city,
        String state,
        String country,
        @SerializedName("postal_code")
        String postalCode) {

    public static Address randomAddress() {
        Faker fake = new Faker();
        return new Address(
                fake.address().streetAddress(),
                fake.address().city(),
                fake.address().state(),
                fake.address().country(),
                fake.address().postcode()
        );
    }
}
