'use strict';

angular.module('crossfitApp')
	.controller('PrepareRentController', function ($rootScope, $scope, $state, $stateParams, $window, Resource, DateUtils, Principal) {


		$scope.resourceBooking = {};

        
        $scope.saveResourceBooking = function(){
        	ResourceBooking.save($scope.resourceBooking, $scope.onSucess, $scope.onError);
        }

        $scope.onError = function(result){
        	console.log("OnError " + result);
        }
        
        $scope.onSucess = function(result){
        	$scope.goBack();
        }
                
        $scope.goBack = function() {
        	$state.go('planning-resources', {resourceId:$scope.resourceBooking.resource.id});
    	};
    	
    	$scope.init = function(){

    		Resource.get({id:$stateParams.resourceId}, function(result){
    			$scope.resourceBooking.resource = result;
    		});

            $scope.resourceBooking.date = $stateParams.startDate;
    	}
    	

		$scope.init();
    });