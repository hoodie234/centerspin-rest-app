
/*
 * This document outlines the data schema for CenterSpin data types
 */

// Final schema of article in DB
var articleModel = {
    id : "{8 char GUID}",
    timestamp: 0,
    
    url : "",
    source : "",
    title : "",
    description : "",
    image : "",
    
    biasMetrics : biasMetricsModel,
    averageRating: 0,
    score : 0,
    numVotes : 0,
    
    submittedBy : "{userID}"
};

// Current article bias schema
var biasMetricsModel = {
    contentRating : 0,
    analysisRating : 0,
    contextRating : 0
};

// Final schema of vote in DB
var voteModel = {
    
    id : "",
    timestamp: "",
    articleID : "",
    userID : "",
    
    biasMetrics : biasMetricsModel,
    userBiasMetrics : biasMetricsModel,
    
    articleMetrics : {
        before : biasMetricsModel,
        after : biasMetricsModel
    }
    
};
