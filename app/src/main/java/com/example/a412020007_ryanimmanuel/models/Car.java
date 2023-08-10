package com.example.a412020007_ryanimmanuel.models;

public class Car {
    public String imageUrl;
    public String name;
    public String year;
    public String totalSeat;
    public String transmission;
    public Boolean withDriver;
    public Boolean notWIthDriver;
    public String dailyPrice;
    public String dailyPriceWithoutDriver;
    public String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Car() {}

    public Car(String imageUrl,
               String name,
               String year,
               String totalSeat,
               String transmission,
               Boolean withDriver,
               Boolean notWithDriver,
               String dailyPrice,
               String dailyPriceWithoutDriver) {

        this.imageUrl = imageUrl;
        this.name = name;
        this.year = year;
        this.totalSeat = totalSeat;
        this.transmission = transmission;
        this.withDriver = withDriver;
        this.notWIthDriver = notWithDriver;
        this.dailyPrice = dailyPrice;
        this.dailyPriceWithoutDriver = dailyPriceWithoutDriver;
    }
}
