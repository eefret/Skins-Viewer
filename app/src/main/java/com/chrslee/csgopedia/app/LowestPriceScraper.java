package com.chrslee.csgopedia.app;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Scrapes a Steam Marketplace search result for the lowest price.
 * This program takes advantage of the fact that prices are held in a unique span element
 * with style of color:white (<span style=color:white>).
 */
public class LowestPriceScraper {

    public static String getLowestPrice(String query) {

        Document doc;
        // Reconnect until successful
        while (true) {
            try {
                doc = Jsoup.connect("http://steamcommunity.com/market/search?cc=us&q=appid%3A730+" + query)
                        .userAgent("Chrome").timeout(0).get();
                break;
            } catch (IOException e) {
                // Do nothing
            }
        }

        // Get first element containing the price
        Elements spans = doc.select("span[style=color:white]");
        Element first = spans.first();

        if (first != null) {
            // Set lowest to the price of the first item
            // eg: value is "$13.89". Get "13.89".
            Double lowest = Double.parseDouble(first.ownText().substring(1));

            // Find absolute lowest price
            for (Element ele : spans) {
                double price = Double.parseDouble(ele.ownText().substring(1));

                if (price < lowest) {
                    lowest = price;
                }
            }
            // Return price to 2 decimal places
            return "$" + String.format("%.2f", lowest) + " USD";
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(LowestPriceScraper.getLowestPrice("AK-47"));
    }
}
