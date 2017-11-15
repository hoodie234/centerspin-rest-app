// Used to store sample data models for testing 
angular.module('centerspinModelsApp',[])

.service('modelsService', function(){
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
    var biasMetricsModel = {
        contentRating : 0,
        analysisRating : 0,
        contextRating : 0
    };
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
    }
    this.articleModel = articleModel;
    this.biasMetricsModel = biasMetricsModel;
    this.voteModel = voteModel;
});