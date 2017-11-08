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
    
    private List<JSONObject> allArticles;
    private final Map<Map<String,String>, List<JSONObject>> queryCache = new ConcurrentHashMap<>();
    
    private final Timer articleUpdateTimer;
    private final int UPDATE_PERIOD = 1000 * 30; // 30 sec
    
    public ArticleCache() {
        long start = System.currentTimeMillis();
        allArticles = loadAllArticles();
        System.out.println(System.currentTimeMillis() - start + "ms");
        articleUpdateTimer = new Timer();
        articleUpdateTimer.schedule(new ArticleUpdater(), UPDATE_PERIOD, UPDATE_PERIOD); //Start immediately
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
            throw new WebApplicationException("Unable to update ArticleCache due to IO Exception: ", e);
        }
        
        return articles;
        
    }
    
    
    private class ArticleUpdater extends TimerTask {
    
        @Override
        public void run() {
          
            allArticles = loadAllArticles();
            
            // TODO ---> Recalculate all cached lists based off of new master list
   
        }
    }
}
