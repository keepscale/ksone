'use strict';

angular.module('crossfitApp')
    .controller('VersionController', function ($scope, AdminVersion) {


    	$scope.add = function(){
    		
    		AdminVersion.save($scope.version, function(){
    			$scope.reset();
    		});
    		
    	}
    	

    	$scope.activate = function(v){
    		
    		AdminVersion.activate(v, function(){
    			$scope.reset();
    		});
    		
    	}
    	
    	$scope.reset = function(){
        	$scope.version = {};
    		$scope.versions = AdminVersion.query();
    	}
    	
    	$scope.reset();
    });
