package com.chrslee.csgopedia.app;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scrapes a Steam Marketplace search result for prices.
 * This program takes advantage of the fact that prices are held in a unique span element
 * with style of color:white (<span style=color:white>).
 */
public class PriceScraper {
    public static HashMap<String, Double> getPrices(String query) {
        HashSet<String> knifeList = new HashSet<String>();
        knifeList.add("Bayonet");
        knifeList.add("Butterfly Knife");
        knifeList.add("Flip Knife");
        knifeList.add("Gut Knife");
        knifeList.add("Huntsman");
        knifeList.add("Karambit");
        knifeList.add("M9 Bayonet");

        Document doc;

        // Reconnect until successful and get the source code
        while (true) {
            try {
                doc = Jsoup.connect("http://steamcommunity.com/market/search?cc=us&q=appid%3A730+" + query)
                        .userAgent("Chrome").timeout(0).get();
                break;
            } catch (IOException e) {
                // Do nothing
            }
        }


        // Regex to get contents within parens
        // http://stackoverflow.com/a/14584318
        Pattern p = Pattern.compile("\\(([^)]+)\\)");

        HashMap<String, Double> priceData = new HashMap<String, Double>();

        // Check if it's a regular knife skin
        if (!knifeList.contains(query)) {
            // Either normal or StatTrak, and 5 possible quality grades
            for (int i = 0; i < 10; i++) {
                Element nameEle = doc.select("span#result_" + i + "_name").first();
                Element priceEle = doc.select("a#resultlink_" + i + " >* " + "span[style=color:white]").first();

                // No more listings
                if (nameEle == null) {
                    break;
                }

                String fullName = nameEle.ownText();
                String key = "";
                double value = 0.0;

                // TODO: Possible IndexOutOfBoundsException if weapon name + skin name is <= 5 letters long.
                String prefix = fullName.substring(0, 8);
                if (prefix.equals("StatTrak") || prefix.equals("★ StatTr")) {
                    key += "ST ";
                } else if (prefix.equals("Souvenir")) {
                    key += "SO ";
                }

                Matcher m = p.matcher(fullName);

                // Extract text from inside parentheses
                while (m.find()) {
                    key += m.group(1);
                    value = Double.parseDouble(priceEle.ownText().substring(1)); // substring(1) to exclude the $ sign
                }

                priceData.put(key, value);
            }
        } else {
            // The first result is the regular knife skin.
            Element priceEle = doc.select("a#resultlink_0" + " >* " + "span[style=color:white]").first();
            priceData.put("Factory New", Double.parseDouble(priceEle.ownText().substring(1)));

            // Now get the StatTrak variant (also the first result).
            while (true) {
                try {
                    doc = Jsoup.connect("http://steamcommunity.com/market/search?cc=us&q=appid%3A730+StatTrak+" + query)
                            .userAgent("Chrome").timeout(0).get();
                    break;
                } catch (IOException e) {
                    // Do nothing
                }
            }
            priceEle = doc.select("a#resultlink_0" + " >* " + "span[style=color:white]").first();
            priceData.put("ST Factory New", Double.parseDouble(priceEle.ownText().substring(1)));
        }
        return priceData;
    }
}
