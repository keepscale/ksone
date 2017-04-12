'use strict';

angular.module('crossfitApp')
	.controller('PrepareResourceBookingController', function ($rootScope, $scope, $state, $stateParams, $window, Resource, ResourceBooking, DateUtils, Principal) {


		$scope.resourceBooking = {};

        
        $scope.saveResourceBooking = function(){
        	ResourceBooking.save($scope.resourceBooking, $scope.onSucess, $scope.onError);
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
        	var date = DateUtils.formatDateAsDate($scope.resourceBooking.date);
        	$state.go('planning-resources', {startDate:date, resourceId:$scope.currentResource.id});
    	};
    	
    	$scope.init = function(){

    		Resource.get({id:$stateParams.resourceId}, function(result){
    			$scope.currentResource = result;
    			$scope.resourceBooking.resourceId = result.id;
    		});

    		var startDate = DateUtils.parseDateAsDate($stateParams.startDate);
    		var startHour = new Date();
            $scope.resourceBooking.date = startDate < new Date() ? new Date() : startDate;
            $scope.resourceBooking.endHour = DateUtils.parseDateAsTime("10:00:00");
            $scope.resourceBooking.startHour = DateUtils.parseDateAsTime("09:00:00");
    	}
    	
    	$scope.changeStartHour = function(oldValue){
    		var diff = Math.abs($scope.resourceBooking.endHour - oldValue);
    		$scope.resourceBooking.endHour = new Date($scope.resourceBooking.startHour.getTime() + diff);
    	}
    	
    	$scope.isHourIncoherent = function(){
    		var start = new Date();
    		start.setHours($scope.resourceBooking.startHour.getHours());
    		start.setMinutes($scope.resourceBooking.startHour.getMinutes());
    		
    		var end = new Date();
    		end.setHours($scope.resourceBooking.endHour.getHours());
    		end.setMinutes($scope.resourceBooking.endHour.getMinutes());
    		
    		return end <= start;
    	}
    	
    	$scope.$watch("resourceBooking.startHour", function(newValue, oldValue) {
    		$scope.changeStartHour(oldValue);
    	}, true);
    	

		$scope.init();
    });