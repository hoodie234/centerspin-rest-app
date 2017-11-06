package com.centerspin.rest.app.resources;
import com.centerspin.rest.app.ArticleCache;
import com.centerspin.utils.Constants;
import java.util.*;
import javax.ws.rs.*;

@Path("content")
public class ContentResource {

    private final ArticleCache cache = new ArticleCache();
    
    @GET
    @Path("articles")
    public String getArticles(@QueryParam("type") @DefaultValue("") String articleType, 
                              @QueryParam("topic") @DefaultValue("") String articleTopic, 
                              @QueryParam("numArticles") @DefaultValue("1") int numArticles) {
        
        // Assemble parameter map
        Map<String,String> queryParameters = new HashMap<>();
        queryParameters.put(Constants.articleType, articleType);
        queryParameters.put(Constants.articleTopic, articleTopic);
        
        // Get articles from smart cache
        return cache.getArticles(queryParameters, numArticles).toString();
    }
    
}
