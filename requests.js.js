
/*
 * This document outlines the request schema for requests to centerspin servers
 */


// www.centerspin.news/api/submit/article ~ POST
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