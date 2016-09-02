'use strict';

angular.module('crossfitApp')
    .controller('MainManagerController', function ($scope, Principal, Planning, Subscription, Booking) {
    	$scope.planning = [];
        $scope.page = 0;
        $scope.selectedIndex = 0;
        $scope.quickbooking = {};
        $scope.quickbookingSubscriptions = [];
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
            $scope.loadAll();
        });
        
        
        $scope.loadAll = function() {
        	$scope.planning = [];
            $scope.quickbooking = {};
            $scope.quickbookingSubscriptions = [];
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
        	$scope.quickbookingSubscriptions = [];
            $scope.quickbooking = {
            	timeslot: slot,
            	timeslotId: slot.id,
            	date: $scope.planning[$scope.selectedIndex].date
            };
              
            $('#quickAddBooking').modal('show');
        }
        $scope.selectSubscriptionForQuickBooking = function(subscription){
			$scope.quickbooking.subscription = subscription;
        }
        $scope.searchSubscriptionForQuickBooking = function(){
        	if ($scope.quickbookingLike.length >= 3)
        		Subscription.query({
              	page: 1, per_page: 5, 
              	search: $scope.quickbookingLike}, 
              	function(result, headers) {
            
  	                $scope.quickbookingSubscriptions = [];
  	                for (var i = 0; i < result.length; i++) {
  	                    $scope.quickbookingSubscriptions.push(result[i]);
  	                }
              	});
        }
        $scope.quickAddBooking = function(){
        	$scope.quickbookingSubscriptions = [];
        	Booking.save($scope.quickbooking, function(){
        		$scope.loadAll();
                $('#quickAddBooking').modal('hide'); 
        	});
        }


        $scope.quickDeleteBooking = function(booking){
        	if (confirm("Supprimer la réservation de "+booking.subscription.member.firstName+" ?")){
            	Booking.delete({id : booking.id}, function(){
            		$scope.loadAll();
            	});
        	}
        }
        
        $scope.quickValidateBooking = function(booking){
        	if (confirm("Valider la réservation de "+booking.subscription.member.firstName+" ?")){
	        	Booking.validate({id : booking.id}, function(){
	        		$scope.loadAll();
	        	});
        	}
        }
    });
    
