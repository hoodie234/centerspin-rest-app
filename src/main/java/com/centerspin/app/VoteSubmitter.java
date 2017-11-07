package com.centerspin.app;
import com.centerspin.utils.Constants;
import com.centerspin.utils.GUI;
import com.centerspin.utils.HttpRequest;
import java.io.IOException;
import java.util.*;
import javax.ws.rs.WebApplicationException;
import org.json.JSONArray;
import org.json.JSONObject;

public class VoteSubmitter extends Thread {
        
        private final JSONObject newVoteRequest;
        
        public VoteSubmitter(JSONObject voteRequest) {
            this.newVoteRequest = voteRequest;
        }
        
        @Override
        public void run() throws WebApplicationException {
            
            // Pull user & article id from request
//            String userID = voteRequest.getString(Constants.userID);
            String articleID = newVoteRequest.getString(Constants.articleID);
                        
            JSONObject articleData;
            JSONArray articleVotes;
            
            // Get Article data for given vote
            try {
                articleData = new HttpRequest(Constants.API_BASE_URL + "/articles/" + articleID)
                        .get()
                        .toJSONObject();
            } catch (IOException e) {
                throw new WebApplicationException("Error getting article data to process vote", e);
            }
            
            
            // Get previous votes on same article
            try {
                articleVotes = new HttpRequest(Constants.API_BASE_URL + "/votes/article/" + articleID)
                        .setReadTimeout(2000)
                        .get()
                        .toJSONArray();
            } catch (IOException e) {
                System.out.println("/votes/article error : " + e.getMessage());
                throw new WebApplicationException("Error getting article votes data to process vote", e);
            }
                            
            // Add current vote request to previous votes array (so that it will be part of the tally)
            articleVotes.put(newVoteRequest);
                        
            System.out.println(articleVotes.toString(4));
            
            
            JSONObject articleMetricsSnapshot = new JSONObject();
            // Take snapshot of Article's bias metrics before vote calculation
            
            JSONObject before = new JSONObject(articleData.getJSONObject(Constants.biasMetrics).toString());
                        
            articleMetricsSnapshot.put(Constants.before, before);
            
            // Process all votes
            VoteCalculator.processVotes(articleData, articleVotes);
           
            // Take post-calculation snapshot
            articleMetricsSnapshot.put(Constants.after, articleData.getJSONObject(Constants.biasMetrics));
                        
            // Put Article data snapshot into new Vote
            newVoteRequest.put(Constants.articleMetrics, articleMetricsSnapshot);
            
            // Add addtl. data to vote request
            newVoteRequest.put(Constants.id, GUI.getNewGUI());
            newVoteRequest.put(Constants.timestamp, new Date().toString());
                        
            // Put vote in DB
            try {
                new HttpRequest(Constants.API_BASE_URL + "/votes")
                        .requestBody(newVoteRequest.toString())
                        .post();
            } catch (IOException e) {
                System.out.println("Error posting to /votes");
                throw new WebApplicationException("Error submitting vote via API gateway. Will not process vote.", e);
            }
            
            // Update article in DB
            // TODO --> PUT command at /articles
            try {
                
                JSONObject articleUpdateRequest = new JSONObject();
                articleUpdateRequest.put(Constants.article, articleData);
                articleUpdateRequest.put(Constants.articleSubmitKey, Constants.SECRET_KEY);
        
                new HttpRequest(Constants.API_BASE_URL + "/articles")
                        .requestBody(articleUpdateRequest.toString())
                        .setReadTimeout(2000)
                        .post();
            } catch (IOException e) {
                System.out.println("Error updating article " + e.getMessage());
                e.printStackTrace();
                throw new WebApplicationException("Error updating article via API gateway", e);
            }
            
     
        }
    }