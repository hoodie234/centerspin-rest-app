package com.centerspin.app;
import com.centerspin.utils.Constants;
import com.centerspin.rest.app.resources.ScraperResource;
import com.centerspin.rest.app.resources.SubmitResource;
import com.centerspin.utils.HttpRequest;
import java.io.IOException;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class Tester {

    private static String TEST_ARTICLE = "http://www.cnn.com/2017/11/07/us/texas-church-shooting/index.html";
    
    private static final ScraperResource scraperResource = new ScraperResource();
    private static final SubmitResource submitResource = new SubmitResource();
    
    public static void main(String[] args) throws IOException, InterruptedException {
        
        ArticleCache cache = new ArticleCache();
        
        List<JSONObject> jo = cache.getAllArticles();
                
        printJsonList(jo);
        
    }
    
    public static void testGetArticleAndUpdate() throws IOException {
        
        JSONObject articleData = new HttpRequest(Constants.API_BASE_URL + "/articles/" + "eFlSkso2")
                                        .setReadTimeout(2000)
                                        .get()
                                        .toJSONObject();
        
        System.out.println(articleData.toString());
        
        articleData.put(Constants.source, "TEST TEST TEST");
        
        JSONObject articleUpdateRequest = new JSONObject();
                articleUpdateRequest.put(Constants.article, articleData);
                articleUpdateRequest.put(Constants.articleSubmitKey, "KDN0rbGG1rvM0bPXHD2Q");
        
                System.out.println(articleUpdateRequest.toString(4));
        new HttpRequest(Constants.API_BASE_URL + "/articles")
                .requestBody(articleUpdateRequest.toString())
                .setReadTimeout(2000)
                .put();
    }
    
    private static void testScrapeAndSubmit() {
        JSONObject requestJO = new JSONObject();
        requestJO.put(Constants.url, TEST_ARTICLE);
        
        JSONObject scraperResponse = new JSONObject(scraperResource.scrapeURL(requestJO.toString()));
                
        JSONObject articleData = scraperResponse.getJSONObject(Constants.article);
        
        String submitResponse = submitResource.submitArticle(articleData.toString());
        
        System.out.println(submitResponse);
    }
    
    private static void testSubmitVote() throws IOException {
        
        /*
            TODO 
                - Implement proper /votes methods for getting article votes, submitting vote etc
        */
        JSONObject requestJO = new JSONObject();
        requestJO.put(Constants.userID, "1");
        requestJO.put(Constants.articleID, "EaqplQPi");
        
        JSONObject biasMetrics = new JSONObject();
        biasMetrics.put(Constants.contentRating, 4);
        biasMetrics.put(Constants.analysisRating, 4);
        biasMetrics.put(Constants.contextRating, 4);
        
        requestJO.put(Constants.biasMetrics, biasMetrics);
                
        String response = submitResource.submitVote(requestJO.toString());
        System.out.println(response);
    }
 
    private static void printJsonList(List<JSONObject> jsonList) {
        
        for (JSONObject jo : jsonList) {
            System.out.println(jo.toString(4));
        }
    }
   
}
