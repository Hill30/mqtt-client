# Build configurations.
module.exports = (grunt) ->

	grunt.registerMultiTask 'fileconstruct', 'Build a file', ->
		src = @data.src
		dist = @data.dist
		return grunt.log.warn('src is required') if !src
		return grunt.log.warn('dist is required') if !dist
		cwd = @data.cwd ? './'
		files = grunt.file.expand {cwd}, src
		return grunt.verbose.warn('No src files found') if !files.length is 0

		path = require 'path'
		return grunt.log.warn('path lib is required') if !path

		console.log 'files:', files
		console.log dist

		grunt.file.delete(dist) if grunt.file.exists(dist)

		scripts = "<% if (config.environment === 'dev') { %>\n"

		files.forEach (file) ->
			scripts += "<script src='#{file}'></script>\n"

		scripts += "<% } %>\n"

		grunt.file.write dist, scripts
		grunt.template.process


	grunt.registerMultiTask "wrap", "Wraps source files with specified header and footer", ->
		data = @data
		path = require("path")
		header = grunt.file.read(grunt.template.process(data.header))
		footer = grunt.file.read(grunt.template.process(data.footer))
		contents = grunt.file.read(grunt.template.process(data.content))
		prodScripts = grunt.file.read(grunt.template.process(data.prodScripts))
		devScripts = grunt.file.read(grunt.template.process(data.devScripts)) if data.devScripts

		tmp = header + contents + prodScripts
		tmp = tmp + devScripts if data.devScripts
		tmp = tmp + footer

		grunt.file.write "./.temp/index.html", tmp

		grunt.file.delete(grunt.template.process(data.devScripts)) if data.devScripts
		grunt.log.writeln "File \"" + "./.temp/index.html" + "\" created."


	grunt.initConfig
	# Deletes deployment and temp directories.
	# The temp directory is used during the build process.
	# The deployment directory contains the artifacts of the build.
	# These directories should be deleted before subsequent builds.
	# These directories are not committed to source control.

		path:
			toApp: "./serviceTracker/src/main/assets/application"

		fileconstruct:
			scripts:
				cwd: "<%= path.toApp %>/.temp"
				dist: "<%= path.toApp %>/src/devScripts.html"
				src: [
					"<%= path.toApp %>/scripts/libs/vendors/jquery/jquery.js"
					"<%= path.toApp %>/scripts/libs/vendors/angular/angular.js"
					"<%= path.toApp %>/scripts/libs/vendors/angular-resource/angular-resource.js"
					"<%= path.toApp %>/scripts/libs/vendors/angular-route/angular-route.js"
					"<%= path.toApp %>/scripts/libs/vendors/angular-bootstrap/ui-bootstrap-tpls.js"
					"<%= path.toApp %>/scripts/libs/vendors/modules/**/*Module.js"
					"<%= path.toApp %>/scripts/libs/vendors/filters/**/*.js"
					"<%= path.toApp %>/scripts/libs/vendors/libs/**/*.js"
					"<%= path.toApp %>/scripts/libs/vendors/resources/**/*.js"
					"<%= path.toApp %>/scripts/libs/vendors/directives/**/*.js"
					"<%= path.toApp %>/scripts/libs/vendors/services/**/*.js"
					"<%= path.toApp %>/scripts/config/development.js"
					"<%= path.toApp %>/scripts/app.js"
					"<%= path.toApp %>/scripts/routes.js"
					"<%= path.toApp %>/scripts/responseInterceptors/*.js"
					"<%= path.toApp %>/scripts/controllers/**/*.js"
					"<%= path.toApp %>/scripts/directives/**/*.js"
					"<%= path.toApp %>/scripts/services/**/*.js"
					"<%= path.toApp %>/scripts/filters/**/*.js"
					"<%= path.toApp %>/scripts/resources/**/*.js"
				]

		wrap:
			dev:
				header: '<%= path.toApp %>/src/header.html'
				content: '<%= path.toApp %>/src/content.html'
				prodScripts : '<%= path.toApp %>/src/prodScripts.html'
				devScripts: '<%= path.toApp %>/src/devScripts.html'
				footer: '<%= path.toApp %>/src/footer.html'
				dest: '<%= path.toApp %>'
			prod:
				header: '<%= path.toApp %>/src/header.html'
				content: '<%= path.toApp %>/src/content.html'
				prodScripts : '<%= path.toApp %>/src/prodScripts.html'
				footer: '<%= path.toApp %>/src/footer.html'
				dest: '<%= path.toApp %>/'

		clean:
			bower:
				src: [
					'<%= path.toApp %>/libs/vendors/'
					'./bower_components/'
					'<%= path.toApp %>/styles/vendors/'
				]
			dev:
				src: [
					'<%= path.toApp %>/public/'
					'<%= path.toApp %>/.test/'
					'<%= path.toApp %>/.temp/'
				]
			prod:
				src: [
					'<%= path.toApp %>/.test/'
					'<%= path.toApp %>/.temp/'
				]

	# Compile CoffeeScript (.coffee) files to JavaScript (.js).
		coffee:
			dev:
				files: [
					cwd: '<%= path.toApp %>/.temp/'
					src: '<%= path.toApp %>/scripts/**/*.coffee'
					dest: '<%= path.toApp %>/.temp/'
					expand: true
					ext: '.js'
				]
				options:
					bare: true
			prod:
				files: [
					'<%= path.toApp %>/.temp/scripts/env.js': [ '<%= path.toApp %>/.temp/scripts/config/production.coffee' ]
					'<%= path.toApp %>/.temp/scripts/scripts.js': [
						'<%= path.toApp %>/.temp/scripts/libs/vendors/modules/**/*.coffee'
						'<%= path.toApp %>/.temp/scripts/libs/vendors/resources/**/*.coffee'
						'<%= path.toApp %>/.temp/scripts/libs/vendors/filters/**/*.coffee'
						'<%= path.toApp %>/.temp/scripts/libs/vendors/directives/**/*.coffee'
						'<%= path.toApp %>/.temp/scripts/libs/vendors/services/**/*.coffee'
						'<%= path.toApp %>/.temp/scripts/app.coffee'
						'<%= path.toApp %>/.temp/scripts/routes.coffee'
						'<%= path.toApp %>/.temp/scripts/responseInterceptors/**/*.coffee'
						'<%= path.toApp %>/.temp/scripts/resources/**/*.coffee'
						'<%= path.toApp %>/.temp/scripts/services/**/*.coffee'
						'<%= path.toApp %>/.temp/scripts/directives/**/*.coffee'
						'<%= path.toApp %>/.temp/scripts/controllers/**/*.coffee'
						'<%= path.toApp %>/.temp/scripts/filters/**/*.coffee'
					]
					'<%= path.toApp %>/.temp/scripts/views.js': [ '<%= path.toApp %>/.temp/scripts/views.coffee' ]
				]
				options:
					bare: true
					sourceMap: false

	# Compile LESS (.less) files to CSS (.css).
		less:
			styles:
				files:
					'<%= path.toApp %>/styles/vendors/styles.css': ['<%= path.toApp %>/styles/vendors/bootstrap/bootstrap.less']
	#options:
	#  sourcemap: true

		connect:
			app:
				options:
					base: './serviceTracker/src/main/assets/application'
					port: 5000
					livereload: false
					keepalive: true

	# Copies directories and files from one location to another.
		copy:
		# Copies the contents of the temp directory, except views, to the dist directory.
		# In 'dev' individual files are used.
			dev:
				files: [
					cwd: '<%= path.toApp %>/.temp/'
					src: '**'
					dest: './public/'
					expand: true
				]

		# Copies img directory to temp.
			img:
				files: [
					cwd: '<%= path.toApp %>/src/'
					src: ['img/**/*.png','img/**/*.jpg','img/**/*.gif']
					dest: '<%= path.toApp %>/.temp/'
					expand: true
				]
		# Copies favicon to temp.
			favicon:
				files: [
					cwd: '<%= path.toApp %>/src/'
					src: 'favicon.ico'
					dest: './.temp/'
					expand: true
				]
		# Copies csslib directory to temp.
			csslib:
				files: [
					cwd: '<%= path.toApp %>/src/'
					src: ['styles/lib/**/*','styles/vendors/**/*']
					dest: '<%= path.toApp %>/.temp/'
					expand: true
				]
		# Copies styles directory with less to temp.
			styleSource:
				files: [
					cwd: '<%= path.toApp %>/src/'
					src: 'styles/**/*'
					dest: '<%= path.toApp %>/.temp/'
					expand: true
				]
		# Copies fonts directory to temp.
			fonts:
				files: [
					cwd: '<%= path.toApp %>/src/'
					src: 'fonts/**/*'
					dest: '<%= path.toApp %>/.temp/'
					expand: true
				]
		# Copies js files to the temp directory
			scriptSource:
				files: [
					cwd: '<%= path.toApp %>/src/scripts'
					src: '**/*.js'
					dest: '<%= path.toApp %>/.temp/scripts'
					expand: true
				,
					cwd: '<%= path.toApp %>/src/scripts'
					src: '**/*.coffee'
					dest: '<%= path.toApp %>/.temp/scripts'
					expand: true
				,
					cwd: '<%= path.toApp %>/src/libs/'
					src: '**/*.js'
					dest: '<%= path.toApp %>/.temp/scripts/libs'
					expand: true
				,
					cwd: '<%= path.toApp %>/src/libs/'
					src: '**/*.coffee'
					dest: '<%= path.toApp %>/.temp/scripts/libs'
					expand: true
				]

		# Task is run when the watched index.template file is modified.
			index:
				files: [
					cwd: '<%= path.toApp %>/.temp/'
					src: 'index.html'
					dest: '<%= path.toApp %>/public/'
					expand: true
				]
		# Task is run when a watched script is modified.
			scripts:
				files: [
					cwd: '<%= path.toApp %>/.temp/'
					src: 'scripts/**'
					dest: './public/'
					expand: true
				]
		# Task is run when a watched style is modified.
			styles:
				files: [
					cwd: '<%= path.toApp %>/.temp/'
					src: 'styles/**'
					dest: '<%= path.toApp %>/public/'
					expand: true
				]
		# Task is run when a watched view is modified.
			views:
				files: [
					cwd: '<%= path.toApp %>/.temp/'
					src: 'views/**'
					dest: '<%= path.toApp %>/public/'
					expand: true
				]

	# Compresses png files
		imagemin:
			img:
				files: [
					cwd: '<%= path.toApp %>/src/'
					src: ['img/**/*.png','img/**/*.jpg','img/**/*.gif']
					dest: '<%= path.toApp %>/.temp/'
					expand: true
				]
				options:
					optimizationLevel: 7

	# Install Bower components
		bower:
			install:
				options:
					targetDir: '<%= path.toApp %>'
					layout: 'byType'
					install: true
					verbose: true
					cleanTargetDir: false
					cleanBowerDir: false

	# This file is then included in the output automatically.  AngularJS will use it instead of going to the file system for the views, saving requests.  Notice that the view content is actually minified.  :)
		ngTemplateCache:
			views:
				files:
					'<%= path.toApp %>/.temp/scripts/views.js': '<%= path.toApp %>/.temp/views/**/*.html'
				options:
					trim: '<%= path.toApp %>/.temp/'

		template:
			views:
				files:
					'<%= path.toApp %>/.temp/views/': '<%= path.toApp %>/src/views/**/*.html'
			dev:
				files:
					'<%= path.toApp %>/.temp/index.html': '<%= path.toApp %>/.temp/index.html'
				environment: 'dev'
			prod:
				files: '<%= template.dev.files %>'
				environment: 'prod'


	# Register grunt tasks supplied by grunt-contrib-*.
	# Referenced in package.json.
	# https://github.com/gruntjs/grunt-contrib

	grunt.loadNpmTasks 'grunt-contrib-clean'
	grunt.loadNpmTasks 'grunt-contrib-coffee'
	grunt.loadNpmTasks 'grunt-contrib-connect'
	grunt.loadNpmTasks 'grunt-contrib-copy'
	grunt.loadNpmTasks 'grunt-contrib-imagemin'
	grunt.loadNpmTasks 'grunt-contrib-uglify'
	grunt.loadNpmTasks 'grunt-contrib-cssmin'
	grunt.loadNpmTasks 'grunt-contrib-less'
	grunt.loadNpmTasks 'grunt-contrib-concat'

	# Register grunt tasks supplied by grunt-hustler.
	# Referenced in package.json.
	# https://github.com/CaryLandholt/grunt-hustler
	grunt.loadNpmTasks 'grunt-hustler'

	# Grunt-Bower-Task.
	grunt.loadNpmTasks 'grunt-bower-task'

	# Starts a web server
	# Enter the following command at the command line to execute this task:
	# grunt server
	grunt.registerTask 'server', [
		'default'
		'connect'
	]

	# Compiles the app with non-optimized build settings and places the build artifacts in the dist directory.
	# Enter the following command at the command line to execute this build task:
	# grunt
	grunt.registerTask 'default', [
		'clean:dev'
		'bower:install'
		#'copy:scriptSource'
		#'coffee:dev'
		#'fileconstruct'
		#'wrap:dev'
		#'copy:styleSource'
		#'template:views'
		#'copy:img'
		#'copy:favicon'
		#'copy:csslib'
		'less:styles'
		#'copy:fonts'
		#'template:dev'
		#'copy:dev'
	]

	# Compiles the app with non-optimized build settings, places the build artifacts in the dist directory, and watches for file changes.
	# Enter the following command at the command line to execute this build task:
	# grunt dev
	grunt.registerTask 'dev', [
		'default'
	]