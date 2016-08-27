'use strict';

angular.module('crossfitApp')
    .controller('MainManagerController', function ($scope, Principal, Planning, Member, Booking) {
    	$scope.planning = [];
        $scope.page = 0;
        $scope.selectedIndex = 0;
        $scope.quickbooking = {};
        $scope.quickbookingUsers = [];
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
            $scope.loadAll();
        });
        
        
        $scope.loadAll = function() {
        	$scope.planning = [];
            $scope.quickbooking = {};
            $scope.quickbookingUsers = [];
            Planning.query({page: $scope.page, per_page: 14}, function(result, headers) {
                for (var i = 0; i < result.days.length; i++) {
                    $scope.planning.push(result.days[i]);
                }
            });
        };

        $scope.select = function(index) {
            $scope.selectedIndex = index;
        };
        $scope.showQuickAddBooking = function(slot){
            $scope.quickbooking = {
            	timeslot: slot,
            	timeslotId: slot.id,
            	date: $scope.planning[$scope.selectedIndex].date
            };
              
            $('#quickAddBooking').modal('show');
        }
        $scope.selectUserForQuickBooking = function(user){
			$scope.quickbooking.owner = user;
        }
        $scope.searchUserForQuickBooking = function(){
        	if ($scope.quickbookingLike.length >= 3)
        	Member.query({
              	page: 1, per_page: 5, 
              	search: $scope.quickbookingLike, 
              	include_actif: true,
              	include_not_enabled: true,
              	include_bloque: false}, 
              	function(result, headers) {
            
  	                $scope.quickbookingUsers = [];
  	                for (var i = 0; i < result.length; i++) {
  	                    $scope.quickbookingUsers.push(result[i]);
  	                }
              	});
        }
        $scope.quickAddBooking = function(){
        	Booking.save($scope.quickbooking, function(){
        		$scope.loadAll();
                $('#quickAddBooking').modal('hide'); 
        	});
        }


        $scope.quickDeleteBooking = function(booking){
        	if (confirm("Supprimer la réservation de "+booking.owner.firstName+" ?")){
            	Booking.delete({id : booking.id}, function(){
            		$scope.loadAll();
            	});
        	}
        }
        
        $scope.quickValidateBooking = function(booking){
        	if (confirm("Valider la réservation de "+booking.owner.firstName+" ?")){
	        	Booking.validate({id : booking.id}, function(){
	        		$scope.loadAll();
	        	});
        	}
        }
    })
    .controller('MainUserController', function ($scope, Principal, Planning) {
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });
    });
