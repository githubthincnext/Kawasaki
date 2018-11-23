package com.software.thincnext.kawasaki.Profile;

public class Bike {

    private String bikeNumber;

    public String getBikeNumber() {
        return bikeNumber;
    }

    public void setBikeNumber(String bikeNumber) {
        this.bikeNumber = bikeNumber;
    }

    public int getBikeImage() {
        return bikeImage;
    }

    public void setBikeImage(int bikeImage) {
        this.bikeImage = bikeImage;
    }

    private int bikeImage;

    public Bike() {
    }

    public Bike(String bikeNumber, int bikeImage) {
        this.bikeNumber = bikeNumber;
        this.bikeImage = bikeImage;

    }


}
