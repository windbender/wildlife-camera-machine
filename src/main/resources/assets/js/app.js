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
  'ui.bootstrap.datetimepicker',
  'ui.dateTimeInput',
  'wlcdm.services',
  'wlcdm.directives',
  'wlcdm.controllers',
  'pmkr.partition'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/setup', {templateUrl: '/partials/setup.html', controller: 'SetupController'});
  $routeProvider.when('/userproject/:id', {templateUrl: '/partials/userprojectDetail.html', controller: 'UserProjectDetailController'});
  $routeProvider.when('/invite', {templateUrl: '/partials/invite.html', controller: 'InviteController'});
  $routeProvider.when('/accept', {templateUrl: '/partials/accept.html', controller: 'AcceptController'});
  $routeProvider.when('/account', {templateUrl: '/partials/account.html', controller: 'AccountController'});
  $routeProvider.when('/project/:id', {templateUrl: '/partials/projectDetail.html', controller: 'ProjectDetailController'});
  $routeProvider.when('/cameras/:id', {templateUrl: '/partials/cameraDetail.html', controller: 'CameraDetailController'});
  $routeProvider.when('/upload', {templateUrl: '/partials/upload.html', controller: 'UploadController'});
  $routeProvider.when('/categorize', {templateUrl: '/partials/categorize.html', controller: 'CategorizeController'});
  $routeProvider.when('/categorize/:gridId', {templateUrl: '/partials/categorize.html', controller: 'CategorizeController'});
  $routeProvider.when('/report', {templateUrl: '/partials/report.html', controller: 'ReportController'});
  $routeProvider.when('/bestof', {templateUrl: '/partials/bestof.html', controller: 'BestofController'});
  $routeProvider.when('/signup', {templateUrl: '/partials/signup.html', controller: 'SignupController'});
  $routeProvider.when('/verify', {templateUrl: '/partials/verify.html', controller: 'VerifyController'})
  $routeProvider.when('/createJoin', {templateUrl: '/partials/createOrJoin.html', controller: 'CreateJoinController'})
  $routeProvider.when('/lostpw',{templateUrl: '/partials/lostpw.html', controller:'LostPWController'})
  $routeProvider.when('/resetpw/:token',{templateUrl: 'partials/resetpw.html', controller:'ResetPWController'})

  $routeProvider.when('/info',{templateUrl: '/partials/info.html', controller:'InfoController'})

  $routeProvider.otherwise({redirectTo: '/info'});
}]);
