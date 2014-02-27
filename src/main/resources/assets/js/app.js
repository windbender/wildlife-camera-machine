'use strict';


// Declare app level module which depends on filters, and services
angular.module('wlcdm', [
  'ngRoute',
  'ui.bootstrap',
  'angularFileUpload',
  'wlcdm.filters',
  'wlcdm.services',
  'wlcdm.directives',
  'wlcdm.controllers'
//  'google-maps'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/setup', {templateUrl: 'partials/partial1.html', controller: 'MyCtrl1'});
  $routeProvider.when('/upload', {templateUrl: 'partials/upload.html', controller: 'UploadController'});
  $routeProvider.when('/categorize', {templateUrl: 'partials/categorize.html', controller: 'CategorizeController'});
  $routeProvider.when('/categorize/:gridId', {templateUrl: 'partials/categorize.html', controller: 'CategorizeController'});
  $routeProvider.when('/report', {templateUrl: 'partials/report.html', controller: 'ReportController'});
  $routeProvider.otherwise({redirectTo: '/categorize'});
}]);
