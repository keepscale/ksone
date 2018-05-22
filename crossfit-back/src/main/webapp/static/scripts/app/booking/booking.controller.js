'use strict';

angular.module('crossfitApp')
    .controller('BookingController', function ($scope, Booking, ParseLinks) {

        $scope.bookings = [];
        $scope.pastBookings = [];
        $scope.pastPage = 1;
        $scope.hasMore = true;

        $scope.loadAllBookings = function() {
            Booking.query(function(result, headers) {
            	$scope.bookings = result;
            });
        };
        $scope.loadPastBookings = function() {
            Booking.getPast({page:$scope.pastPage}, function(result, headers){
            	if($scope.pastBookings.length == result.length){
            		$scope.hasMore = false;
            	}
            	$scope.pastBookings = result;
            })
        };
        
        $scope.showMorePastBooking = function(){
        	$scope.pastPage++;
            $scope.loadPastBookings();
        }

        $scope.loadAllBookings();
        $scope.loadPastBookings();

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
