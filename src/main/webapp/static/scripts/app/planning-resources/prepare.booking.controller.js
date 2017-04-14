'use strict';

angular.module('crossfitApp')
	.controller('PrepareResourceBookingController', function ($rootScope, $scope, $state, $stateParams, $window, Resource, ResourceBooking, DateUtils, Principal, Member) {


		$scope.resourceBooking = {};

        
        $scope.saveResourceBooking = function(){
        	$scope.resourceBooking.memberId = $scope.selectedMember.id;
        	$scope.resourceBooking.startHour = $scope.localStartHour;
        	$scope.resourceBooking.endHour = $scope.localEndHour;
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
    			$scope.availableMember = [];
    			for (var i = 0; i < $scope.currentResource.rules.length; i++) {
    				$scope.availableMember.push($scope.currentResource.rules[i].member);
    			}
    		});
    		

			Principal.identity().then(function(account) {
				$scope.selectedMember = account;
			});

    		var startDate = DateUtils.parseDateAsDate($stateParams.startDate);
    		var startHour = new Date();
            $scope.resourceBooking.date = startDate < new Date() ? new Date() : startDate;
            $scope.localEndHour = DateUtils.parseDateAsTime("10:00:00");
            $scope.localStartHour = DateUtils.parseDateAsTime("09:00:00");
    	}
    	
    	$scope.changeStartHour = function(oldValue){
    		var diff = Math.abs($scope.localEndHour - oldValue);
    		$scope.localEndHour = new Date($scope.localStartHour.getTime() + diff);
    	}
    	
    	$scope.isHourIncoherent = function(){
    		var start = new Date();
    		start.setHours($scope.localStartHour.getHours());
    		start.setMinutes($scope.localStartHour.getMinutes());
    		
    		var end = new Date();
    		end.setHours($scope.localEndHour.getHours());
    		end.setMinutes($scope.localEndHour.getMinutes());
    		
    		return end <= start;
    	}
    	
    	$scope.$watch("localStartHour", function(newValue, oldValue) {
    		$scope.changeStartHour(oldValue);
    	}, true);
    	

		$scope.init();
    });