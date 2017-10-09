'use strict';

var angular = require('angular');
module.exports = function ($rootScope, $scope, $http) {
    'ngInject';


    $scope.SBApplications = [{
        "id": "f0bd90d3",
        "name": "spring-boot-application",
        "managementUrl": "http://DIN69000535.corp.capgemini.com:9999/",
        "healthUrl": "http://DIN69000535.corp.capgemini.com:9999/health/",
        "serviceUrl": "http://DIN69000535.corp.capgemini.com:9999/",
        "statusInfo":
            {
                "status": "UP",
                "timestamp": 1506059411182,
                "details":
                    {
                        "diskSpace":
                            {
                                "status": "UP",
                                "total": 250053914624,
                                "free": 231168925696,
                                "threshold": 10485760
                            },
                        "status": "UP"
                    }
            },
        "source": "http-api",
        "metadata": {},
        "info": {}
    }];

    $scope.startOrStop = 'start';


    $scope.getApplication = function () {
        //http://localhost:8080/api/applications/getApplication
        $http.get('/api/applications/getApplication').then(function (response) {
            //alert(response.data);
            $scope.SBApplications = response.data;
            $scope.loadingText = false;
        }).catch(function (response) {
            $scope.error = response.data;
            $scope.loadingText = false;
        });
    };
    $scope.getApplication();
    $scope.manageApp = function (sbApp) {
        $scope.loadingText = true;
        if (sbApp.status == 'SUCCESS') {
            $http.get('/api/applications/stopApplication/' + sbApp.pid + '/' + sbApp.hostUrl).then(function (response) {
                $scope.getApplication();
            }).catch(function (response) {
                $scope.error = response.data;
                $scope.loadingText = false;
            });
        } else {
            $http.get('/api/applications/startApplication/' + sbApp.name + '/' + sbApp.hostUrl).then(function (response) {
                $scope.getApplication();
            }).catch(function (response) {
                $scope.error = response.data;
                $scope.loadingText = false;
            });
        }
    };

    /* $scope.onCategoryChange = function (status) {
         alert(status);
         var filterObj = $scope.SBApplications.filter(function (e) {
             return e.name == status;
         });
         if (filterObj[0].statusInfo.status == "UP") {
             $scope.startOrStop = 'stop';
         } else {
             $scope.startOrStop = 'start';
         }
     };
 */

};