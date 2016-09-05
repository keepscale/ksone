'use strict';

angular.module('crossfitApp')
	.controller('PrepareBookingController', function ($rootScope, $scope, $state, $stateParams, $window, Booking, TimeSlot, DateUtils, Principal) {

		//$stateParams.timeSlotId;
		//$stateParams.bookingDate;

		$scope.cancelBooking = function(){
			if (confirm("Annuler votre réservation ?")){
	        	Booking.delete({id : $scope.booking.id}, function(){
	        		$scope.init();
	        	},$scope.onError);
        	}
		}
        
        $scope.saveBooking = function(){
        	Booking.save($scope.booking, function(){
        		$scope.init();
        	},$scope.onError);
        }
        
		$scope.prepareBooking = function() {
	        $scope.subscriptions = [];
			Booking.prepareBooking($scope.booking, 
				function(booking){
					$scope.booking.subscriptionId = booking.subscriptionId;
        		},$scope.onError);
        };
        
        $scope.onError = function(result){
        	$scope.message = result.data.message;
			if (result.data.possibleSubscriptions){
                for (var i = 0; i < result.data.possibleSubscriptions.length; i++) {
					var s = result.data.possibleSubscriptions[i];
					$scope.subscriptions.push(s);
				}
			}
			else if (result.data.errors){
				 $scope.errors = result.data.errors;
			}

			
			if (result.data.subscriptionId){
				$scope.booking.subscriptionId = result.data.subscriptionId;
			}
			if (result.data.id){
				$scope.booking.id = result.data.id;
			}
			if (result.data.createdAt){
				$scope.booking.createdAt = result.data.createdAt;
			}
        }
        
        $scope.selectSubscription = function(s){
        	if (s)
        		$scope.booking.subscriptionId = s.id;
        	else
        		$scope.booking.subscriptionId = null;
        }
        $scope.goBack = function() {
        	window.history.back();
    	};
		
    	$scope.init = function(){

    		TimeSlot.get({id:$stateParams.timeSlotId}, function(result){
    			$scope.timeSlot = result;
    		});
    		
            
            $scope.subscriptions = [];
            $scope.message = "";
            $scope.errors = "";
            
            $scope.booking = {
        			timeslotId: $stateParams.timeSlotId,
        			date: DateUtils.parseDateAsDate($stateParams.bookingDate)
                };
            $scope.prepareBooking()
    	}
    	

		$scope.init();
    });