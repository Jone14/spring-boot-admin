'use strict';

var angular = require('angular');
module.exports = function ($rootScope, $scope, $http) {
    'ngInject';




    $scope.SBApplications = [{"id":"f0bd90d3",
        "name":"spring-boot-application",
        "managementUrl":"http://DIN69000535.corp.capgemini.com:9999/",
        "healthUrl":"http://DIN69000535.corp.capgemini.com:9999/health/",
        "serviceUrl":"http://DIN69000535.corp.capgemini.com:9999/",
        "statusInfo":
            {"status":"UP",
                "timestamp":1506059411182,
                "details":
                    {"diskSpace":
                        {"status":"UP",
                            "total":250053914624,
                            "free":231168925696,
                            "threshold":10485760},
                        "status":"UP"}},
        "source":"http-api",
        "metadata":{},
        "info":{}}];

    $scope.startOrStop='start';


    $scope.getApplication = function(){
        $http.get('/api/applications').then(function (response) {
            //alert(response.data);
            $scope.SBApplications=response.data;
            if(response.data[0].statusInfo.status=="UP"){
                $scope.startOrStop='stop';
            }else{
                $scope.startOrStop='start';
            }
        }).catch(function (response) {
            $scope.error = response.data;
        });
    };
    $scope.getApplication();
    $scope.startOrStopApp = function (status) {
        if(status=='start'){
               $http.get('/api/applications/stopApplication').then(function (response) {
                   alert(response.data);
               }).catch(function (response) {
                   $scope.error = response.data;
               });

            $scope.startOrStop='stop';
        }else{
            $http.get('/api/applications/startApplication').then(function (response) {
                alert(response.data);
            }).catch(function (response) {
                $scope.error = response.data;
            });

            $scope.startOrStop='start ';
        }
    };

    $scope.onCategoryChange = function (status) {
      alert(status);
        var filterObj = $scope.SBApplications.filter(function(e) {
            return e.name == status;
        });
        if(filterObj[0].statusInfo.status=="UP"){
            $scope.startOrStop='stop';
        }else{
            $scope.startOrStop='start';
        }
    };


};