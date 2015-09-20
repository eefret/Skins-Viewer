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
        //So here he have a predefined list of knifes that can be pulled
        HashSet<String> knifeList = new HashSet<String>();
        knifeList.add("Bayonet");
        knifeList.add("Butterfly Knife");
        knifeList.add("Flip Knife");
        knifeList.add("Gut Knife");
        knifeList.add("Huntsman Knife");
        knifeList.add("Karambit");
        knifeList.add("M9 Bayonet");

        Document doc; //Used to contain the HTML object from the scrapped web

        // Reconnect until successful and get the source code
        while (true) { //try to connect indefinitely until he gets to break (NEVER DO THIS)
            try {
                //He takes a query in this method and append it to the end of this URI
                //that will basically search the page of the item he passed in the query
                doc = Jsoup.connect("http://steamcommunity.com/market/search?cc=us&q=appid%3A730+" + query)
                        .userAgent("Chrome").timeout(0).get(); //he sets the browser to mimic Chrome
                break;
            } catch (IOException e) {
                // Do nothing
            }
        }


        // Regex to get contents within parens
        // http://stackoverflow.com/a/14584318
        Pattern p = Pattern.compile("\\(([^)]+)\\)"); //this basically takes the text between parenthesis
        //For example "(mytext)" will return "mytext"

        HashMap<String, Double> priceData = new HashMap<String, Double>();

        // Check if it's a regular knife skin
        if (!knifeList.contains(query)) {//if the knife in the query exists in the Predefined List executes this

            // Either normal or StatTrak, and 5 possible quality grades
            for (int i = 0; i < 10; i++) { // executes this block 10 times
                //Here he selects a span tag with an ID built this way "result_(for index)_name"
                //for example <span id="result_1_name" ...
                Element nameEle = doc.select("span#result_" + i + "_name").first();
                //Here he selects a link tag with an ID built this way "resultlink_(for index)>*span[style=color:white]" then he takes all spans inside it with the color white and select the first
                Element priceEle = doc.select("a#resultlink_" + i + " >* " + "span[style=color:white]").first();

                // No more listings
                if (nameEle == null) {//if the first didnt returne anything just break
                    break;
                }

                //take the full name from the first element scrapped
                //that means the tag is like this <span id="result_1_name">NAME</span>
                String fullName = nameEle.ownText();
                String key = "";
                double value = 0.0;

                // TODO: Possible IndexOutOfBoundsException
                String prefix = fullName.substring(0, 8); //take the first 8 chars of the full name
                if (prefix.equals("StatTrak") || prefix.equals("★ StatTr")) {
                    //if in the first 8 letters starts with StatTrak or ★ StatTr
                    key += "ST "; //the key is ST
                } else if (prefix.equals("Souvenir")) {
                    //if in the first 8 letters starts with Souvenir
                    key += "SO "; //the key is SO
                }

                //now he will take the pattern from above (remember the one that takes the text inside parenthesis) and create a metcher with the fullName, that will select all texts inside parenthesis in the fullname
                Matcher m = p.matcher(fullName);

                // Extract text from inside parentheses
                while (m.find()) { //then he iterates over them
                    key += m.group(1); //takes the first subsequence of the match
                    value = Double.parseDouble(priceEle.ownText().substring(1)); // substring(1) to exclude the $ sign
                    //and pulls the price from the second element defined above
                }
                //the he put the price with the name as the key inside this map
                priceData.put(key, value);
            }
        } else {
            //this is pretty much the same as above just that he doesnt know the name of them
            //so he make a second request to open the item page and pull the unknown data
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
        //after any of the above conditions is executed returns the Map with the
        //names and prices as key and value
    }
}
