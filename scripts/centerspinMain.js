angular.module('centerspinApp', ['ngMaterial'])

.controller('centerspinMainCtrl',[ function($scope) {


    // Final schema of article in DB
    $scope.articleModel = {
        id : "{8 char GUID}",
        timestamp: 0,
        
        url : "",
        source : "",
        title : "",
        description : "",
        image : "",
        
        biasMetrics : $scope.biasMetricsModel,
        averageRating: 0,
        score : 0,
        numVotes : 0,
        
        submittedBy : "{userID}"
    };

    // Current article bias schema
    $scope.biasMetricsModel = {
        contentRating : 0,
        analysisRating : 0,
        contextRating : 0
    };

    // Final schema of vote in DB
    $scope.voteModel = {
        
        id : "",
        timestamp: "",
        articleID : "",
        userID : "",
        
        biasMetrics : $scope.biasMetricsModel,
        userBiasMetrics : $scope.biasMetricsModel,
        
        articleMetrics : {
            before : $scope.biasMetricsModel,
            after : $scioe.biasMetricsModel
        }
        
    };

}]);