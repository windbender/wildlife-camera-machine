'use strict';

/* Services */


// Demonstrate how to register services
// In this case it is a simple value service.
angular.module('wlcdm.services', []).
  value('version', '0.1')
  .factory('focus', function ($rootScope, $timeout) {
	  return function(name) {
	    $timeout(function (){
	      $rootScope.$broadcast('focusOn', name);
	    });
	  }
	});
