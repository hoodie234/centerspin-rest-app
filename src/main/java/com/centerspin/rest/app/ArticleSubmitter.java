package com.centerspin.rest.app;
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
        articleData.put(Constants.submitTime, new Date().toString());
        
        // Add empty set of bias metrics to articleData
        JSONObject biasMetrics = new JSONObject();
        biasMetrics.put(Constants.accuracy, -1);
        biasMetrics.put(Constants.sources, -1);
        biasMetrics.put(Constants.objectivity, -1);
        biasMetrics.put(Constants.context, -1);
        biasMetrics.put(Constants.avgRating, -1);
        biasMetrics.put(Constants.score, -1);
        
        articleData.put(Constants.biasMetrics, biasMetrics);
        
        JSONObject newArticleRequest = new JSONObject();
        newArticleRequest.put(Constants.article, articleData);
        newArticleRequest.put(Constants.articleSubmitKey, Constants.SECRET_KEY);
                
   
        // TODO --> Look at status code here?
        // TODO --> Return something from method??
        new HttpRequest(Constants.API_BASE_URL + "/articles")
                .setReadTimeout(1000)
                .requestBody(newArticleRequest.toString())
                .post();         

    }
}
