package com.centerspin.utils;
import java.util.*;
import org.json.JSONObject;

public class ArticleComparators {

    public static final Comparator<JSONObject> SCORE = new Comparator<JSONObject>()
    {
        @Override
        public int compare(JSONObject article1, JSONObject article2){
            // Return 0 if same
            if(article1.getDouble(Constants.score) == article2.getDouble(Constants.score)) return 0;

            // Return 1 if a1 > a2.  Return -1 if a1 < a2
            return article1.getDouble(Constants.score) > article2.getDouble(Constants.score) ? 1 : -1;
        }
    };
}
