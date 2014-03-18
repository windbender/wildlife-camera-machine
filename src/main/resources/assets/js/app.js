'use strict';


// Declare app level module which depends on filters, and services
angular.module('wlcdm', [
  'ngRoute','ngCookies',
  'http-auth-interceptor',
  'ui.bootstrap',
  'chieffancypants.loadingBar',
  'angularFileUpload',
  'wlcdm.filters',
  'nvd3ChartDirectives',
  'wlcdm.services',
  'wlcdm.directives',
  'wlcdm.controllers'
//  'google-maps'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/setup', {templateUrl: '/partials/partial1.html', controller: 'MyCtrl1'});
  $routeProvider.when('/upload', {templateUrl: '/partials/upload.html', controller: 'UploadController'});
  $routeProvider.when('/categorize', {templateUrl: '/partials/categorize.html', controller: 'CategorizeController'});
  $routeProvider.when('/categorize/:gridId', {templateUrl: '/partials/categorize.html', controller: 'CategorizeController'});
  $routeProvider.when('/report', {templateUrl: '/partials/report.html', controller: 'ReportController'});
  $routeProvider.when('/signup', {templateUrl: '/partials/signup.html', controller: 'SignupController'});
  $routeProvider.when('/verify', {templateUrl: '/partials/verify.html', controller: 'VerifyController'})
  $routeProvider.when('/createJoin', {templateUrl: '/partials/createOrJoin.html', controller: 'CreateJoinController'})
  $routeProvider.otherwise({redirectTo: '/categorize'});
}]);
