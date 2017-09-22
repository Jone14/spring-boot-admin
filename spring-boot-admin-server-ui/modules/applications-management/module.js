'use strict';

var angular = require('angular');
var module = angular.module('sba-applications-management', ['sba-core']);
global.sbaModules.push(module.name);
module.controller('appManagementCtrl', require('./controllers/appManagementCtrl.js'));

module.config(function ($stateProvider) {
    $stateProvider.state('appmanagement', {
        url: '/appmanagement',
        templateUrl: 'applications-management/app-management.html',
        controller: 'appManagementCtrl'
    });
});

module.run(function (MainViews) {
    MainViews.register({
        title: 'App-Management',
        state: 'appmanagement',
        order: 200
    });

});