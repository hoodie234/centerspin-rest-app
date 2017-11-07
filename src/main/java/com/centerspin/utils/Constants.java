package com.centerspin.utils;
import java.util.*;

public class Constants {

    public static final String message = "message";
    
    public static final String API_BASE_URL = "https://2254w78i64.execute-api.us-west-2.amazonaws.com/dev";
    
    // Article Scan Params
    public static final String articleType = "articleType";
    public static final String articleTopic = "articleTopic";
    public static final String numArticles = "numArticles";
            
    // Used for security against /articles
    public static final String articleSubmitKey = "articleSubmitKey";
    public static final String SECRET_KEY = "KDN0rbGG1rvM0bPXHD2Q";
    
    
    // Primary data types
    public static final String article = "article";
    public static final String vote = "vote";
    
    // For source state
    public static final String sourceState = "sourceState";
    
    // ID & Meta Data
    public static final String id = "id";
    public static final String userID = "userID";
    public static final String articleID = "articleID";
    public static final String timestamp = "timestamp";
    
    // Article Properties
    public static final String url = "url";
    public static final String title = "title";
    public static final String description = "description";
    public static final String source = "source";
    public static final String image = "image";
    
    // Used in vote data
    public static final String articleMetrics = "articleMetrics";
    public static final String before = "before";
    public static final String after = "after";
    
    // Bias metrics
    public static final int MAX_METRIC_VAL = 5;
    public static final String biasMetrics = "biasMetrics";
    public static final String contentRating = "contentRating";
    public static final String analysisRating = "analysisRating";
    public static final String contextRating = "contextRating";
    public static final String[] BIAS_METRIC_KEYS = {contentRating, analysisRating, contextRating};
    
    // Bias Meta Data
    public static final String avgRating = "avgRating";
    public static final String score = "score";
    
}
