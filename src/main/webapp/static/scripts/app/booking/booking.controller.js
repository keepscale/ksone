'use strict';

angular.module('crossfitApp')
    .controller('BookingController', function ($scope, Booking, ParseLinks) {

        $scope.bookings = [];
        $scope.pastBookings = [];
        
        $scope.loadAllBookings = function() {
            Booking.query(function(result, headers) {
            	$scope.bookings = result;
            });
        };
        
        $scope.loadAllBookings();

        $scope.delete = function (booking) {
        	if (confirm("Supprimer la réservation '"+booking.title+"' ?")){
	            Booking.delete({id: booking.id}, function(result) {
	                $scope.loadAllBookings();
	            }, function(result){
	            	$scope.message = result.datav.message;
	            });
        	}
        };
        
        
        
    });
