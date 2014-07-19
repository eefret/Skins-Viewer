## ![app icon](http://i.imgur.com/CExc4lL.png) CS:GO Skins Viewer <a href="https://play.google.com/store/apps/details?id=com.chrslee.csgopedia.app"><img align="right" src="http://developer.android.com/images/brand/en_generic_rgb_wo_45.png" alt="google play link" /></a>

An Android app that serves as a reference guide for all Counter-Strike: Global Offensive skins.

To-do list:

1. Improve the UI for the price display section (CardFragment.java).
2. Find a way to automatically retrieve new skin releases and update the database.
3. Fix spaghetti code residing in ItemsActivity and SpecificItemsActivity, caused by the need to
switch text display fields
(refer to [Item.java](https://github.com/c133/csgopedia/blob/master/app/src/main/java/com/chrslee/csgopedia/app/util/Item.java)
and [item_view.xml](https://github.com/c133/csgopedia/blob/master/app/src/main/res/layout/item_view.xml)).
4. Support only API 14 and up.
5. Make navigation drawer work correctly. Currently, the up navigation button does not open the
drawer, and the drawer indicator (the three horizontal lines next to the app icon) does not "retract"
when the drawer is opened.
6. Add a search function.