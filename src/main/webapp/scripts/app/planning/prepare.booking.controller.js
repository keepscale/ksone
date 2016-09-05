'use strict';

angular.module('crossfitApp')
	.controller('PrepareBookingController', function ($rootScope, $scope, $state, $stateParams, $window, Booking, DateUtils, Principal) {

		//$stateParams.timeSlotId;
		//$stateParams.bookingDate;

        $scope.booking = {
			timeslotId: $stateParams.timeSlotId,
			date: DateUtils.parseDateAsDate($stateParams.bookingDate)
        };
        
		$scope.prepareBooking = function() {
			Booking.prepareBooking($scope.booking, function(){
				
        	});
        };
		
        $scope.prepareBooking()
    });