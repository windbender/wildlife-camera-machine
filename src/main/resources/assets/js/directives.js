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

