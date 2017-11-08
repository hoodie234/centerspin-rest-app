package com.centerspin.rest.app.resources;
import com.centerspin.app.ArticleCache;
import com.centerspin.app.ArticleSearchSpec;
import com.centerspin.utils.Constants;
import java.util.*;
import javax.ws.rs.*;

@Path("content")
public class ContentResource {

    private final ArticleCache cache = new ArticleCache();
    
    @GET
    @Path("articles")
    public String getArticles(@QueryParam(Constants.type) @DefaultValue("") String articleType, 
                              @QueryParam(Constants.topic) @DefaultValue("") String articleTopic,
                              @QueryParam(Constants.sortBy) @DefaultValue(Constants.score) String sortBy,
                              @QueryParam(Constants.numArticles) @DefaultValue("1") int numArticles) {
        
        // Assemble search spec
        ArticleSearchSpec searchSpec = new ArticleSearchSpec();
        searchSpec.type = articleType;
        searchSpec.topic = articleTopic;
        searchSpec.sortBy = sortBy;
        
        // Get articles from smart cache
        return cache.getArticles(searchSpec, numArticles).toString();
    }
    
}
