'use strict';

/* Controllers */

angular.module('wlcdm.controllers', [])
.controller('CategorizeController', function($http, $scope, focus) {

	$scope.currentIndex = 1; // Initially the index is at the second image, but this doesn't actually EXIST!  :-)
	$scope.selected = {};
	$scope.next = {};
	$scope.nextnext = {};
	$scope.numberOfAnimals =1;

	$http.get('/api/images').success(function(data) {
		$scope.images = data;
		$scope.maxindex = $scope.images.length;
		$scope.currentIndex=0;
	}).error(function(data,status,headers,config) {
		toastr.error("sorry unable to retrive list");
	});
	
	$http.get('/api/images/events').success(function(data) {
		$scope.events = data;
	}).error(function(data,status,headers,config) {
		toastr.error("sorry unable to retrive list");
	});

	$http.get('/api/images/species').success(function(data) {
		$scope.keys = data;
	}).error(function(data,status,headers,config) {
		toastr.error("sorry unable to retrive list");
	});


	$scope.next = function() {
		$scope.currentIndex < $scope.images.length - 1 ? $scope.currentIndex++ : $scope.currentIndex = 0;
	};
	$scope.prev = function() {
		$scope.currentIndex > 0 ? $scope.currentIndex-- : $scope.currentIndex = $scope.images.length - 1;
	};
		
	
//	elem.bind("keyup", function() {
//		scope.$apply(attrs.onkey);
//	});
	

	$scope.logAnimal = function(speciesname,speciesid,imageid) {
		console.log("found "+$scope.numberOfAnimals+" of "+speciesname+" on picture "+imageid);
		var idRequest = {
				'numberOfAnimals': $scope.numberOfAnimals,
				'speciesName':speciesname,
				'speciesId':speciesid,
				'imageid':imageid
		}
		$http.post('api/images/identification',idRequest).success(function(data) {
			toastr.options.showDuration = 300;
			toastr.options.hideDuration = 300;
			toastr.options.timeOut = 500;
			
			toastr.success(""+idRequest.numberOfAnimals+" "+idRequest.speciesName);
		}).error(function(data, status, headers, config) {
			toastr.error("failed to post");
		});
		$scope.numberOfAnimals =1;
		
	};
	
	$scope.handleKey = function(keyCode) {
		if(keyCode > 96) keyCode = keyCode - (97-65);
		// right arrow, return, tab
	     	if(keyCode == 39 || keyCode ==13 || keyCode==9 ) {
	     		$scope.next();
	     	
	     		// left arrow	
	     	} else if(keyCode == 37 ) {
	     		$scope.prev();
	     		
	     	// numbers	
	     	} else if((keyCode >= 48) && (keyCode <=56)) {
			// numbers
			$scope.numberOfAnimals = keyCode - 48;
		
		// space	
		} else if(keyCode == 32) {
			$scope.logAnimal("none",-1,$scope.images[$scope.currentIndex].id);
			$scope.next();
		
		// other keys	
		} else {
    		$scope.keys.forEach(function(key) {
    			if(key.keycode == keyCode) {
    				$scope.logAnimal(key.name,key.id,$scope.images[$scope.currentIndex].id);
    				$scope.next();
    			}
    		});
    		
		}
	};
	$scope.sendKey = function(keyStr) {
		var key = keyStr.charCodeAt(0);
		$scope.handleKey(key);
	}
	$scope.rOnKeyup = function() {
		var e = window.event;
		if(e.type == "keypress") {
			var kc = e.keyCode;
			$scope.handleKey(kc);
	    }
	};
	
	
	
	$scope.$watch('currentIndex', function() {
		var elements = angular.element( document.querySelector( '#slide' ) );
		var el = elements[0]
		var w = el.clientWidth;
		var size = ''+w;
		
		//query the size of "slide"
		// add something on the end of the URL to indicate the size needed.
		$scope.selected.imagesrc = $scope.images[$scope.currentIndex].src+'?sz='+size;
		$scope.imagename = $scope.images[$scope.currentIndex].title;
		// now preload two into the future
//		var next = $scope.currentIndex +1;
//		if(next >= $scope.maxindex) {
//			next =0;
//		}
//		var nextnext = next +1;
//		if(nextnext >= $scope.maxindex) {
//			nextnext =0;
//		}
//		$scope.next.imagesrc = $scope.images[next].src+'?sz='+size;
//		$scope.nextnext.imagesrc = $scope.images[nextnext].src+'?sz='+size;
		
	});
	
    focus('focusMe');

    $scope.map = {
    	    center: {
    	        latitude: 38.5,
    	        longitude: -122.55
    	},
    	zoom: 8
    };	
    
	})
	.controller('MyCtrl1', [function() {
		// nuffin
  }])
  .controller('ReportController', [function() {
		// nuffin
  }])
  
//https://github.com/danialfarid/angular-file-upload  
.controller('UploadController', ['$scope','$upload',function($scope,$upload) {
	$scope.actualProg = 0;
	$scope.possibleProg = 0;
	$scope.updateBar = function() {
		var possibleTotal = 0;
		var actualTotal = 0;
		for(var prog in $scope.progress) {
			actualTotal = actualTotal + $scope.progress[prog];
			possibleTotal = possibleTotal + 100;	
		}
//		for(var i=0; i < $scope.progress.length; i++) {
//			possibleTotal = possibleTotal + 100;
//			actualTotal = actualTotal + $scope.progress[i];
//		}
		$scope.actualProg = actualTotal;
		$scope.possibleProg = possibleTotal;
		
	}
	$scope.update = function(which, percent) {
		$scope.progress[which] = percent;
		$scope.updateBar();
	}
	
	$scope.onFileSelect = function($files) {
		//$files: an array of files selected, each file has name, size, and type.
		$scope.progress = {};
		$scope.uploads = [];
		for (var i = 0; i < $files.length; i++) {
			var file = $files[i];
			var upload = $upload.upload({
				url: '/api/images', //upload.php script, node.js route, or servlet url
		        method: 'POST',
		        // headers: {'headerKey': 'headerValue'},
		        // withCredentials: true,
//		        data: {myObj: $scope.myModelObj},
		        file: file,
//		         file: $files, //upload multiple files, this feature only works in HTML5 FromData browsers
		        /* set file formData name for 'Content-Desposition' header. Default: 'file' */
		        //fileFormDataName: myFile, //OR for HTML5 multiple upload only a list: ['name1', 'name2', ...]
		        /* customize how data is added to formData. See #40#issuecomment-28612000 for example */
		        //formDataAppender: function(formData, key, val){} //#40#issuecomment-28612000
		      	}).progress(function(evt) {
		      		$scope.update(this.file.name,parseInt(100.0 * evt.loaded / evt.total));
		        	console.log('percent: ' + parseInt(100.0 * evt.loaded / evt.total));
		      	}).success(function(data, status, headers, config) {
		        // file is uploaded successfully
		      		console.log(data);
		      	});
		      	//.error(...)
		      	//.then(success, error, progress); 
			$scope.uploads.push(upload);

		}
	};
  }]);

