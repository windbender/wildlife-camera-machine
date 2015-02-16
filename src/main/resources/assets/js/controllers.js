/*jshint globalstrict: true*/
/* global angular */
/* global toastr */
/* global document */
/* global window */
/* global moment */
/* global d3 */
/* jshint -W117 */
/* jshint -W083 */


"use strict";

/* Controllers */

var app = angular.module('wlcdm.controllers', []);

app.controller('CategorizeController', function($http, $rootScope, $scope, focus) {

	$scope.currentIndex = 1; // Initially the index is at the second image,
	// but this doesn't actually EXIST! :-)
	$scope.selected = {};
	$scope.typeSpecies = {};
	$scope.next = {};
	$scope.nextnext = {};
	$scope.numberOfAnimals =1;
	$scope.showLoad = false;
	$scope.noMoreImages = false;

	$scope.$on('imageLoadStart', function() {
		$scope.showLoad = true;
		// console.log("start");
	});

	$scope.$on('imageLoadDone', function() {
		$scope.$apply(function() {
			$scope.showLoad = false;
			// console.log("done");
		});
	});

	$scope.getNextEvent = function() {
		$http.get('/api/images/nextEvent?lastEvent='+$scope.eventId).success(function(data) {
			if(data === "") return;
			if(data.imageEvent !== null) {
				$scope.images = data.imageEvent.imageRecords;
				$scope.eventId = data.imageEvent.id;
				$scope.maxindex = $scope.images.length;
				$scope.currentIndex=0;
				$scope.setImage();
			} else {
				$scope.noMoreImages = true;
			}
			$scope.remainingToIdentify = data.remainingToIdentify;
			$scope.numberIdentified = data.numberIdentified;
			$scope.percentIdentified = 100.0 *$scope.numberIdentified / ($scope.remainingToIdentify + $scope.numberIdentified);
		}).error(function(data,status,headers,config) {
			toastr.error("sorry unable to retrive list");
		});
	};

	$scope.getNextEvent();
	$http.get('/api/images/topSpecies?includeNone=true&includeUnknown=true').success(function(data) {
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
		$scope.currentIndex = $scope.currentIndex < $scope.images.length - 1 ? $scope.currentIndex+1 : 0;
	};
	$scope.prevImage = function() {
		$scope.currentIndex = $scope.currentIndex > 0 ? $scope.currentIndex-1 : $scope.images.length - 1;
	};

	$scope.submitTypedPlus = function() {
		if(typeof $scope.typeSpecies == 'undefined') return;
		if(typeof $scope.typeSpecies.id == 'undefined') return;
		if($scope.logAnimal($scope.typeSpecies.name,$scope.typeSpecies.id,$scope.images[$scope.currentIndex].id,$scope.eventId)) {

		}

		var elements = angular.element( document.querySelector( '#slide' ) );
		var el = elements[0];
		el.focus();
	};

	$scope.submitTyped = function() {
		if(typeof $scope.typeSpecies == 'undefined') return;
		if(typeof $scope.typeSpecies.id == 'undefined') return;
		if($scope.logAnimal($scope.typeSpecies.name,$scope.typeSpecies.id,$scope.images[$scope.currentIndex].id,$scope.eventId)) {
			$scope.getNextEvent();
		}

		var elements = angular.element( document.querySelector( '#slide' ) );
		var el = elements[0];
		el.focus();
	};

	$scope.logAnimal = function(speciesname,speciesid,imageid,eventid) {
		if($scope.showLoad) {
			toastr.info("wait until the image shows up before you try to categorize");
			return false;
		}
		console.log("found "+$scope.numberOfAnimals+" of "+speciesname+" on picture "+imageid);
		var idRequest = {
				'numberOfAnimals': $scope.numberOfAnimals,
				'speciesName':speciesname,
				'speciesId':speciesid,
				'imageid':imageid,
				'eventid':eventid
		};
		$http.post('/api/images/identification',idRequest).success(function(data) {
			$scope.lastIdentification = data;
			toastr.options.showDuration = 300;
			toastr.options.hideDuration = 300;
			toastr.options.timeOut = 500;
			// clear the one you just did.
			for(var i=0; i<$scope.species.length; i++) {
				if ($scope.species[i].name == 'none') {
					$scope.typeSpecies = $scope.species[i];
				}
			}
			//$scope.eventId = undefined;

			toastr.success(""+idRequest.numberOfAnimals+" "+idRequest.speciesName);
		}).error(function(data, status, headers, config) {
			toastr.error("failed to post");
		});
		$scope.numberOfAnimals =1;
		return true;
	};

	$scope.getKeyText = function(species) {
		if(species.c == ' ') {
			return "spc";
		} else {
			return species.c;
		}
	};

	$scope.handleKey = function(keyCode) {
		if(keyCode == 9) {
			// tab should behave as expected
			return true;
		} else if(keyCode == 8) {
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
		if(keyCode == 13) {
			// they hit return submit the species.
			$scope.submitTyped();
		}
		// force upper case
		if(keyCode > 96) keyCode = keyCode - (97-65);
		// right arrow, return, tab
		if(keyCode === 39  ) {
			$scope.nextImage();
			// left arrow
		} else if(keyCode == 37 ) {
			$scope.prevImage();
			// numbers
		} else if((keyCode >= 48) && (keyCode <=56)) {
			// numbers
			$scope.numberOfAnimals = keyCode - 48;

			// space
		} else {
			$scope.topSpecies.forEach(function(key) {
				if(key.keycode == keyCode) {
					for(var i=0; i<$scope.species.length; i++) {
						if ($scope.species[i].id == key.id) {
							$scope.typeSpecies = $scope.species[i];
						}
					}
				}
			});
		}
	};

	$scope.speciesButtonPush = function(species) {
		var keyStr = species.c;
		for(var i=0; i<$scope.species.length; i++) {
			if ($scope.species[i].id == species.id) {
				$scope.typeSpecies = $scope.species[i];
			}
		}
		return true;
	};

	$scope.rOnKeyup = function() {
		var e = window.event;
		var kc;
		if(e.srcElement.id == "numIndv") {
			return true;
		}
		if(e.srcElement.id == "typeSel") {
			return true;
		}
		if(e.type == "keypress") {
			kc = e.keyCode;
			return $scope.handleKey(kc);
		} else if(e.type == "keydown") {
			kc = e.keyCode;
			if( !$scope.handleKey(kc)) {
				e.preventDefault();
			} else {
				console.log("propagate");
			}
		}  
	};

	$scope.setImage = function() {
		var elements = angular.element( document.querySelector( '#slide' ) );
		var el = elements[0];
		var w = el.clientWidth;
		var size = ''+w;
		if(typeof $scope.images === 'undefined') return;
		if($scope.images.length ===0) return;
		$scope.selected.imagesrc = '/api/images/'+$scope.images[$scope.currentIndex].id+'?sz='+size;
		$rootScope.$broadcast('imageLoadStart');
		$scope.imagename = $scope.images[$scope.currentIndex].originalFileName;
	};

	$scope.$watch('currentIndex', function() {
		$scope.setImage();
	});

	focus('focusMe');

});

app.controller('InfoController', ['$scope',function($scope) {

}]);


app.controller('AcceptController', ['$scope','$http','$location',function($scope,$http,$location) {
	$scope.code = $location.search().inviteCode;
	$scope.pwdHasNumber = false;
	$http.get('/api/projects/accept?code='+$scope.code).success(function(data) {
		$scope.invite = data;
		$scope.email = data.email;
	}).error(function(data) {
	});
	$scope.submit = function() {
		$http.post('/api/users/accept', {
			username : $scope.username,
			inviteCode : $scope.code,
			password : $scope.password,
		})
		.success(function() {
			toastr.success("signup successful!");
			$location.path('/info');
		}).error(function(data, status, headers, config) {
			toastr.error("there was a problem");
			$scope.msg = data;
		});
	};
	$scope.cancel = function() {

	};
}]);

app.controller('InviteController', ['$scope','$http','$location',function($scope,$http,$location) {
	$scope.invite = function() {
		$http.post('/api/projects/invite',{
			email: $scope.inviteEmail,
			canAdmin: $scope.canAdmin,
			canUpload: $scope.canUpload,
			canCategorize: $scope.canCategorize,
			canReport: $scope.canReport
		}).success(function(data) {
			toastr.success("Invite Sent");
			$location.path('/setup');
		}).error(function(data) {
			toastr.error("Invite Failed");
		});
	};
	$scope.cancel = function() {
		$location.path('/setup');
	};

}]);
app.controller('AccountController', ['$scope','$http',function($scope,$http,CurUser) {
	$scope.curUser = CurUser;

	$http.get('/api/users/myprojects').success(function(data) {
		$scope.projects = data;
	});

}]);

app.factory('Project', function ($resource) {
	var Project = $resource('/api/projects/:projectId', {projectId: '@id'},{update: {method: 'PUT'}});
	Project.prototype.isNew = function(){
		return (typeof(this.id) === 'undefined');
	};
	return Project;
});

app.factory('UserProject', function ($resource) {
	var UserProject = $resource('/api/userproject/:userprojectId', {userprojectId: '@id'},{update: {method: 'PUT'}});
	UserProject.prototype.isNew = function(){
		return (typeof(this.id) === 'undefined');
	};
	return UserProject;
});

app.controller({
	UserProjectDetailController : function($q, $resource, $scope, $http, $routeParams, $location, UserProject) {
		var upId = $routeParams.id;

		$http.get('/api/users/currentProject').success(function(data) {
			$scope.current_project = data;
			$scope.userproject.project = data;
		});

		$scope.userOther = function(val) {
			return $http.get('/api/users/lookup/?text='+val+'&p='+$scope.current_project.id).then(function(res){
				// return res.data;
				return res.data;
			});
		};

		$scope.formatInput=function(user) {
			if(typeof user === 'undefined' ) return "";
			return user.username+" : "+user.email; 
		};

		if (upId === 'new') {
			$scope.userproject = new UserProject();
			$scope.showSave = true;
		} else {
			$scope.userproject = UserProject.get({userprojectId: upId});
			$scope.showSave = false;
		}

		$scope.cancel = function() {
			$location.path('/setup');
		};

		$scope.save = function () {
			if ($scope.userproject.isNew()) {
				$scope.userproject.idForUser = $scope.userproject.user.id;
				$scope.userproject.idForProject = $scope.userproject.project.id;
				$scope.userproject.$save(function (userproject, headers) {
					toastr.success("Created");
					$location.path('/setup');
				});
			} else {
				$scope.userproject.idForUser = $scope.userproject.userId;

				$scope.userproject.$update(function() {
					toastr.success("Updated");
					$location.path('/setup');
				});
			}
		};
	}
});
app.controller({
	ProjectDetailController: function($scope, $rootScope, $routeParams, $location, Project) {
		var projectId = $routeParams.id;

		if (projectId === 'new') {
			$scope.project = new Project();
			$scope.showSave = true;
		} else {
			$scope.project = Project.get({projectId: projectId});
			$scope.showSave = false;
		}

		$scope.cancel = function() {
			$location.path('/account');
		};

		$scope.save = function () {
			if ($scope.project.isNew()) {
				$scope.project.$save(function (project, headers) {
					toastr.success("Created");
					$location.path('/account');
				});
			} else {
				$scope.project.$update(function() {
					toastr.success("Updated");
					$location.path('/account');
				});
			}
			$rootScope.$broadcast('reloadMenus');

		};

	}
});


app.controller('SetupController', ['$scope','$http','$route',function($scope,$http,$route) {
	$http.get('/api/users/currentProject').success(function(data) {
		$scope.current_project = data;
	});

	$scope.deleteUserproject = function(userproject) {
		alert("deleting "+userproject.id);
		$http.delete('/api/userproject/'+userproject.id).success(function(data) {
			$scope.userprojects = data;
		});
		$route.reload();
	};

	$scope.deleteInvite = function(inviteId) {
		$http.post('/api/projects/deleteInvite',inviteId).success(function(data) {
		});
		$route.reload();
	};

	$http.get('/api/cameras').success(function(data) {
		$scope.cameras = data;
	});
	$http.get('/api/userproject').success(function(data) {
		$scope.userprojects = data;
	});
	$http.get('/api/projects/invites').success(function(data) {
		$scope.invites = data;
	});

}]);

app.factory('Camera', function ($resource) {
	var Camera = $resource('/api/cameras/:cameraId', {cameraId: '@id'},{update: {method: 'PUT'}});
	Camera.prototype.isNew = function(){
		return (typeof(this.id) === 'undefined');
	};
	return Camera;
});

app.controller({
	CameraDetailController: function($scope, $routeParams, $location, Camera) {
		var cameraId = $routeParams.id;

		if (cameraId === 'new') {
			$scope.camera = new Camera();
			$scope.showSave = true;
		} else {
			$scope.camera = Camera.get({cameraId: cameraId});
			$scope.showSave = false;
		}

		$scope.cancel = function() {
			$location.path('/setup');
		};

		$scope.save = function () {
			if ($scope.camera.isNew()) {
				$scope.camera.$save(function (camera, headers) {
					toastr.success("Created");
					$location.path('/setup');
				});
			} else {
				$scope.camera.$update(function() {
					toastr.success("Updated");
					$location.path('/setup');
				});
			}
		};
	}

});

app.controller('ReportController', ['$scope','$rootScope','$http','$timeout',function($scope,$rootScope,$http,$timeout) {
	$scope.options = {width: 500, height: 300, 'bar': 'aaa'};

	$scope.showLoad = false;
	$scope.$on('imageReportLoadStart', function() {
		$scope.showLoad = true;
//		console.log("start");
	});

	$scope.$on('imageReportLoadDone', function() {
		$scope.$apply(function() {
			$scope.showLoad = false;
//			console.log("done");
		});
	});

	$http.get('/api/images/topSpecies?includeNone=true&count=-1').success(function(data) {
		$scope.topSpecies = data;
	}).error(function(data,status,headers,config) {
		toastr.error("sorry unable to retrive list");
	});
	
	$scope.selectAllSpecies = function() {
		$scope.params.species = [];
		$scope.params.species.push('all');
		$scope.onChange();
	};
	
	$scope.selectNoSpecies = function() {
		$scope.params.species = [];
		$scope.onChange();
	};
	
	$scope.removeItem = function(array, item) {
		for(var i = array.length - 1; i >= 0; i--) {
			if(array[i] === item) {
				array.splice(i, 1);
			}
		}
	};
	
	$scope.checkClick = function(species_id) {
		if($scope.isChecked(species_id) ) {
			if($scope.params.species[0] == "all") {
				$scope.removeItem($scope.params.species,"all");
				// now add ALL the rest
				for(var q=0; q < $scope.topSpecies.length; q++) {
					$scope.params.species.push(""+$scope.topSpecies[q].id);
				}
			}
			$scope.removeItem($scope.params.species,""+species_id);
		} else {
			$scope.params.species.push(""+species_id);
		}
		$scope.onChange();
	};
	
	$scope.isChecked = function(species_id) {
		if($scope.params.species[0] == "all") return true;
		for(var i=0; i < $scope.params.species.length; i++) {
			if($scope.params.species[i] == ""+species_id) {
				return true;
			}
		}
		return false;
	};

	$scope.xAxisNumberTickFormat = function(){
		return function(d){
			return d;
		};
	};
	
	$scope.xAxisDateTickFormat = function(){
		return function(d){
			var m = moment(d);
			if(m.dates() ==1) {
				return m.format('YYYY/MM/DD');
			}
			return "";
		};
	};
	
	var colorCategory = d3.scale.category20b();
	$scope.colorFunction = function() {
		return function(d, i) {
			// this next thing works for species.  it will need to be generalized for TOD and Date
			// However, I don't know how to force a redraw...so changing to a new event doesn't change the charts
			//if(d[0] == $scope.imageEvents[$scope.reportEventIndex].nameHist[0].name ) return "#DD0000";
			return "#0088DD";
		};
	};
	
	$scope.bySpeciesData = [];
	$scope.byHourData = [];
	$scope.byDayData = [];
	$scope.reportImg = {};
	$scope.imageEvents = [];
	$scope.reportEventIndex = 0;
	$scope.reportImgIndex = 0;
	$scope.prevEvent = function() {
		$scope.needReview = false;
		$scope.reportEventIndex = $scope.reportEventIndex > 0 ? $scope.reportEventIndex-1 : $scope.imageEvents.length-1;
	};
	$scope.nextEvent = function() {
		$scope.needReview = false;
		$scope.reportEventIndex = $scope.reportEventIndex < $scope.imageEvents.length - 1 ? $scope.reportEventIndex+1 : 0;
	};

	$scope.prevImage = function() {
		var ar = $scope.imageEvents[$scope.reportEventIndex].imageEvent.imageRecords;
		$scope.reportImgIndex = $scope.reportImgIndex > 0 ? $scope.reportImgIndex-1 : ar.length-1;

		var imgId = $scope.imageEvents[$scope.reportEventIndex].imageEvent.imageRecords[$scope.reportImgIndex].id;
		var n = $scope.curEventData.goodMap[imgId];
		$scope.isGood = n;
	};
	$scope.nextImage = function() {
		var ar = $scope.imageEvents[$scope.reportEventIndex].imageEvent.imageRecords;
		$scope.reportImgIndex = $scope.reportImgIndex < ar.length - 1 ? $scope.reportImgIndex+1 : 0;

		var imgId = $scope.imageEvents[$scope.reportEventIndex].imageEvent.imageRecords[$scope.reportImgIndex].id;
		var n = $scope.curEventData.goodMap[imgId];
		$scope.isGood = n;
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
		var x = $scope.imageEvents[$scope.reportEventIndex].imageEvent.imageRecords;
		if(x.length > 1) return true;
		return false;
	};
	
	$scope.loadImage = function() {
		var elements = angular.element( document.querySelector( '#pics' ) );
		var el = elements[0];
		var w = el.clientWidth;
		var size = ''+w;
		if(typeof $scope.imageEvents === 'undefined') {
			$scope.reportImg.imagesrc = '/img/none.png';
			return;
		}
		if($scope.imageEvents.length === 0) {
			$scope.reportImg.imagesrc = '/img/none.png';
			return;
		}
		$scope.reportImg.imagesrc = '/api/images/'+$scope.imageEvents[$scope.reportEventIndex].imageEvent.imageRecords[$scope.reportImgIndex].id+'?sz='+size;

		$scope.idedSpecies = $scope.imageEvents[$scope.reportEventIndex].nameHist;

		$rootScope.$broadcast('imageReportLoadStart');
	};

	$scope.loadEventData = function() {
		if(typeof $scope.imageEvents != 'undefined') {
			if(typeof $scope.imageEvents[$scope.reportEventIndex] != 'undefined') {
				if(typeof $scope.imageEvents[$scope.reportEventIndex].imageEvent.id != 'undefined') {
					$http.get('/api/report/event/'+$scope.imageEvents[$scope.reportEventIndex].imageEvent.id).success(function(data) {
						$scope.curEventData = data;
						if($scope.curEventData.flaggedCount > 0) {
							$scope.needReview = true;
						} else {
							$scope.needReview = false;
						}
						var imgId = $scope.imageEvents[$scope.reportEventIndex].imageEvent.imageRecords[$scope.reportImgIndex].id;
						var n = $scope.curEventData.goodMap[imgId];
						$scope.isGood = n;
					});
				}
			}
		}
	};
	
	$scope.sendGood = function() {
		$http.post('/api/report/good',{
			imageId: $scope.imageEvents[$scope.reportEventIndex].imageEvent.imageRecords[$scope.reportImgIndex].id,
			good: $scope.isGood
		}
		).success(function(data) {
			$scope.loadEventData();
		});
	};
	
	$scope.updateGood = function() {
		$timeout($scope.sendGood, 250);
	};
	
	$scope.sendReview = function() {
		$http.post('/api/report/review',{
			eventId: $scope.imageEvents[$scope.reportEventIndex].imageEvent.id,
			review: $scope.needReview
		}
		).success(function(data) {

		});
	};
	
	$scope.updateReview = function() {
		$timeout($scope.sendReview, 250);
	};
	
	$scope.setImageLength = function() {
		if(typeof $scope.reportEventIndex === 'undefined') return;
		if(typeof $scope.imageEvents === 'undefined' ) return;
		if(typeof $scope.imageEvents[$scope.reportEventIndex] === 'undefined' ) return;
		if(typeof $scope.imageEvents[$scope.reportEventIndex].imageEvent.imageRecords === 'undefined' ) return;
		$scope.imageLength = $scope.imageEvents[$scope.reportEventIndex].imageEvent.imageRecords.length;
	};
	
	$scope.$watch('reportEventIndex', function() {
		$scope.reportImgIndex = 0;
		$scope.setImageLength();
		$scope.loadImage();
		$scope.loadEventData();
	});
	$scope.$watch('reportImgIndex', function() {
		$scope.loadImage();
	});

	$scope.params = {};
	$scope.params.showCharts = true;
	$scope.params.projectId = undefined;
	$scope.params.polyGeoRegion = [];
	$scope.params.timeStart = 1380670262;
	$scope.params.timeEnd = 1516577462;
	$scope.params.tod = {};
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
	};

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
		},30);
	});


	$scope.onChange = function() {
		$timeout.cancel($scope.updateTimer);
		$scope.updateTimer = $timeout(function() {
			$scope.doUpdate();
		},100);
	};
	
	$scope.doUpdate = function() {
		$http.post('/api/report',$scope.params).success(function(data) {
			$scope.bySpeciesData = data.bySpeciesData;
			$scope.byHourData = data.byHourData;
			$scope.byDayData = data.byDayData;

			$scope.imageEvents = data.imageEvents;
			$scope.reportEventIndex = 0;
			$scope.reportImgIndex = 0;
			$scope.setImageLength();
			$scope.loadImage();
			$scope.loadEventData();
		}).error(function(data,status,headers,config) {
			toastr.error("sorry unable to retrive list");
		});
	};

}]);

app.controller({
	BestofController: function($rootScope, $scope,$routeParams,$http) {
		$http.get('/api/report/bestimages').success(function(data) {
			$scope.images = data;


		}).error(function(data,status,headers,config) {
			toastr.error("sorry unable to retrive list of best items");
		});


	}
});

app.controller('UploadController', ['$scope','$log','$upload','$http',function($scope,$log,$upload,$http) {
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
	};
	
	$scope.update = function(which, percent) {
		$log.info("updating "+which+"  with "+percent);
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
		// $files: an array of files selected, each file has name, size, and
		// type.
		var i;
		var file;
		
		$scope.progress = {};
		$scope.uploads = [];
		for (i = 0; i < $files.length; i++) {
			file = $files[i];
			var of = function(daFile) {

				var progfunc = function(evt) {
					$scope.update(daFile.name,parseInt(100.0 * evt.loaded / evt.total,10));
				};
				var upload = $upload.upload({
					url: '/api/images', // upload.php script, node.js route, or servlet url
					method: 'POST',
					headers: {'camera_id': $scope.camera_id,'pos_lat': $scope.lat,'pos_lon': $scope.lon},
					file: daFile
				}).progress(progfunc)
				.error(function(data,status,headers,config) {
					toastr.error("sorry can't upload the image because "+data);
				});
				// .then(success, error, progress);
				$scope.uploads.push(upload);
			};
			of(file);

		}
		// alert(" done starting uploads");
	};

	$scope.doAbort = function() {
		for(var i=0; i < $scope.uploads.length; i++) {
			$scope.uploads[i].abort();
		}
	};
	
}]);

app.controller({
	LoginController : function($cookies, $scope, $rootScope, $timeout, $http, authService, CurUser) {
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
				var shortWait = $timeout(function() {
					$rootScope.$broadcast('reloadMenus');
				},500);

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

app.factory('Menu', function($resource) {
	var Menu = $resource('/api/users/menus',
			{}, {
				'query':  {method:'GET', isArray:true}
			});
	return Menu;
});

app.controller({
	MenuController: function($scope, $location, Menu, CurUser) {
		$scope.menus = Menu.query();

		$scope.$on('reloadMenus', function() {
			$scope.menus = Menu.query();
		});

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
		$scope.project_id = 1;

		$scope.$on('reloadMenus', function() {
			$http.get('/api/users/currentProject').success(function(data) {
				$scope.project_id = parseInt(data.id,10);
			});
			$http.get('/api/users/projects').success(function(data) {
				$scope.projects = data;
			});
		});

		$http.get('/api/users/currentProject').success(function(data) {
			$scope.project_id = parseInt(data.id,10);
		});
		$http.get('/api/users/projects').success(function(data) {
			$scope.projects = data;
		});
		$scope.projectChanged = function() {
			$http.post('/api/users/currentProject',$scope.project_id).success(function() {
				$route.reload();
				$rootScope.$broadcast('reloadMenus');

			});
		};
		
		$scope.makeName = function(project) {
			return project.primaryAdmin;
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
		// this keeps the auth provider from intercepting the actual login
		// attempt!
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
	VerifyController: function($scope,$timeout, $rootScope,$location, $http) {
		$scope.code = $location.search().verifyCode;

		$scope.verified = false;
		$scope.failedverify = false;
		$http.post('/api/users/verify', {
			verifyCode : $scope.code
		}).success(function(data) {
			$scope.verified = true;
			$scope.failedverify = false;
			$scope.username = data.username;
			var shortWait = $timeout(function() {
				$location.path('/account');	
				$location.replace();
				$location.search('verifyCode', null);
				$rootScope.$broadcast('reloadMenus');
			},4000);
			$rootScope.$broadcast('reloadMenus');
		}).error(function(data) {
			$scope.failedverify = true;
			toastr.error("sorry unable to verify your account ");
		});
	}
});

app.controller({
	LostPWController: function( $scope,$http) {
		$scope.save = function() {
			// do something with $scope.resetemail
			$http.post("/api/users/lostpw",$scope.resetemail);
		};
	}
});

app.controller({
	ResetPWController: function($rootScope, $scope,$routeParams,$http) {
		$scope.token = $routeParams.token;
		$scope.validToken = false;
		$scope.isGood = false;
		$scope.isBad = false;
		$http.get("/api/users/validtoken/"+$scope.token).success(function(data,status, headers, config) {
			$scope.validToken = true;
			$scope.msg = "Please enter a new password";
		}).error(function(data, status, headers, config) {
			$scope.validToken = false;
			$scope.isBad = true;
		});

		$scope.submit = function() {
			// do something with $scope.resetemail
			$http.post("/api/users/resetpw",{
				token: $scope.token,
				pass: $scope.password
			}).success(function(data,status, headers, config) {
				$scope.validToken = true;
				toastr.info("reset successful");
				$scope.isGood = true;

			}).error(function(data, status, headers, config) {
				$scope.validToken = false;
				toastr.error("reset failed");
				$scope.isBad = true;

			});
		};

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
			};
			
			$http.post('/api/projects/join',joinProjectRequest).success(function(data) {
				toastr.success("request sent");
			}).error(function(data, status, headers, config) {
				toastr.error("failed to post");
			});

		};
		
		$scope.submitCreate = function() {
			var createProjectRequest = {
					'projectName':$scope.projectName,
					'projectDescription':$scope.projectDescription
			};
			$http.post('/api/projects',createProjectRequest).success(function(data) {
				toastr.success("project created");
			}).error(function(data, status, headers, config) {
				toastr.error("failed to post");
			});
		};
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