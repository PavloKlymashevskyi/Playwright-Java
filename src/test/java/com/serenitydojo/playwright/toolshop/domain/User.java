package com.serenitydojo.playwright.toolshop.domain;

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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


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

        // dob
        int year = fake.number().numberBetween(1980, 2010);
        int month = fake.number().numberBetween(1, 12);
        int day = fake.number().numberBetween(1, 28);
        LocalDate date = LocalDate.of(year, month, day);
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return new User(
                fake.name().firstName(),
                fake.name().lastName(),
                Address.randomAddress(),
                fake.phoneNumber().phoneNumber(),
                formattedDate,
                "!123Test",
                fake.internet().emailAddress()
        );
    }

    public User withPassword(String password) {
        return new User(
                this.first_name,
                this.last_name,
                this.address,
                this.phone,
                this.dob,
                password,
                this.email
        );
    }

}


