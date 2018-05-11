'use strict';

angular.module('crossfitApp')
    .controller('NavbarController', function ($scope, $location, $state, $window, DateUtils, Auth, Principal, EventBooking) {
    	
    	Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });
    	
        $scope.$state = $state;

        $scope.logout = function () {
        	$scope.account = null;
            Auth.logout();
            $state.go('home');
        };
        
        $scope.newEventCount = 0;
        $scope.eventHighLightCount = 0;
        
        
        $scope.hasNewEvent = function(){
        	return $scope.newEventCount > 0;
        }
        
        $scope.displayLastEvent = function(){
    		$scope.eventHighLightCount = $scope.newEventCount;
    		$scope.newEventCount = 0;
        }
        

        $scope.newBookingEvent = function(bookingEvent){
        	bookingEvent.bookingStartDate = DateUtils.convertDateTimeFromServer(bookingEvent.bookingStartDate);
        	var now = new Date();
        	
        	if (bookingEvent.bookingStartDate > now && now.toDateString() === bookingEvent.bookingStartDate.toDateString()){
            	//console.log(bookingEvent);
            	$scope.newEventCount = $scope.newEventCount + 1;
            	EventBooking.query(function(result, headers){
            		$scope.events = result;
            	});
                $scope.$apply() 
        	}
        	else{
        		//console.log("bookingEvent n'est pas aujourd'hui");
        	}
        	
        	
        }        

        $scope.connect = function() {
            var socket = new SockJS('/ws');
            var stompClient = Stomp.over(socket);  
            stompClient.connect({}, function(frame) {
                stompClient.subscribe('/topic/bookings', function(messageOutput) {
                	$scope.newBookingEvent(JSON.parse(messageOutput.body));
                });
            });
        }

        if (Principal.isInAnyRole(['ROLE_MANAGER','ROLE_ADMIN','ROLE_COACH'])){
            $scope.connect();
        	EventBooking.query(function(result, headers){
        		$scope.events = result;
        	});
        }
    });
