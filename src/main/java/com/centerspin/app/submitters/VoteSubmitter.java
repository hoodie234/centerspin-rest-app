package com.centerspin.app.submitters;

import com.centerspin.app.VoteCalculator;
import java.io.*;
import org.json.*;
import javax.ws.rs.*;
import com.centerspin.utils.*;


public class VoteSubmitter extends Thread {
        
        private final JSONObject newVoteRequest;
        
        public VoteSubmitter(JSONObject voteRequest) {
            this.newVoteRequest = voteRequest;
        }
        
        @Override
        public void run() throws WebApplicationException {
            
            // Pull user & article id from request
//            String userID = newVoteRequest.getString(Constants.userID);
            String articleID = newVoteRequest.getString(Constants.articleID);
                        
            JSONObject articleData;
            JSONArray articleVotes;
            
            // Get Article data for given vote
            try {
                articleData = new HttpRequest(Constants.API_BASE_URL + "/articles/" + articleID).get().toJSONObject();
            } catch (IOException e) {
                throw new WebApplicationException("Error getting article data to process vote", e);
            }
            
            // Get previous votes on same article
            try {
                articleVotes = new HttpRequest(Constants.API_BASE_URL + "/votes/article/" + articleID).get().toJSONArray();
            } catch (IOException e) {
                throw new WebApplicationException("Error getting all previous votes for article", e);
            }
            
            // TODO ---> Get user data
                            
            // Add current vote request to previous votes array so that it will be part of the tally
            articleVotes.put(newVoteRequest);
                        
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
            
            // Put ID & timestamp into new Vote
            newVoteRequest.put(Constants.id, GUID.generate());
            newVoteRequest.put(Constants.timestamp, String.valueOf(System.currentTimeMillis()));
            
            // Pull comment & submit it
            try {
                String commentText = (String) newVoteRequest.remove(Constants.comment);
                // Submit the comment
                if (!commentText.isEmpty()) {

                    CommentSubmitter commentSubmitter = new CommentSubmitter()
                            .id(GUID.generate())
                            .voteID(newVoteRequest.getString(Constants.id))
                            .articleID(articleID)
                            .userID("user1234")
                            .timestamp(newVoteRequest.getString(Constants.timestamp))
                            .text(commentText);

                    commentSubmitter.start();

                }
            } catch (JSONException e) {
                System.out.println(e.getMessage());
                // ignored
            }
            
   
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
            try {
                
                JSONObject articleUpdateRequest = new JSONObject();
                articleUpdateRequest.put(Constants.article, articleData);
                articleUpdateRequest.put(Constants.articleSubmitKey, Constants.SECRET_KEY);
        
                new HttpRequest(Constants.API_BASE_URL + "/articles")
                        .requestBody(articleUpdateRequest.toString())
                        .post();
            } catch (IOException e) {
                System.out.println("Error updating article " + e.getMessage());
                e.printStackTrace();
                throw new WebApplicationException("Error updating article via API gateway", e);
            }

        }
    }