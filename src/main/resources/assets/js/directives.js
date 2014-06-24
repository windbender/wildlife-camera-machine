'use strict';

/* Directives */




var app = angular.module('wlcdm.directives', [])
	.directive('focusOn', function() {
		   return function(scope, elem, attr) {
		      scope.$on('focusOn', function(e, name) {
		        if(name === attr.focusOn) {
		          elem[0].focus();
		        }
		      });
		   };
		});

app.directive('myDateselect', ['$http', function($http) {
	return {
		restrict: 'E',
		scope: {
			outsideTime: '=date'
		},
		templateUrl: '/partials/dateselect/dateselect.html',
		link: function (scope, element,attrs) {
			
		    scope.$watch('outsideTime', function() {
		    	scope.insideTime = new Date(scope.outsideTime*1000);
		    });
		    scope.$watch('insideTime', function() {
		    	scope.outsideTime = scope.insideTime.getTime()/1000;
		    });

			scope.setNow = function() {
				scope.insideTime = new Date();
			};
			scope.addDay = function(days) {
				var m = moment(scope.insideTime);
				m = m.add('days', days);
				scope.insideTime = m.toDate();
			};
			
		}
	};
}]);


app.directive('imageonload', function($rootScope) {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            element.bind('load', function() {
				scope.$broadcast('imageLoadDone');
            });
        }
    };
});

app.directive('imagereportonload', function($rootScope) {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            element.bind('load', function() {
				scope.$broadcast('imageReportLoadDone');
            });
        }
    };
});

app.directive('authDemoApplication', ['cfpLoadingBar',function(cfpLoadingBar) {
    return {
            restrict : 'C',
            link : function(scope, elem, attrs) {
                    // once Angular is started, remove class:
                    elem.removeClass('waiting-for-angular');

                    var login = elem.find('#login-holder');
                    var main = elem.find('#content');

                    login.hide();

                    scope.$on('event:auth-loginRequired', function() {
                            login.slideDown('slow', function() {
                                    login.show();
                                    main.hide();
                                    cfpLoadingBar.complete();
                            });
                    });
                    scope.$on('event:auth-loginConfirmed', function() {
                            main.show();
                            login.slideUp();
                            login.hide();
                    });
                    scope.$on('event:auth-loginCanceled', function() {
                            main.show();
                            login.slideUp();
                            login.hide();
                    });

            }
    };
}]);

