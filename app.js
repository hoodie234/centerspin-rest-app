angular.module('centerspinApp', ['ngMaterial', 'centerspinModelsApp','ngRoute'])
.config(function($mdThemingProvider) {
    $mdThemingProvider.theme('default')
      .primaryPalette('indigo')
      .accentPalette('indigo')
      .backgroundPalette('blue-grey')
  })
.config(['$routeProvider', function($routeProvider){
    $routeProvider
    .when('/profile',{templateUrl:'views/profile.html'})
    .when('/submit',{templateUrl:'views/submit.html'})
    .otherwise({redirectTo:'/'});
}])
.controller('centerspinMainCtrl',['$scope','modelsService','$http', function($scope,modelsService,$http) {
    $scope.articleModel = modelsService.articleModel;
    $scope.biasMetricsModel = modelsService.biasMetricsModel;
    $scope.voteModel = modelsService.vodeModel;
    $scope.articleSubmitObj = {url:''};
    $scope.newArticle = null;
    $scope.hasError = false;

    $scope.api = 'http://52.27.217.244:8080/api/v1/scraper';
    $scope.submitArticle = function(){
        $http.post($scope.api, JSON.stringify($scope.articleSubmitObj))
        .then(function(response){
            $scope.newArticle = response.data;
        }, function(response){
            $scope.hasError = true;
        });
    }

}])
