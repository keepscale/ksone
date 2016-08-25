'use strict';

angular.module('crossfitApp')
    .controller('BookingController', function ($scope, Booking, ParseLinks) {

    	
        
        
        
        $scope.bookings = [];
        $scope.page = 1;
        $scope.loadAllBookings = function() {
            Booking.query({page: $scope.page, per_page: 20}, function(result, headers) {
                $scope.links = ParseLinks.parse(headers('link'));
                for (var i = 0; i < result.length; i++) {
                    $scope.bookings.push(result[i]);
                }
            });
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.bookings = [];
            $scope.loadAllBookings();
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAllBookings();
        };
        $scope.loadAllBookings();

        $scope.delete = function (id) {
            Booking.get({id: id}, function(result) {
                $scope.booking = result;
                $('#deleteBookingConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Booking.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteBookingConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.booking = {startAt: null, endAt: null, status: null, createdDate: null, createdBy: null, id: null};
        };
        
        
        
        
        
    });
