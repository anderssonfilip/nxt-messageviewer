var app = angular.module('nxtApp', [])

app.factory('assetService', ['$http',
    function($http) {

        var doRequest = function() {
            return $http({
                method: 'JSONP',
                url: 'http://127.0.0.1:7876/nxt?requestType=getAllAssets&callback=JSON_CALLBACK'
            });
        }
        return {
            getAllAssets: function() {
                return doRequest();
            }
        };
    }
]);

// Executed after all of the service have been configured and the injector has been created.
// The closest thing in Angular to the main method
app.run(function($rootScope) {

});


app.controller('MyController', ['$scope', '$http', 'assetService',
    function($scope, $http, assetService) {

        $scope.loadAssets = function() {

            assetService.getAllAssets()
                .success(function(data, status, headers) {
                    // the success function wraps the response in data
                    // so we need to call data.data to fetch the raw data
                    console.log(status);
                    $scope.assets = data;
                })
                .error(function(data, status) {
                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                    console.log(status);
                });
        }

        var updateClock = function() {
            $scope.clock = new Date();
        };
        var timer = setInterval(function() {
            $scope.$apply(updateClock);
        }, 1000);
        updateClock();

    }
]);