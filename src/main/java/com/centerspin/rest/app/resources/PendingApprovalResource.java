package com.centerspin.rest.app.resources;
import com.centerspin.utils.Constants;
import com.centerspin.utils.GUI;
import java.util.*;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.json.JSONArray;
import org.json.JSONObject;

@Singleton
@Path("pending")
@Produces(MediaType.APPLICATION_JSON)
public class PendingApprovalResource {

    private final Map<String, JSONObject> pendingArticles = new HashMap<>();
    
    @GET
    public String getPendingAricles() {
        
        JSONArray pendingArticlesArray = new JSONArray();
        
        for (JSONObject pendingArticle : pendingArticles.values()) {
            pendingArticlesArray.put(pendingArticle);
        }
        
        return pendingArticlesArray.toString();
    }
    
    @POST
    public String addPendingArticle(String requestBody) {
        
        JSONObject articleData = new JSONObject(requestBody);
        String id = GUI.getNewGUI();
        
        articleData.put(Constants.id, id);
        pendingArticles.put(id, articleData);
        
        JSONObject responseJO = new JSONObject();
        responseJO.put("message", "Your article has been submitted for consideration.");
        
        return responseJO.toString();
    }
    
    @POST
    @Path("approve/{article-id}")
    public String approveArticleSource(@PathParam("article-id") String articleID) {
        
        // Get instance of Submitter resource
        SubmitResource submitter = new SubmitResource();
        
        // Get article w/ given ID
        JSONObject approvedArticle = pendingArticles.remove(articleID);
        
        // Submit article
        submitter.submitArticle(approvedArticle.toString());
        
        // Find all other articles w/ matching source
        List<JSONObject> articlesFromSameSource = new LinkedList<>();
        
        for (JSONObject article : pendingArticles.values()) {
            if (article.getString(Constants.source).equals(approvedArticle.getString(Constants.source))) {
                System.out.println(">>>>>>> Adding article with " + article.getString(Constants.id) + " to match list");
                articlesFromSameSource.add(article);
            }
        }
        
        // Submit these matching articles and remove them from pending list
        for (JSONObject article : articlesFromSameSource) {
            submitter.submitArticle(article.toString());
            pendingArticles.remove(article.getString(Constants.id));
        }
        
        // Add source to "approved" in Dynamo
        
        JSONObject responseJO = new JSONObject();
        responseJO.put("message", "Articles with source " + approvedArticle.getString(Constants.source) + " have been approved and submitted");
        
        return responseJO.toString();
    }
    
    @POST
    @Path("deny/{article-id}")
    public String denyArticleSource(@PathParam("article-id") String articleID) {
        
        JSONObject deniedArticle = pendingArticles.remove(articleID);
        
        // Find all other articles w/ matching source
        List<JSONObject> articlesFromSameSource = new LinkedList<>();
        
        for (JSONObject article : pendingArticles.values()) {
            if (article.getString(Constants.source).equals(deniedArticle.getString(Constants.source))) {
                articlesFromSameSource.add(article);
            }
        }
        
        // Submit these matching articles and remove them from pending list
        for (JSONObject article : articlesFromSameSource) {
            pendingArticles.remove(article.getString(Constants.id));
        }
        
        // Add source to "blacklisted" in Dynamo
        
        JSONObject responseJO = new JSONObject();
        responseJO.put("message", "Source " + deniedArticle.getString(Constants.source) + " has been blacklisted");
        
        return responseJO.toString();
    }
    
}
