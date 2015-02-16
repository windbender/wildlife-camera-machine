/*jshint globalstrict: true*/
/* global angular */
/* jshint -W040 */

"use strict";

/* Services */


//Demonstrate how to register services
//In this case it is a simple value service.
angular.module('wlcdm.services', []).
value('version', '0.1')
.factory('focus', function ($rootScope, $timeout) {
	return function(name) {
		$timeout(function (){
			$rootScope.$broadcast('focusOn', name);
		});
	};
});


angular.module('pmkr.memoize', [])
.factory('pmkr.memoize', 
		[
		function() {
			function service() {
				return memoizeFactory.apply(this, arguments);
			}
			function memoizeFactory(fn) {
				var cache = {};
				function memoized() {
					var args = [].slice.call(arguments);
					var key = JSON.stringify(args);
					var fromCache = cache[key];
					if (fromCache) {
						return fromCache;
					}
					cache[key] = fn.apply(this, arguments);
					return cache[key];
				}
				return memoized;
			} // end service function
			return service;
		}
		]
);

angular.module('pmkr.filterStabilize', ['pmkr.memoize'])
.factory('pmkr.filterStabilize', ['pmkr.memoize',function(memoize) {
	function service(fn) {

		function filter() {
			var args = [].slice.call(arguments);
			// always pass a copy of the args so that the original input can't be modified
			args = angular.copy(args);
			// return the `fn` return value or input reference (makes `fn` return optional)
			var filtered = fn.apply(this, args) || args[0]; 
			return filtered;
		}

		var memoized = memoize(filter);
		return memoized;

	}
	return service;
}
]);

angular.module('pmkr.partition', ['pmkr.filterStabilize'])
.filter('pmkr.partition', ['pmkr.filterStabilize',function(stabilize) {
	var filter = stabilize(function(input, size) {
		if (!input || !size) {
			return input;
		}

		var newArr = [];
		for (var i = 0; i < input.length; i+= size) {
			newArr.push(input.slice(i, i+size));
		}
		return newArr;
	});
	return filter;
}
]);