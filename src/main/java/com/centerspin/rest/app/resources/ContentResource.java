package com.centerspin.rest.app.resources;


import com.centerspin.app.ArticleCache;
import com.centerspin.app.ArticleSearchSpec;
import com.centerspin.utils.Constants;
import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("content")
@Produces(MediaType.APPLICATION_JSON)
public class ContentResource {

    private final ArticleCache cache = new ArticleCache();
    
    @GET
    @Path("articles/{article-id}")
    public String getArticle(@PathParam("article-id") String articleID) {
        return cache.getArticle(articleID).toString();
    }
    
    @GET
    @Path("articles")
    public String getArticles(@QueryParam(Constants.type) @DefaultValue(Constants.any) String articleType, 
                              @QueryParam(Constants.topic) @DefaultValue(Constants.any) String articleTopic,
                              @QueryParam(Constants.sortBy) @DefaultValue(Constants.newest) String sortBy,
                              @QueryParam(Constants.numArticles) @DefaultValue("10") int numArticles) {
        
        // Assemble search spec
        ArticleSearchSpec searchSpec = new ArticleSearchSpec();
        searchSpec.type = articleType;
        searchSpec.topic = articleTopic;
        searchSpec.sortBy = sortBy;
        
        // Get articles from smart cache
        return cache.getArticles(searchSpec, numArticles).toString();
    }
    
}
