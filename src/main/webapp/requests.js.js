
/*
 * This document outlines the request schema for requests to centerspin servers
 */


// www.centerspin.news/api/submit/article ~ POST
// See 'scraper request' below.  This is scraperRequest.article with addition of "submitted by" user field
var newArticleRequest = {
    url : "",
    source : "",
    title : "",
    description : "",
    image : "",
    submittedBy : "",
}

// www.centerspin.news/api/submit/vote ~ POST
var voteRequestModel = {
    articleID : "",
    userID : "",
    biasMetrics : biasMetricsModel
};

// www.centerspin.news/api/scaper ~ POST
var scraperRequest = {
    url : ""
};
// Response
var scraperResponse = {
    sourceState : "approved", // or 'blacklisted' or 'newSource'
    
    // Article data retuned from /scraper
    article : {
        url : "",
        title : "",
        destription : "",
        source : "",
        image : "",
    }
}