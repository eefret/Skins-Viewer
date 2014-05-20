package com.chrslee.csgopedia.app.util;

/**
 * Created by Christopher on 5/20/2014.
 */
public class Rifle {
    private String rifleType; //Rifle name
    private int iconID; //ID of png icon
    private String price; //Price of rifle

    public Rifle(String rifleType, int iconID, String price) {
        super();
        this.rifleType = rifleType;
        this.iconID = iconID;
        this.price = price;
    }

    public String getRifleType() {
        return rifleType;
    }

    public int getIconID() {
        return iconID;
    }

    public String getPrice() {
        return price;
    }
}
