package com.example.jdbc;

public class Address {
    private int id;
    private String city;
    private String street;
    private String house;


    public Address(String city, String street, String house) {
        this.city = city;
        this.street = street;
        this.house = house;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getHouse() { return house; }
    public void setHouse(String house) { this.house = house; }

    @Override
    public String toString() {
        return city + ", " + street + ", " + house;
    }
}