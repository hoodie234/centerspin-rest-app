package com.centerspin.rest.app.resources;

import com.centerspin.app.ArticleSubmitter;
import com.centerspin.utils.Constants;
import com.centerspin.app.VoteSubmitter;
import java.io.*;
import javax.ws.rs.*;

import org.json.*;

@Path("/submit")
public class SubmitResource {
    
    
    @Path("/article")
    @POST
    public String submitArticle(String requestBody) {
        
        JSONObject articleData = new JSONObject(requestBody);
        JSONObject responseJO = new JSONObject();
        
        try {
            ArticleSubmitter.submitArticle(articleData);
            responseJO.put(Constants.message, "Your article has been successfully submitted");
        } catch (IOException e) {
            responseJO.put(Constants.message, "There was an error submitting your article.  Please try again." + e.getMessage());
        }
        
        return responseJO.toString();
    }
    
    @Path("/vote")
    @POST
    public String submitVote(String requestBody) throws IOException {
        
        JSONObject voteRequest = new JSONObject(requestBody);
        
        VoteSubmitter voteSubmitterThread = new VoteSubmitter(voteRequest);
        voteSubmitterThread.start();
        
        JSONObject responseJO = new JSONObject();
        responseJO.put("message", "Your vote has been submitted");
        
        return responseJO.toString();
        

    }
        
    
}
