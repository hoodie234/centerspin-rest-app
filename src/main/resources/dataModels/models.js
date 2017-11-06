var articleModel = {
    id : "{8 char GUID}",
    timeStamp: 0,
    
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

var biasMetricsModel = {
    contentRating : 0,
    analysisRating : 0,
    contextRating : 0
};


var voteRequestModel = {
    articleID : "",
    userID : "",
    biasMetrics : biasMetricsModel
};


var voteModel = {
    
}
