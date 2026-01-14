package com.serenitydojo.playwright.domain;

// https://api.practicesoftwaretesting.com/api/documentation
//{
//        "first_name": "John",
//        "last_name": "Doe",
//        "address": {
//        "street": "Street 1",
//        "city": "City",
//        "state": "State",
//        "country": "Country",
//        "postal_code": "1234AA"
//        },
//        "phone": "0987654321",
//        "dob": "1970-01-01",
//        "password": "SuperSecure@123",
//        "email": "john@doe.example"
//}

import net.datafaker.Faker;


public record User(
        String first_name,
        String last_name,
        Address address,
        String phone,
        String dob,
        String password,
        String email) {


    public static User randomUser() {
        Faker fake = new Faker();
        return new User(
                fake.name().firstName(),
                fake.name().firstName(),
                Address.randomAddress(),
                fake.phoneNumber().phoneNumber(),
                "1975-01-01",
                "!123Test",
                fake.internet().emailAddress()
        );
    }

}


