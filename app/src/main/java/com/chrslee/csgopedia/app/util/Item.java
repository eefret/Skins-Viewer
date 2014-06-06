package com.chrslee.csgopedia.app.util;

/**
 * An Item consists of an item name, image name, and price.
 */
public class Item {
    private String itemName; //Item name
    private String description; //Text field under item name
    private int iconID; //ID of png icon
    private String price; //Price of item

    public Item(String itemName, String description, int iconID, String price) {
        super();
        this.itemName = itemName;
        this.iconID = iconID;
        this.price = price;
        this.description = description;
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

    public String getDescription() {
        return description;
    }
}
