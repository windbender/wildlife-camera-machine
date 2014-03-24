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
		$http.get('/api/images/nextEvent?lastEvent='+$scope.eventId).success(function(data) {
			if(data == "") return;
			if(data.imageEvent != null) {
				$scope.images = data.imageEvent.imageRecords;
				$scope.eventId = data.imageEvent.id;
				$scope.maxindex = $scope.images.length;
				$scope.currentIndex=0;
				$scope.setImage();
			}
			$scope.remainingToIdentify = data.remainingToIdentify;
			$scope.numberIdentified = data.numberIdentified;
			$scope.percentIdentified = 100.0 *$scope.numberIdentified / ($scope.remainingToIdentify + $scope.numberIdentified);
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
	$scope.submitTyped = function() {
		if(typeof $scope.typeSpecies == 'undefined') return;
		$scope.logAnimal($scope.typeSpecies.name,$scope.typeSpecies.id,$scope.images[$scope.currentIndex].id,$scope.eventId)
		$scope.getNextEvent();
		
		var elements = angular.element( document.querySelector( '#slide' ) );
		var el = elements[0]
		el.focus();
	}

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
			$scope.lastIdentification = data;
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
		if(keyCode == 8) {
			if(typeof $scope.lastIdentification == 'undefined') return;
			// this is backspace... we need this for "oops"
			console.log("oops we need to undo"+$scope.lastIdentification);
			$http.post('/api/images/clearid',$scope.lastIdentification).success(function(data) {
				toastr.success("we successfully cleared your ID ");
				$scope.getNextEvent();
			}).error(function(data, status, headers, config) {
				toastr.error("failed to clear post");
			});
			return false;
		}
		// force upper case
		if(keyCode > 96) keyCode = keyCode - (97-65);
		// right arrow, return, tab
	    if(keyCode == 39  ) {
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
		return $scope.handleKey(key);
	}
	$scope.rOnKeyup = function() {
		var e = window.event;
		if(e.type == "keypress") {
			var kc = e.keyCode;
			return $scope.handleKey(kc);
	    } else if(e.type == "keydown") {
			var kc = e.keyCode;
			if( !$scope.handleKey(kc)) {
				e.preventDefault();
			} else {
				console.log("propagate");
			}
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
    
	});
app.controller('MyCtrl1', [function() {
		// nuffin
  }]);

app.controller('ReportController', ['$scope','$http','$timeout',function($scope,$http,$timeout) {
	$scope.options = {width: 500, height: 300, 'bar': 'aaa'};
	
	$http.get('/api/images/topSpecies').success(function(data) {
		$scope.topSpecies = data;
	}).error(function(data,status,headers,config) {
		toastr.error("sorry unable to retrive list");
	});
	$scope.selectAllSpecies = function() {
		$scope.params.species = [];
	    $scope.params.species.push('all');
	    $scope.onChange();
	}
	$scope.selectNoSpecies = function() {
		$scope.params.species = [];
		$scope.onChange();
	}
	$scope.removeItem = function(array, item) {
		for(var i = array.length - 1; i >= 0; i--) {
		    if(array[i] === item) {
		       array.splice(i, 1);
		    }
		}
	}
	$scope.checkClick = function(species_id) {
		if($scope.isChecked(species_id) ) {
			if($scope.params.species[0] == "all") {
				$scope.removeItem($scope.params.species,"all");
				// now add ALL the rest
				for(var q=0; q < $scope.topSpecies.length; q++) {
					$scope.params.species.push(""+$scope.topSpecies[q].id);
				}
			}
			$scope.removeItem($scope.params.species,""+species_id)
		} else {
			$scope.params.species.push(""+species_id);
		}
		$scope.onChange();
	}
	$scope.isChecked = function(species_id) {
		if($scope.params.species[0] == "all") return true;
		for(var i=0; i < $scope.params.species.length; i++) {
			if($scope.params.species[i] == ""+species_id) {
				return true;
			}
		}
		return false;
	}
	
	$scope.xAxisNumberTickFormat = function(){
	    return function(d){
	    	return d;
	    }
	}
	$scope.xAxisDateTickFormat = function(){
	    return function(d){
	    	var m = moment(d);
	    	if(m.dates() ==1) {
		        return m.format('YYYY/MM/DD');
	    	}
	    	return "";
	    }
	}
	var colorCategory = d3.scale.category20b()
	$scope.colorFunction = function() {
	    return function(d, i) {
	    	if(d[1] == 0) return "#000000";
	    	return "#0088DD";
//	        var q= colorCategory(i);
//	        return q;
	    };
	}
    $scope.bySpeciesData = [];
    $scope.byHourData = [];
    $scope.byDayData = [];
    $scope.reportImg = {};
    $scope.imageEvents = [];
    $scope.reportEventIndex = 0;
    $scope.reportImgIndex = 0;
    $scope.prevEvent = function() {
		$scope.reportEventIndex > 0 ? $scope.reportEventIndex-- : $scope.reportEventIndex = $scope.imageEvents.length-1;
    };
    $scope.nextEvent = function() {
		$scope.reportEventIndex < $scope.imageEvents.length - 1 ? $scope.reportEventIndex++ : $scope.reportEventIndex = 0;
    };
    
    $scope.prevImage = function() {
    	var ar = $scope.imageEvents[$scope.reportEventIndex].imageRecords;
		$scope.reportImgIndex > 0 ? $scope.reportImgIndex-- : $scope.reportImgIndex = ar.length-1;
    };
    $scope.nextImage = function() {
    	var ar = $scope.imageEvents[$scope.reportEventIndex].imageRecords;
		$scope.reportImgIndex < ar.length - 1 ? $scope.reportImgIndex++ : $scope.reportImgIndex = 0;
    };
    
    $scope.map = {
    	    center: {
    	        latitude: 38.5,
    	        longitude: -122.55
    	},
    	zoom: 8
    };	
    
    $scope.showImageControls = function() {
    	if(typeof $scope.imageEvents[$scope.reportEventIndex] == 'undefined') return true;
    	var x = $scope.imageEvents[$scope.reportEventIndex].imageRecords;
    	if(x.length > 1) return true;
    	return false;
    }
    $scope.loadImage = function() {
    	var elements = angular.element( document.querySelector( '#pics' ) );
		var el = elements[0]
		var w = el.clientWidth;
		var size = ''+w;
		if(typeof $scope.imageEvents === 'undefined') return;
		if($scope.imageEvents.length ==0) return;
		$scope.reportImg.imagesrc = '/api/images/'+$scope.imageEvents[$scope.reportEventIndex].imageRecords[$scope.reportImgIndex].id+'?sz='+size;
    }
    $scope.$watch('reportEventIndex', function() {
    	$scope.reportImgIndex = 0;
    	if(typeof $scope.imageEvents === 'undefined' ) return;
    	if(typeof $scope.imageEvents[$scope.reportEventIndex].imageRecords === 'undefined' ) return;
    	$scope.imageLength = $scope.imageEvents[$scope.reportEventIndex].imageRecords.length;
    	$scope.loadImage();
    });
    $scope.$watch('reportImgIndex', function() {
    	$scope.loadImage();
    });

    $scope.params = {};
    $scope.params.projectId = undefined;
    $scope.params.polyGeoRegion = [];
    $scope.params.timeStart = 1356998400;
    $scope.params.timeEnd = 1420070400;
    $scope.params.tod = {}
    $scope.params.tod.DAYTIME = true;
    $scope.params.tod.EVENING = true;
    $scope.params.tod.NIGHTTIME = true;
    $scope.params.tod.MORNING = true;
    $scope.params.species = [];
    $scope.params.species.push('all');
    
    $scope.$watchCollection('params.tod', function() {
    	$scope.onChange();
    });
    $scope.sliderTimeFormatting = function(value) { 
    	var s =moment(value*1000).format("YYYY/MM/DD");
    	return s; 
    }
    
    $scope.$watch('params.timeStart', function() {
    	if(typeof $scope.sliderTimer != 'undefined') {
    		$timeout.cancel($scope.sliderTimer);
    	}
    	$scope.sliderTimer = $timeout(function() {
        	$scope.onChange();
        },200);
    });
    $scope.$watch('params.timeEnd', function() {
    	if(typeof $scope.sliderTimer != 'undefined') {
    		$timeout.cancel($scope.sliderTimer);
    	}
    	$scope.sliderTimer = $timeout(function() {
        	$scope.onChange();
        },200);
    });
    
    
	$scope.onChange = function() {
		$http.post('/api/report',$scope.params).success(function(data) {
			$scope.bySpeciesData = data.bySpeciesData;
			$scope.byHourData = data.byHourData;
			$scope.byDayData = data.byDayData;
			
			$scope.imageEvents = data.imageEvents;
			$scope.reportEventIndex = 0;
		    $scope.reportImgIndex = 0;

			$scope.loadImage();
		}).error(function(data,status,headers,config) {
			toastr.error("sorry unable to retrive list");
		});
	};
    $scope.onChange();
          
  }]);
  
//https://github.com/danialfarid/angular-file-upload  
app.controller('UploadController', ['$scope','$upload','$http',function($scope,$upload,$http) {
	$scope.actualProg = 0;
	$scope.possibleProg = 0;
	
	$http.get('/api/projects/cameras').success(function(data) {
		$scope.cameras = data;
	}).error(function(data,status,headers,config) {
		toastr.error("sorry unable to retrive list");
	});

	
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
	};
	$scope.shouldShowCancel = function() {
		return 0;
	};
	$scope.shouldDisableSelect = function() {
		if( typeof $scope.camera_id == 'undefined') {
			return true;
		}
		return false;
	};
	$scope.onFileSelect = function($files) {
		//$files: an array of files selected, each file has name, size, and type.
		
		$scope.progress = {};
		$scope.uploads = [];
		for (var i = 0; i < $files.length; i++) {
			var file = $files[i];
			var upload = $upload.upload({
				url: '/api/images', //upload.php script, node.js route, or servlet url
		        method: 'POST',
		        headers: {'camera_id': $scope.camera_id,'pos_lat': $scope.lat,'pos_lon': $scope.lon},
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
		      		// so pull it from the 
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
  }]);

app.controller({
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
	MenuController: function($scope, $location, CurUser) {
		$scope.doCollapse = function() {
            $scope.isCollapsed=true;
		};
		$scope.doToggle = function() {
            $scope.isCollapsed = !$scope.isCollapsed;
		};
		$scope.isLoggedIn = function() {
            if(CurUser.getCurUser().username == "(none)") return false;
            if(CurUser.getCurUser().username === null) return false;
            if(CurUser.getCurUser().username === undefined) return false;
            return true;
		};

	}
});
app.controller({
	LogoutController : function($rootScope, $scope,$http, $window, $route, CurUser) {
		$scope.logout = function() {
			$http.post('/api/users/logout','please log me out').success(function() {
				CurUser.setUsername(undefined);
				$window.location.href = "/#/";
				$rootScope.$broadcast('reloadMenus');
			});
		};
		$scope.projects = [];
		$http.get('/api/users/projects').success(function(data) {
			$scope.projects = data;
		});
		$scope.projectChanged = function() {
			$http.post('/api/users/currentProject',$scope.project_id).success(function() {
				$route.reload();
			});
		}
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