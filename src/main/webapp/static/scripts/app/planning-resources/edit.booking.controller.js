'use strict';

angular.module('crossfitApp')
	.controller('EditResourceBookingController', function ($rootScope, $scope, $state, $stateParams, $window, Resource, ResourceBooking, DateUtils, Principal, Member) {


		$scope.resourceBooking = {};

        
        $scope.deleteResourceBooking = function(){
        	ResourceBooking.delete({id:$scope.resourceBooking.id}, $scope.onSucess, $scope.onError);
        }

        $scope.onError = function(result){
        	$scope.message = result.data.message;
        	if (result.data.errors){
				 $scope.errors = result.data.errors;
			}
        }
        
        $scope.onSucess = function(result){
        	$scope.goBack();
        }

        $scope.goBack = function() {
        	$state.go('planning-resources', {});
    	};

    	$scope.init = function(){

    		ResourceBooking.get({id:$stateParams.id}, function(result){
    			$scope.resourceBooking = result;
    		});

    	}
    	

		$scope.init();
    });