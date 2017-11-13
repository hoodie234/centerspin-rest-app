package com.centerspin.rest.app.resources;
import com.centerspin.utils.Constants;
import com.centerspin.utils.GUID;
import com.centerspin.utils.HttpRequest;
import java.io.IOException;
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
        String id = GUID.generate();
        
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
        
        // Get that article's source
        String approvedSource = approvedArticle.getString(Constants.source);
        
        // Submit article
        submitter.submitArticle(approvedArticle.toString());
        
        // Find all other articles w/ matching source
        List<JSONObject> articlesFromSameSource = new LinkedList<>();
        
        for (JSONObject article : pendingArticles.values()) {
            if (article.getString(Constants.source).equals(approvedSource)) {
                articlesFromSameSource.add(article);
            }
        }
        
        // Submit these matching articles and remove them from pending list
        for (JSONObject article : articlesFromSameSource) {
            submitter.submitArticle(article.toString());
            pendingArticles.remove(article.getString(Constants.id));
        }
        
        // Update Sources DB table
        JSONObject requestBody = new JSONObject();
        requestBody.put("sourceState", "approved");
        requestBody.put("sourceName", approvedSource);
       
        try {
            new HttpRequest(Constants.API_BASE_URL + "/sources/add")
                .requestBody(requestBody.toString())
                .post();
        } catch (IOException e) {
            throw new WebApplicationException("Error submitting approval of source: " + approvedSource, e);
        }
        
        
        // Send response
        JSONObject responseJO = new JSONObject();
        responseJO.put("message", "Articles with source " + approvedArticle.getString(Constants.source) + " have been approved and submitted");
        
        return responseJO.toString();
    }
    
    @POST
    @Path("deny/{article-id}")
    public String denyArticleSource(@PathParam("article-id") String articleID) {
        
        // Get article w/ given ID
        JSONObject deniedArticle = pendingArticles.remove(articleID);
        
        // Get that article's resource
        String deniedSource = deniedArticle.getString(Constants.source);
        
        // Find all other articles w/ matching source
        List<JSONObject> articlesFromSameSource = new LinkedList<>();
        
        for (JSONObject article : pendingArticles.values()) {
            if (article.getString(Constants.source).equals(deniedSource)) {
                articlesFromSameSource.add(article);
            }
        }
        
        // Remove matching articles from pending list
        for (JSONObject article : articlesFromSameSource) {
            pendingArticles.remove(article.getString(Constants.id));
        }
        
        // Update Sources DB table
        JSONObject requestBody = new JSONObject();
        requestBody.put("sourceState", "blacklisted");
        requestBody.put("sourceName", deniedSource);
       
        try {
            new HttpRequest(Constants.API_BASE_URL + "/sources/add")
                .requestBody(requestBody.toString())
                .post();
        } catch (IOException e) {
            throw new WebApplicationException("Error submitting approval of source: " + deniedSource, e);
        }
        
        // Send response
        JSONObject responseJO = new JSONObject();
        responseJO.put("message", "Source " + deniedArticle.getString(Constants.source) + " has been blacklisted");
        
        return responseJO.toString();
    }
    
}
