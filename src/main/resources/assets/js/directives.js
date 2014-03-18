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


app.directive('barChart', function(){
    var chart = d3.custom.barChart();
    return {
        restrict: 'E',
        replace: true,
        template: '<div class="chart"></div>',
        scope:{
            height: '=height',
            data: '=data',
            hovered: '&hovered'
        },
        link: function(scope, element, attrs) {
            var chartEl = d3.select(element[0]);
            chart.on('customHover', function(d, i){
                scope.hovered({args:d});
            });
//            chart.on('myclick', function(d, i){
//                log.console("got a click");
//            });

            scope.$watch('data', function (newVal, oldVal) {
                chartEl.datum(newVal).call(chart);
            });

            scope.$watch('height', function(d, i){
                chartEl.call(chart.height(scope.height));
            })
        }
    }
});