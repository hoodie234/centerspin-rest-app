package com.centerspin.app;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.json.*;

import com.centerspin.utils.*;

public class ArticleCache {
    
    private List<JSONObject> allArticles;
    
    private final Map<ArticleSearchSpec, List<JSONObject>> queryCache = new ConcurrentHashMap<>();
    
    private final Timer articleUpdateTimer;
    private final int UPDATE_PERIOD = 1000 * 30; // 30 sec
    
    public ArticleCache() {
        allArticles = loadAllArticles();
        articleUpdateTimer = new Timer();
        articleUpdateTimer.schedule(new ArticleUpdater(), UPDATE_PERIOD, UPDATE_PERIOD);
    }
    
    public List<JSONObject> getAllArticles() {
        return allArticles;
    }
    
    public List<JSONObject> getArticles(ArticleSearchSpec searchSpec, int numArticles) {
        
        // Check if query has already been processed and cached
        if (queryCache.containsKey(searchSpec)) {
            return getSublist(queryCache.get(searchSpec), numArticles);
        }
        
        // Copy list of all articles
        List<JSONObject> matchingArticles = new LinkedList<>();
        
        for (JSONObject article : allArticles) {
            
            if (article.getString("type").equals(searchSpec.type) == false && searchSpec.topic.equals(Constants.any) == false) continue;
            if (article.getString("topic").equals(searchSpec.topic) == false && searchSpec.topic.equals(Constants.any) == false) continue;
            
            matchingArticles.add(article);
        }
        
        switch (searchSpec.sortBy) {
            
            case Constants.score:
                Collections.sort(matchingArticles, ArticleComparators.SCORE);
                break;
                
            case Constants.newest:
                Collections.sort(matchingArticles, ArticleComparators.NEWEST_FIRST);
                break;
        }

        // Put full list of articles into cache
        queryCache.put(searchSpec, matchingArticles);
                
        // Return sublist containing requested number of articles
        return getSublist(matchingArticles, numArticles);
    }

    // TODO ---> This may be a good place to use AWS Java SDK because of how big the potential list is
    private List<JSONObject> loadAllArticles() {
        
        List<JSONObject> articles = new LinkedList<>();
        
        try {
            JSONArray articlesArray = new HttpRequest(Constants.API_BASE_URL + "/articles")
                            .setReadTimeout(30 * 1000) // 30 second timeout is LOOOOOONG
                            .get()
                            .toJSONArray();

            // Place all articles into List
            for (int i = 0; i < articlesArray.length(); i++) {
                articles.add(articlesArray.getJSONObject(i));
            }
            
        } catch (IOException e) {
            // WHAT TO DO HERE???? THROW WEB APP EXCEPTION???
        }
        
        return articles;
        
    }
    
    private List<JSONObject> getSublist(List<JSONObject> articles, int numArticles) {
        if (numArticles > articles.size()) numArticles = articles.size();
        return articles.subList(0, numArticles);
    }
    
    private class ArticleUpdater extends TimerTask {
    
        @Override
        public void run() {
          
            allArticles = loadAllArticles();
            
            for (ArticleSearchSpec searchSpec : queryCache.keySet()) {
                List<JSONObject> matchingArticles = getArticles(searchSpec, allArticles.size());
                queryCache.put(searchSpec, matchingArticles);
            }
   
        }
    }
}
