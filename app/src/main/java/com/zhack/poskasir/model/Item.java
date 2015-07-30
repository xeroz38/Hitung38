package com.zhack.poskasir.model;

/**
 * Created by zunaidi.chandra on 30/07/2015.
 */
public class Item {

    public static final String ITEM_ID = "item_id";
    public static final String ITEM_IMAGE = "item_image";
    public static final String ITEM_TITLE = "item_title";
    public static final String ITEM_CATEGORY = "item_category";
    public static final String ITEM_PRICE = "item_price";

    public static final String[] QUERY_SHORT = {
            ITEM_ID,
            ITEM_IMAGE,
            ITEM_TITLE,
            ITEM_CATEGORY,
            ITEM_PRICE
    };
    public String image;
    public String title;
    public String category;
    public String price;
}
