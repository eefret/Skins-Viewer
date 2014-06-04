package com.chrslee.csgopedia.app.util;

/**
 * An Item consists of an item name, image name, and price.
 */
public class Item {
    private String itemName; //Item name
    private int iconID; //ID of png icon
    private String price; //Price of item

    public Item(String itemName, int iconID, String price) {
        super();
        this.itemName = itemName;
        this.iconID = iconID;
        this.price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public int getIconID() {
        return iconID;
    }

    public String getPrice() {
        return price;
    }
}
