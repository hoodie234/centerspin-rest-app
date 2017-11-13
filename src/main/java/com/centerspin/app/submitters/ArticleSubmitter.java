package com.centerspin.app.submitters;
import com.centerspin.utils.Constants;
import com.centerspin.utils.GUID;
import com.centerspin.utils.HttpRequest;
import com.centerspin.utils.HttpResponse;
import java.io.IOException;
import java.util.*;
import javax.ws.rs.WebApplicationException;
import org.json.JSONObject;

public class ArticleSubmitter {

    public static void submitArticle(JSONObject articleData) throws IOException {
        
        // Add new GUID * timestamp
        articleData.put(Constants.id, GUID.generate());
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
                
   
        // Submit article
        new HttpRequest(Constants.API_BASE_URL + "/articles").requestBody(newArticleRequest.toString()).post();   
       
              
        // Submit comment if present
        String comment = articleData.getString(Constants.comment);
        if (comment != null && !comment.isEmpty()) {

            CommentSubmitter commentSubmitter = new CommentSubmitter()
                    .id(GUID.generate())
                    .articleID(articleData.getString(Constants.id))
                    .userID("user1234")
                    .timestamp(articleData.getString(Constants.timestamp))
                    .text(comment);

            commentSubmitter.submit();

        }
    }
}
