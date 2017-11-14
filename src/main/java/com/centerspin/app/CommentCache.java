package com.centerspin.app;
import com.centerspin.utils.Constants;
import com.centerspin.utils.HttpRequest;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.WebApplicationException;
import org.json.JSONArray;
import org.json.JSONObject;

public class CommentCache {

    private final Map<String, List<JSONObject>> commentMap;
    
    private final Timer commentUpdater;
    private final int THIRTY_SECONDS = 30 * 1000;
    
    public CommentCache() {
        commentMap = new ConcurrentHashMap<>();
        loadAllComments();
        commentUpdater = new Timer();
        commentUpdater.schedule(new CommentUpdater(), THIRTY_SECONDS, THIRTY_SECONDS);
    }
    
    
    
    public List<JSONObject> getCommentsForArticle(String articleID) {
        
        if (commentMap.containsKey(articleID)) {
            return commentMap.get(articleID);
        } else {
            return new LinkedList<>();
        }
        
    }
    
    private void loadAllComments() {
        
        JSONArray allComments;
        try {
            allComments = new HttpRequest(Constants.API_BASE_URL + "/comments").get().toJSONArray();
        } catch (IOException e) {
            throw new WebApplicationException("Error loading comments", e);
        }
        
        for (int i = 0; i < allComments.length(); i++) {
            
            JSONObject comment = allComments.getJSONObject(i);
                        
            String articleID = comment.getString(Constants.articleID);
            
            if (commentMap.containsKey(articleID)) {
                commentMap.get(articleID).add(comment);
            } else {
                
                List<JSONObject> commentsForArticle = new LinkedList<>();
                commentsForArticle.add(comment);
                commentMap.put(articleID, commentsForArticle);
            }
            
        }
        
    }
    
    private class CommentUpdater extends TimerTask {
    
        @Override
        public void run() {
          
            loadAllComments();
            
        }
    }
}
