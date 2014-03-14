'use strict';

/* Controllers */

var app = angular.module('wlcdm.controllers', [])
.controller('CategorizeController', function($http, $scope, focus) {

	$scope.currentIndex = 1; // Initially the index is at the second image, but this doesn't actually EXIST!  :-)
	$scope.selected = {};
	$scope.next = {};
	$scope.nextnext = {};
	$scope.numberOfAnimals =1;

	$scope.getNextEvent = function() {
		$http.get('/api/images/nextEvent').success(function(data) {
			if(data == "") return;
			$scope.images = data.imageRecords;
			$scope.maxindex = $scope.images.length;
			$scope.eventId = data.id;
			$scope.currentIndex=0;
			$scope.setImage();
		}).error(function(data,status,headers,config) {
			toastr.error("sorry unable to retrive list");
		});
	}
	$scope.getNextEvent();
	$http.get('/api/images/topSpecies').success(function(data) {
		$scope.topSpecies = data;
	}).error(function(data,status,headers,config) {
		toastr.error("sorry unable to retrive list");
	});
	$http.get('/api/images/species').success(function(data) {
		$scope.species = data;
	}).error(function(data,status,headers,config) {
		toastr.error("sorry unable to retrive list");
	});


	$scope.nextImage = function() {
		$scope.currentIndex < $scope.images.length - 1 ? $scope.currentIndex++ : $scope.currentIndex = 0;
	};
	$scope.prevImage = function() {
		$scope.currentIndex > 0 ? $scope.currentIndex-- : $scope.currentIndex = $scope.images.length - 1;
	};
		
	
//	elem.bind("keyup", function() {
//		scope.$apply(attrs.onkey);
//	});
	

	$scope.logAnimal = function(speciesname,speciesid,imageid,eventid) {
		console.log("found "+$scope.numberOfAnimals+" of "+speciesname+" on picture "+imageid);
		var idRequest = {
				'numberOfAnimals': $scope.numberOfAnimals,
				'speciesName':speciesname,
				'speciesId':speciesid,
				'imageid':imageid,
				'eventid':eventid
		}
		$http.post('/api/images/identification',idRequest).success(function(data) {
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
	     		$scope.nextImage();
	     	
	     		// left arrow	
	     	} else if(keyCode == 37 ) {
	     		$scope.prevImage();
	     		
	     	// numbers	
	     	} else if((keyCode >= 48) && (keyCode <=56)) {
			// numbers
			$scope.numberOfAnimals = keyCode - 48;
		
		// space	
		} else if(keyCode == 32) {
			$scope.logAnimal("none",-1,$scope.images[$scope.currentIndex].id,$scope.eventId);
			$scope.getNextEvent();
		
		// other keys	
		} else {
    		$scope.topSpecies.forEach(function(key) {
    			if(key.keycode == keyCode) {
    				$scope.logAnimal(key.name,key.id,$scope.images[$scope.currentIndex].id,$scope.eventId);
    				$scope.getNextEvent();
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
	
	$scope.setImage = function() {
		var elements = angular.element( document.querySelector( '#slide' ) );
		var el = elements[0]
		var w = el.clientWidth;
		var size = ''+w;
		if(typeof $scope.images === 'undefined') return;
		if($scope.images.length ==0) return;
		$scope.selected.imagesrc = '/api/images/'+$scope.images[$scope.currentIndex].id+'?sz='+size;
		$scope.imagename = $scope.images[$scope.currentIndex].originalFileName;
	}
	
	$scope.$watch('currentIndex', function() {
		$scope.setImage();
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
		var actualTotal = 0;
		for(var prog in $scope.progress) {
			actualTotal = actualTotal + $scope.progress[prog];
		}
		$scope.actualProg = actualTotal;
		$scope.possibleProg = $scope.uploads.length * 100;
		$scope.actualPercent = 100 *$scope.actualProg / $scope.possibleProg;
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
		        }).success(function(data, status, headers, config) {
		        // file is uploaded successfully
		      		console.log(data);
		      	});
		      	//.error(...)
		      	//.then(success, error, progress); 
			$scope.uploads.push(upload);

		}
		//alert(" done starting uploads");
	};
	
	$scope.doAbort = function() {
		for(var i=0; i < $scope.uploads.length; i++) {
			$scope.uploads[i].abort();
		}
	}
  }])
.controller({
	LoginController : function($cookies, $scope, $rootScope, $http, authService, CurUser) {
		$scope.curUser = CurUser;
		
		$scope.submit = function() {
			$scope.failMsg = "";
			$http.post('/api/users/login', {
				username : $scope.username,
				password : $scope.password
			}).success(function() {
				authService.loginConfirmed();
				$scope.curUser.setUsername($scope.username);
				$scope.failMsg = "";
				$rootScope.$broadcast('reloadMenus');
				$scope.password = "";
			}).error(function(data, status, headers, config) {
				$scope.failMsg = "sorry that user or password invalid";
				$scope.password = "";
			});
		};
		
		$scope.cancel = function() {
			$scope.failMsg = "";
			$rootScope.$broadcast('event:auth-loginCanceled');
		};

	}

});

app.controller({
	LogoutController : function($rootScope, $scope,$http, $window, CurUser) {
		$scope.logout = function() {
			$http.post('/api/users/logout','please log me out').success(function() {
				CurUser.setUsername(undefined);
				$window.location.href = "/#/";
				$rootScope.$broadcast('reloadMenus');
			});
		};
		$scope.gotoLogin = function() {
			$rootScope.$broadcast('event:auth-loginRequired');
		};
		$scope.isLoggedIn = function() {
			if(CurUser.getCurUser().username == "(none)") return false;
			if(CurUser.getCurUser().username === null) return false;
			if(CurUser.getCurUser().username === undefined) return false;
			return true;
		};
		
		$scope.curUser = CurUser;
	}
});


app.config(function(authServiceProvider) {
	authServiceProvider.addIgnoreUrlExpression(function(response) {
		// this keeps the auth provider from intercepting the actual login attempt!
		return response.config.url === "users/login";
	});
});

app.factory('CurUser',function($http) {
	var CurUser = {};
	var myCurUser = {
		username:"(none)",
		landingPage: "/dash"	
	};
	
	$http.get('/api/users/getLoggedIn').success(function(data) {
		myCurUser = data;
	});
	
	CurUser.getLandingPage = function() {
		if(typeof myCurUser.prefs == 'undefined' || myCurUser.pref === null) {
			return "/dash";
		}
		if(typeof myCurUser.prefs.landingPage =='undefined' || myCurUser.prefs.landingPage === null) {
			return "/dash";
		}
		return myCurUser.prefs.landingPage;
	};
	CurUser.getCurUser = function() {
		return myCurUser;
	};
	CurUser.setUsername = function(inp) {
		myCurUser.username = inp;
	};
	CurUser.showHelp = function() {
		return myCurUser.showHelp;
	};
	return CurUser;

});

app.directive('ensureUnique', [ '$http', function($http) {
	return {
		require : 'ngModel',
		link : function(scope, ele, attrs, c) {
			scope.$watch(attrs.ngModel, function() {
				$http({
					method : 'GET',
					url : '/api/users/check',
					params : {
						username : ele.val()
					}
				}).success(function(data, status, headers, cfg) {
					scope.usernameIsUnique = 'invalid';
					c.$setValidity('username', false);
				}).error(function(data, status, headers, cfg) {
					scope.usernameIsUnique = 'valid';
					c.$setValidity('username', true);
				});
			});
		}
	};
} ]);

app.controller({
	SignupController : function($scope, $http, $location) {
		$scope.usernameIsLowercase = 'valid';
		$scope.usernameIsNoSpace = 'valid';
		$scope.usernameIsUnique = 'valid';

		// should be one of
//		BARE,
//		UNITS_ONLY,
//		UNIT_AND_ATTRIBUTES,
//		FULL_SAMPLE;		
		$scope.initialData = "FULL_SAMPLE";
		$scope.submit = function() {
			$http.post('api/users/signup', {
				firstName : $scope.firstName,
				lastName : $scope.lastName,
				mobile : $scope.mobile,
				username : $scope.username,
				email : $scope.email,
				password : $scope.password,
				initialData : $scope.initialData
			})
			.success(function() {
				toastr.success("signup successful!");
				$scope.msg = "Thank you for signing up! Please check your email. You should a message. Please follow the link in the email to complete your signup";
			}).error(function(data, status, headers, config) {
				toastr.error("there was a problem");
				$scope.msg = data;
			});
		};
		
		$scope.cancel = function() {
			window.location.href = "/";
		};
	}
});

app.controller({
	VerifyController: function($scope,$timeout, $location, $http) {
		$scope.code = $location.search()['verifyCode'];
		
		$scope.verified = false;
		$scope.failedverify = false;
		$http.post('/api/users/verify', {
				verifyCode : $scope.code
			}).success(function(data) {
				$scope.verified = true;
				$scope.failedverify = false;
				$scope.username = data.username;
				var shortWait = $timeout(function() {
					$location.path('/createJoin');	
					$location.replace();
					$location.search('verifyCode', null);
				},4000);
				$rootScope.$broadcast('reloadMenus');
			}).error(function(data) {
				$scope.failedverify = true;
				toastr.error("sorry unable to verify your account ");
			});
	}
});
app.controller({
	CreateJoinController: function($scope, $http) {
		$scope.selectedProject = undefined;
		$http.get('/api/projects').success(function(data) {
			$scope.projects = data;
		}).error(function(data,status,headers,config) {
			toastr.error("sorry unable to retrive list");
		});

		$scope.submitJoinRequest = function() {
			var joinProjectRequest = {
					'selectedProject':$scope.selectedProject
			}
			$http.post('/api/projects/join',joinProjectRequest).success(function(data) {
				toastr.success("request sent");
			}).error(function(data, status, headers, config) {
				toastr.error("failed to post");
			});

		}
		$scope.submitCreate = function() {
			var createProjectRequest = {
					'projectName':$scope.projectName,
					'projectDescription':$scope.projectDescription
			}
			$http.post('/api/projects',createProjectRequest).success(function(data) {
				toastr.success("project created");
			}).error(function(data, status, headers, config) {
				toastr.error("failed to post");
			});
		}
	}
});

app.directive('forceLowercase', function() {
	return {
		require : 'ngModel',
		link : function(scope, element, attrs, modelCtrl) {
			var lowercaseit = function(inputValue) {
				if (typeof inputValue == 'undefined')
					return inputValue;
				var lowercase = inputValue.toLowerCase();
				if (lowercase !== inputValue) {
					modelCtrl.$setViewValue(lowercase);
					modelCtrl.$render();
				}
				return lowercase;
			};
			modelCtrl.$parsers.push(lowercaseit);
			lowercaseit(scope[attrs.ngModel]); // capitalize initial value
		}
	};
});

app.directive(
	'passwordValidate',
	function() {
		return {
			require : 'ngModel',
			link : function(scope, elm, attrs, ctrl) {
				ctrl.$parsers.unshift(function(viewValue) {

					scope.pwdValidLength = (viewValue && viewValue.length >= 8 ? 'valid' : undefined);
					
					scope.pwdHasLetter = (viewValue && /[A-z]/.test(viewValue)) ? 'valid' : undefined;
					
					scope.pwdHasNumber = (viewValue && /\d/.test(viewValue)) ? 'valid' : undefined;

					if (scope.pwdValidLength && scope.pwdHasLetter && scope.pwdHasNumber) {
						ctrl.$setValidity('pwd', true);
						return viewValue;
					} else {
						ctrl.$setValidity('pwd', false);
						return undefined;
					}
				});
			}
		};
	});