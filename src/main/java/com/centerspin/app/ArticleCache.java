package com.centerspin.app;
import com.centerspin.utils.Constants;
import com.centerspin.utils.ArticleComparators;
import com.centerspin.utils.HttpRequest;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.WebApplicationException;
import org.json.JSONArray;
import org.json.JSONObject;

public class ArticleCache {
    
    private List<JSONObject> allArticles = new LinkedList<>();
    private final Map<Map<String,String>, List<JSONObject>> queryCache = new ConcurrentHashMap<>();
    
    private final Timer articleUpdateTimer;
    private final int UPDATE_PERIOD = 1000 * 30; // 30 sec
    
    public ArticleCache() {
        articleUpdateTimer = new Timer();
        articleUpdateTimer.schedule(new ArticleUpdater(), 0, UPDATE_PERIOD); //Start immediately
    }
    
    public List<JSONObject> getAllArticles() {
        return allArticles;
    }
    
    
    public List<JSONObject> getArticles(Map<String,String> queryParams, int numArticles) {
        
        // Check if query has already been processed and cached
        if (queryCache.containsKey(queryParams)) {
            return queryCache.get(queryParams).subList(0, numArticles);
        }
        
        // Copy list of all articles
        List<JSONObject> matchingArticles = new LinkedList<>();
        
        for (JSONObject article : allArticles) {
            
            boolean isMatch = true;
            
            for (Map.Entry<String, String> queryParam : queryParams.entrySet()) {
                
                if (queryParam.getValue().isEmpty()) continue;
                
                if (article.getString(queryParam.getKey()).equals(queryParam.getValue()) == false) {
                    isMatch = false;
                    break;
                }
                
                if (isMatch) matchingArticles.add(article);
            }
        }
        
        // Custom comparator ranks articles by their score
        Collections.sort(matchingArticles, ArticleComparators.SCORE);
        
        // Put full list of articles into cache
        queryCache.put(queryParams, matchingArticles);
        
        // Return sublist containing requested number of articles
        return matchingArticles.subList(0, numArticles);
    }

  
    private class ArticleUpdater extends TimerTask {
    
        @Override
        public void run() {
            
            try {
                
                // Get all articles currently in DynamoDB 
                // TODO --> TTL keeps them around for only so long
                JSONArray articlesArray = new HttpRequest(Constants.API_BASE_URL + "/articles")
                                    .get()
                                    .toJSONArray();
                
                List<JSONObject> articles = new LinkedList<>();
                
                // Place all articles into List
                for (int i = 0; i < articlesArray.length(); i++) {
                    articles.add(articlesArray.getJSONObject(i));
                }
                
                // Replace master list
                allArticles = articles;
                
                // Recalculate all queried scans to update cache
                for (Map<String,String> queryParamSet : queryCache.keySet()) {
                    getArticles(queryParamSet, 0);
                }
                
            } catch (IOException e) {
                throw new WebApplicationException("Unable to update ArticleCache due to IO Exception: ", e);
            }
        }
    }
}
