'use strict';


// Declare app level module which depends on filters, and services
angular.module('wlcdm', [
  'ngRoute','ngCookies',
  'ngResource',
  'http-auth-interceptor',
  'ui.bootstrap',
  'chieffancypants.loadingBar',
  'angularFileUpload',
  'wlcdm.filters',
  'rzModule',
  'nvd3ChartDirectives',
  'wlcdm.services',
  'wlcdm.directives',
  'wlcdm.controllers',
  'google-maps'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/setup', {templateUrl: '/partials/setup.html', controller: 'SetupController'});
  $routeProvider.when('/userproject/:id', {templateUrl: '/partials/userprojectDetail.html', controller: 'UserProjectDetailController'});
  $routeProvider.when('/account', {templateUrl: '/partials/account.html', controller: 'AccountController'});
  $routeProvider.when('/project/:id', {templateUrl: '/partials/projectDetail.html', controller: 'ProjectDetailController'});
  $routeProvider.when('/cameras/:id', {templateUrl: '/partials/cameraDetail.html', controller: 'CameraDetailController'});
  $routeProvider.when('/upload', {templateUrl: '/partials/upload.html', controller: 'UploadController'});
  $routeProvider.when('/categorize', {templateUrl: '/partials/categorize.html', controller: 'CategorizeController'});
  $routeProvider.when('/categorize/:gridId', {templateUrl: '/partials/categorize.html', controller: 'CategorizeController'});
  $routeProvider.when('/report', {templateUrl: '/partials/report.html', controller: 'ReportController'});
  $routeProvider.when('/signup', {templateUrl: '/partials/signup.html', controller: 'SignupController'});
  $routeProvider.when('/verify', {templateUrl: '/partials/verify.html', controller: 'VerifyController'})
  $routeProvider.when('/createJoin', {templateUrl: '/partials/createOrJoin.html', controller: 'CreateJoinController'})
  $routeProvider.otherwise({redirectTo: '/report'});
}]);
