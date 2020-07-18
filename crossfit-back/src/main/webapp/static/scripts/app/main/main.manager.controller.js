'use strict';

angular.module('crossfitApp')
    .controller('MainManagerController', function ($scope, Principal, Planning, Subscription, Booking, Member, DateUtils) {
    	$scope.planning = [];
        $scope.page = 0;
        $scope.selectedIndex = 0;
        $scope.quickbooking = {};
        $scope.quickbookingSubscriptions = [];
        
        $scope.membersHealthCount;
        
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
            $scope.loadAll();
        });
        
        $scope.next = function(){
        	$scope.page++;
            $scope.selectedIndex = 0;
            $scope.loadAll();
        }

        $scope.prev = function(){
        	$scope.page--;
            $scope.selectedIndex = 0;
            $scope.loadAll();
        }
        
        
        $scope.loadAll = function() {
            $scope.quickbooking = {};
            $scope.quickbookingSubscriptions = [];
            Planning.boxPlanning({page: $scope.page, per_page: 2}, function(result, headers) {
            	$scope.page = result.page;
            	$scope.planning = result.days;
            });

        	if(Principal.isInAnyRole(['ROLE_MANAGER', 'ROLE_ADMIN'])){
	            Member.health({}, function(result, headers){
	            	$scope.membersHealthCount = result;
	            });
        	}
        };

        $scope.toggle = function(event){
        	var panel = $(event.target).parents(".panel");
        	$(panel).toggleClass("close-slot");
        }
        
        $scope.forceOpenedSlot = [];
        
        $scope.forceOpen = function(event, slot){
        	$scope.toggle(event);
        	var idx = $scope.forceOpenedSlot.indexOf(slot.start);

			// Is currently selected
			if (idx > -1) {
				$scope.forceOpenedSlot.splice(idx, 1);
			}

			// Is newly selected
			else {
				$scope.forceOpenedSlot.push(slot.start);
			}
        }

        
        $scope.calculateCssClassQuickAdd = function(subscription){
        	var now = Date.now();
        	var end = DateUtils.toUTCDate(new Date(subscription.subscriptionEndDate)).getTime();
        	
        	return end > now ? 'actif' : "inactif";
        	
        }
        
        $scope.calculateCssClass = function(slot){
        	var now =  DateUtils.toUTCDate(new Date()).getTime();
        	var start = DateUtils.toUTCDate(new Date(slot.start)).getTime();
        	var end = DateUtils.toUTCDate(new Date(slot.end)).getTime();
        	
        	var style = "";
        	if ( start <= now && now <= end){
        		style = "active-slot";
        	}
        	else if ( start < now ){
        		style = "close-slot";
        	}
        	else{
        		style = "open-slot";
        	}
        	if ($scope.forceOpenedSlot.indexOf(slot.start) > -1){
        		style = "open-slot";
        	}
        	if (slot.bookings.length == 0){
        		style += " no-booking"
        	}
        	
        	return style;
        }

        $scope.select = function(index) {
            $scope.selectedIndex = index;
        };
        $scope.showQuickAddBooking = function(slot){
        	$scope.quickbookingLike = "";
        	$scope.quickbookingSubscriptions = [];
        	$scope.quickbookingSlot = slot;
            $scope.quickbooking = {
            	timeslotId: slot.id,
            	date: $scope.planning[$scope.selectedIndex].date
            };
              
            $('#quickAddBooking').modal('show');
        }
        $scope.selectSubscriptionForQuickBooking = function(subscription){
			$scope.quickbooking.subscriptionId = subscription.id;
        }
        $scope.searchSubscriptionForQuickBooking = function(){
        	if ($scope.quickbookingLike.length >= 3)
        		Subscription.query({
              	page: 1, per_page: 10, 
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
        	$scope.quickbooking.timeslot = null;
        	Booking.save($scope.quickbooking, function(){
        		$scope.loadAll();
                $('#quickAddBooking').modal('hide'); 
        	});
        }

        $scope.quickCheckIn = function(booking){
        	if (confirm("Valider la présence de "+booking.title+" ?")){
        		Booking.checkIn({id : booking.id}, function(){
        			$scope.loadAll();
        		});
        	}
        }
        
        $scope.quickDeleteBooking = function(booking){
        	if (confirm("Supprimer la réservation de "+booking.title+" ?")){
            	Booking.delete({id : booking.id}, function(){
            		$scope.loadAll();
            	});
        	}
        }
        
        $scope.quickValidateBooking = function(booking){
        	if (confirm("Valider la réservation de "+booking.title+" ?")){
	        	Booking.validate({id : booking.id}, function(){
	        		$scope.loadAll();
	        	});
        	}
        }
        
        $scope.hasHealthIndicator = function(){
        	var hasIndicator = false;
        	if ($scope.membersHealthCount == null){
        		return false;
        	}
        	Object.keys($scope.membersHealthCount).forEach(function(key,index) {
        	    if ($scope.membersHealthCount[key] > 0){
        	    	hasIndicator = true;
        	    }
        	});
        	return hasIndicator;
        }
    });
    
