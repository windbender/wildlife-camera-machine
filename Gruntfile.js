module.exports = function(grunt) {
    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.initConfig({
        jshint: {
            files: ['Gruntfile.js', 'src/main/resources/assets/js/controllers.js','src/main/resources/assets/js/app.js','src/main/resources/assets/js/directives.js','src/main/resources/assets/js/filters.js','src/main/resources/assets/js/services.js']
        },
        concat: {
		options: {
			separator: '\r\n',
			},
		cssdist: {
			nonull: true,
				src: [
//			'src/main/resources/assets/bower_components/bootstrap/dist/css/bootstrap.min.css',
			'src/main/resources/assets/bower_components/toastr/toastr.min.css',
			'src/main/resources/assets/bower_components/nvd3/nv.d3.css',
			'src/main/resources/assets/bower_components/angularjs-slider/dist/rzslider.css',
			'src/main/resources/assets/bower_components/angular-bootstrap-datetimepicker/src/css/datetimepicker.css',
			'src/main/resources/assets/css/app.css'
			],
			dest: 'src/main/resources/assets/css/concatted.css'
		},
		fontdist: {
			nonull:true,
			src: [ 'src/main/resources/assets/bower_components/bootstrap/dist/fonts/glyphicons-halflings-regular.eot'
			],
			dest: 'src/main/resources/assets/fonts/glyphicons-halflings-regular.eot'
		},
		fontdist2: {
			nonull:true,
			src: [ 'src/main/resources/assets/bower_components/bootstrap/dist/fonts/glyphicons-halflings-regular.svg'
			],
			dest: 'src/main/resources/assets/fonts/glyphicons-halflings-regular.svg'
		},
		fontdist3: {
			nonull:true,
			src: [ 'src/main/resources/assets/bower_components/bootstrap/dist/fonts/glyphicons-halflings-regular.ttf'
			],
			dest: 'src/main/resources/assets/fonts/glyphicons-halflings-regular.ttf'
		},
		fontdist4: {
			nonull:true,
			src: [ 'src/main/resources/assets/bower_components/bootstrap/dist/fonts/glyphicons-halflings-regular.woff'
			],
			dest: 'src/main/resources/assets/fonts/glyphicons-halflings-regular.woff'
		},
		fontdist5: {
			nonull:true,
			src: [ 'src/main/resources/assets/bower_components/bootstrap/dist/fonts/glyphicons-halflings-regular.woff2'
			],
			dest: 'src/main/resources/assets/fonts/glyphicons-halflings-regular.woff2'
		},
		mapdist: {
			nonull: true,
			src: ['src/main/resources/assets/bower_components/angular-cookies/angular-cookies.min.js.map'
			],
			dest: 'src/main/resources/assets/js/angular-cookies.min.js.map'
		},
		dist: {
			nonull: true,
			src: [
			'src/main/resources/assets/bower_components/ng-file-upload/angular-file-upload-shim.min.js',
			'src/main/resources/assets/bower_components/jquery/dist/jquery.min.js',
			'src/main/resources/assets/bower_components/angular/angular.min.js',
			'src/main/resources/assets/bower_components/angular-route/angular-route.min.js',
			'src/main/resources/assets/bower_components/angular-resource/angular-resource.min.js',
			'src/main/resources/assets/bower_components/angular-cookies/angular-cookies.min.js',
			'src/main/resources/assets/bower_components/angular-loading-bar/src/loading-bar.js',
			'src/main/resources/assets/js/angular-http-auth.js',
			'src/main/resources/assets/bower_components/bootstrap/dist/js/bootstrap.min.js',
			'src/main/resources/assets/bower_components/ng-file-upload/angular-file-upload.min.js',
			'src/main/resources/assets/bower_components/moment/min/moment.min.js',
			'src/main/resources/assets/bower_components/toastr/toastr.min.js',
			'src/main/resources/assets/bower_components/angular-bootstrap/ui-bootstrap.min.js',
			'src/main/resources/assets/bower_components/angular-bootstrap/ui-bootstrap-tpls.min.js',
			'src/main/resources/assets/bower_components/d3/d3.min.js',
			'src/main/resources/assets/bower_components/nvd3/nv.d3.js',
			'src/main/resources/assets/bower_components/angularjs-nvd3-directives/dist/angularjs-nvd3-directives.js',
			
			'src/main/resources/assets/bower_components/angularjs-slider/rzslider.js',
			'src/main/resources/assets/bower_components/angular-bootstrap-datetimepicker/src/js/datetimepicker.js',

			'src/main/resources/assets/bower_components/angular-date-time-input/src/dateTimeInput.js',

			'src/main/resources/assets/bower_components/underscore/underscore.js',
			
			'src/main/resources/assets/bower_components/lodash/lodash.js',
			'src/main/resources/assets/bower_components/angular-google-maps/dist/angular-google-maps.js',
			
			'src/main/resources/assets/js/app.js',
			'src/main/resources/assets/js/services.js',
			'src/main/resources/assets/js/controllers.js',
			'src/main/resources/assets/js/filters.js',
			'src/main/resources/assets/js/directives.js'
			],
			dest: 'src/main/resources/assets/js/concatted.js',
		},
        }
	});
	grunt.registerTask('default', ['jshint','concat']);
};

