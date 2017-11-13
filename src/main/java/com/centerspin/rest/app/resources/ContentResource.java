package com.centerspin.rest.app.resources;


import com.centerspin.app.ArticleCache;
import com.centerspin.app.ArticleSearchSpec;
import com.centerspin.app.CommentCache;
import com.centerspin.utils.Constants;
import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import org.json.JSONArray;
import org.json.JSONObject;

@Path("content")
@Produces(MediaType.APPLICATION_JSON)
public class ContentResource {

    private final ArticleCache articleCache = new ArticleCache();
    private final CommentCache commentCache = new CommentCache();
    @GET
    @Path("articles/{article-id}")
    public String getArticle(@PathParam("article-id") String articleID) {
        return articleCache.getArticle(articleID).toString();
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
        return new JSONArray(articleCache.getArticles(searchSpec, numArticles)).toString();
    }
    
    @GET
    @Path("comments/{article-id}")
    public String getCommentsForArticle(@PathParam("article-id") String articleID) {
        
        JSONArray articles = new JSONArray(commentCache.getCommentsForArticle(articleID));
        return articles.toString();
    }
    
}
