'use strict';

/* Directives */


var app = angular.module('wlcdm.directives', []);

app.directive('slider', function($timeout) {
	  return {
	    restrict: 'AE',
	    replace: true,
	    scope: {
	      images: '='
	    },
	    link: function(scope, elem, attrs) {
	    	scope.currentIndex = -1; // Initially the index is at the first image
	    	scope.bestOfImg = {};
	    	scope.bestOfImg.imagesrc = '/img/none.png';
	    	scope.next = function() {    	
	    		scope.currentIndex < scope.images.length - 1 ? scope.currentIndex++ : scope.currentIndex = 0;
	    		scope.sizeAndLoad()
	    	};
	    	 
	    	scope.prev = function() {
	    		scope.currentIndex > 0 ? scope.currentIndex-- : scope.currentIndex = scope.images.length - 1;
	    		scope.sizeAndLoad()
	    	};

	    	scope.sizeAndLoad = function() {
	    		var elements = angular.element( document.querySelector( '#slider' ) );
	    		var el = elements[0]
	    		var w = el.clientWidth;
	    		var size = ''+w;
	    		var h = $(window).height();
	    		var mh = (w-1) * 480 / 640;
	    		if(h < mh ) {
	    			// height constrains
	    			w = h * 640 / 480;
	    		} 
	    		
	    		scope.bestOfImg.imagesrc = '/api/images/'+scope.images[scope.currentIndex].id+'?sz='+size;	  		
	    		scope.bestOfImg.width=w-10;
	    		scope.bestOfImg.height=(w-10) *480 / 640;
	    	}
	    	scope.$watch('images', function() {
	    		scope.next();
	    	});
	    	
	    	
	    	function doSomething() {
	    		scope.sizeAndLoad()
			};

			var resizeTimer;
			$(window).resize(function() {
			    clearTimeout(resizeTimer);
			    resizeTimer = setTimeout(doSomething, 100);
			});
	    	
	    	var timer;
	    	var sliderFunc = function() {
	    	  timer = $timeout(function() {
	    	    scope.next();
	    	    timer = $timeout(sliderFunc, 5000);
	    	  }, 5000);
	    	};
	    	 
	    	sliderFunc();
	    	 
	    	scope.$on('$destroy', function() {
	    	  $timeout.cancel(timer); // when the scope is getting destroyed, cancel the timer
	    	});
	    },
	    templateUrl: 'partials/sliderTemplate.html'
	  };
	});


app.directive('focusOn', function() {
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

