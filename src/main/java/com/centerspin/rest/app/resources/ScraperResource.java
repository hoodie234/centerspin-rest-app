package com.centerspin.rest.app.resources;

import java.io.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.json.*;

import com.centerspin.utils.*;

@Path("/scraper")
@Produces(MediaType.APPLICATION_JSON)
public class ScraperResource {

    /*
        FINISHED AND TESTED 
    */
    
    @POST
    public String scrapeURL(String requestBody) throws WebApplicationException {
        
        // Get Article submission info from request body
        JSONObject articleRequest = new JSONObject(requestBody);
        
        // Get url from request body
        String url = articleRequest.getString(Constants.url);
        
        // Create object to hold JSON data from OpenGraph scraper
        JSONObject articleData;
        
        // Get Article info from OpenGraph/JSOUP page parse
        try {
            articleData = OpenGraph.getArticleData(url);
        } catch (IOException e) {
            throw new WebApplicationException("Error parsing article data from URL provided.", e);
        }
        
        // Get Article source (i.e. www.cnn.com or www.npr.org)
        String articleSource = articleData.getString(Constants.source);
        
        // Create object to hold response from .../scraper/check 
        JSONObject sourcesResponse;
        
        // Check approval state of source using API Gateway
        try {
            String responseBody = new HttpRequest(Constants.API_BASE_URL + "/sources/check")
                    .setQueryParameter("sourceName", articleSource)
                    .get()
                    .getBody();
            
            sourcesResponse = new JSONObject(responseBody);
            
        } catch (IOException e) {
            throw new WebApplicationException("Error checking source state @ /sources/check", e);
        }
        
        // Assemble JSON Response
        JSONObject responseJO = new JSONObject();
        responseJO.put(Constants.article, articleData);
        responseJO.put(Constants.sourceState, sourcesResponse.getString(Constants.sourceState));
        
        return responseJO.toString();
        
    }
    
}
