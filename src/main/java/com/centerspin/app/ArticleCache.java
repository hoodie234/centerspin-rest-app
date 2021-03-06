package com.centerspin.app;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.json.*;

import com.centerspin.utils.*;
import javax.ws.rs.WebApplicationException;

public class ArticleCache {
    
    private final Map<String, JSONObject> articleIdMap = new ConcurrentHashMap<>();
    private final Map<ArticleSearchSpec, List<JSONObject>> queryCache = new ConcurrentHashMap<>();
    
    private final Timer articleUpdateTimer;
    private final int THIRTY_SECONDS = 1000 * 30; // 30 sec
    
    public ArticleCache() {
        loadAllArticles();
        articleUpdateTimer = new Timer();
        articleUpdateTimer.schedule(new ArticleUpdater(), THIRTY_SECONDS, THIRTY_SECONDS);
    }
    
    public List<JSONObject> getAllArticles() {
        return new LinkedList(articleIdMap.values());
    }
    
    public JSONObject getArticle(String id) {
        return articleIdMap.get(id);
    }
    
    public List<JSONObject> getArticles(ArticleSearchSpec searchSpec, int numArticles) {
        
        // Check if query has already been processed and cached
        if (queryCache.containsKey(searchSpec)) {
            return getSublist(queryCache.get(searchSpec), numArticles);
        }
        
        // Copy list of matching articles
        List<JSONObject> matchingArticles = new LinkedList<>();
        
        for (JSONObject article : articleIdMap.values()) {
                        
            if (searchSpec.type.equals(Constants.any) == false) {
                if (article.getString(Constants.type).equals(searchSpec.type) == false) {
                    continue;
                }
            }
            
            if (searchSpec.topic.equals(Constants.any) == false) {
                if (article.getString(Constants.topic).equals(searchSpec.topic) == false) {
                    continue;
                }
            }
            
            matchingArticles.add(article);
        }
        
        switch (searchSpec.sortBy) {
            
            case Constants.avgRating:
                Collections.sort(matchingArticles, ArticleComparators.AVG_RATING);
                break;
                
            case Constants.newest:
                Collections.sort(matchingArticles, ArticleComparators.NEWEST_FIRST);
                break;
                
            // Add more later
        }

        // Put full list of articles into cache
        queryCache.put(searchSpec, matchingArticles);
                
        // Return sublist containing requested number of articles
        return getSublist(matchingArticles, numArticles);
    }

    private void loadAllArticles() {
                
        try {
            JSONArray articlesArray = new HttpRequest(Constants.API_BASE_URL + "/articles")
                            .get()
                            .toJSONArray();

            // Place all articles into List
            for (int i = 0; i < articlesArray.length(); i++) {
                
                JSONObject article = articlesArray.getJSONObject(i);
                articleIdMap.put(article.getString(Constants.id), article);
            }
            
        } catch (IOException e) {
            throw new WebApplicationException("Error loading all articles from DB", e);
        }
        
   
    }
    
    private List<JSONObject> getSublist(List<JSONObject> articles, int numArticles) {
        if (numArticles > articles.size()) numArticles = articles.size();
        return articles.subList(0, numArticles);
    }
    
    private class ArticleUpdater extends TimerTask {
    
        @Override
        public void run() {
          
            loadAllArticles();
            
            for (ArticleSearchSpec searchSpec : queryCache.keySet()) {
                List<JSONObject> matchingArticles = getArticles(searchSpec, articleIdMap.size());
                queryCache.put(searchSpec, matchingArticles);
            }
   
        }
    }
}
