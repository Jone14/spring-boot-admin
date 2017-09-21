/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
'use strict';

var angular = require('angular');
module.exports = function ($rootScope, $scope) {
  'ngInject';

  /*$scope.toggleExpandAll = function () {
    $scope.expandAll = !$scope.expandAll;
    angular.forEach($rootScope.applicationGroups.groups, function (group) {
      group.collapsed = !$scope.expandAll;
    });
  };*/

 /* $scope.order = {
    column: 'name',
    descending: false
  };*/

    $scope.SBApplications = ["Application one", "Application Two"];
    $scope.startOrStop='start';

    $scope.startOrStopApp = function (status) {
        if(status=='start'){
            alert("Application Started");
            $scope.startOrStop='stop';
        }else{
            alert("Application Stopped");
            $scope.startOrStop='start ';
        }
    };




};
