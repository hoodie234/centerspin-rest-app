package com.centerspin.app.submitters;

import java.io.*;
import org.json.*;
import javax.ws.rs.*;
import com.centerspin.utils.*;

public class CommentSubmitter {

    private String id;
    private String userID;
    private String articleID;
    private String voteID;
    private String timestamp;
    private String text;
    
    public CommentSubmitter id(String id) {
        this.id = id;
        return this;
    }
    
    public CommentSubmitter userID(String userID) {
        this.userID = userID;
        return this;
    }
    
    public CommentSubmitter articleID(String articleID) {
        this.articleID = articleID;
        return this;
    }
    
    public CommentSubmitter voteID(String voteID) {
        this.voteID = voteID;
        return this;
    }
    
    public CommentSubmitter timestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }
    
    public CommentSubmitter text(String text) {
        this.text = text;
        return this;
    }
    
    public void submit() {
        
        JSONObject commentRequest = new JSONObject();
        
        commentRequest.put(Constants.id, id);
        commentRequest.put(Constants.timestamp, timestamp);
        commentRequest.put(Constants.articleID, articleID);
        commentRequest.put(Constants.userID, userID);
        commentRequest.put(Constants.text, text);
        
        if (voteID != null) {
            commentRequest.put(Constants.voteID, voteID);
        }
        
        try {
            new HttpRequest(Constants.API_BASE_URL + "/comments").requestBody(commentRequest.toString()).post();
        } catch (IOException e) {
            throw new WebApplicationException("Error submitting comment to database.", e);
        }
    }
}
