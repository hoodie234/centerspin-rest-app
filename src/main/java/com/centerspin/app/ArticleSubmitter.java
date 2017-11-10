package com.centerspin.app;
import com.centerspin.utils.Constants;
import com.centerspin.utils.GUI;
import com.centerspin.utils.HttpRequest;
import com.centerspin.utils.HttpResponse;
import java.io.IOException;
import java.util.*;
import javax.ws.rs.WebApplicationException;
import org.json.JSONObject;

public class ArticleSubmitter {

    public static void submitArticle(JSONObject articleData) throws IOException {
        
        // Add new GUI * timestamp
        articleData.put(Constants.id, GUI.getNewGUI());
        articleData.put(Constants.timestamp, System.currentTimeMillis());
        
        // Add empty set of bias metrics to articleData
        JSONObject biasMetrics = new JSONObject();
        biasMetrics.put(Constants.contentRating, -1);
        biasMetrics.put(Constants.analysisRating, -1);
        biasMetrics.put(Constants.contextRating, -1);
        
        // Put in bias metrics and avg score
        articleData.put(Constants.biasMetrics, biasMetrics);
        articleData.put(Constants.avgRating, -1);
        
        JSONObject newArticleRequest = new JSONObject();
        newArticleRequest.put(Constants.article, articleData);
        newArticleRequest.put(Constants.articleSubmitKey, Constants.SECRET_KEY);
                
   
        // TODO --> Look at status code here?
        // TODO --> Return something from method??
        new HttpRequest(Constants.API_BASE_URL + "/articles")
//                .setReadTimeout(1000)
                .requestBody(newArticleRequest.toString())
                .post();         

    }
}
