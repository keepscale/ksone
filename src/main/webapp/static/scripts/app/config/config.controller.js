'use strict';

angular.module('crossfitApp')
    .controller('ConfigController', function ($scope, CrossfitBox, ParseLinks, AdminSQL) {
 
    	$scope.box = CrossfitBox.current();
    	
    	$scope.querySQL = "SELECT * FROM member"

		$scope.countQuery = 0;
    	$scope.queryResultList = {};
    		
		$scope.executeSelect = function(){
	    	$scope.queryResultList = {};
    		AdminSQL.executeSelect($scope.querySQL, function(result){
    			$scope.queryResultList = result;
    			$scope.countQuery = result.length;
    		});
    	};

		$scope.executeUpdate = function(){
    		AdminSQL.executeUpdate($scope.querySQL, function(result){
    			$scope.countQuery = result;
    		});
    	};
    	
    	
    	$scope.update = function(){
    		CrossfitBox.update($scope.box);
    	};
    });
