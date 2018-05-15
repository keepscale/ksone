'use strict';

angular.module('crossfitApp')
	.controller('NavbarListController', function ($scope, $location, $state, $window, DateUtils, Auth, Principal, EventBooking) {
		Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });
	})
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
        
    	
    	$scope.getLastCheckEventDate = function(){
    		var val = $window.localStorage.getItem("LastCheckEventDate");
    		if (val){
    			return new Date(val);
    		}
    		else{
    			return $scope.updateLastCheckEventDate();
    		}
    	}
    	
    	$scope.updateLastCheckEventDate = function(){
    		var d = new Date();
    		$window.localStorage.setItem("LastCheckEventDate", d);
    		return d;
    	}
        
        $scope.events = [];
        $scope.previousCheckEventDate = $scope.getLastCheckEventDate();
        $scope.stompClient = null;
    	$scope.statusWS = "CLOSED";
        
    	
    	$scope.newEventCountToDisplay = function(){
    		var count = $scope.newEventCount();
    		return count < 10 ? count : "9+";
    	}
    	
        $scope.newEventCount = function(){
        	var d = $scope.getLastCheckEventDate();
        	var count = $scope.events.filter(event=>event.eventDate > d).length;
        	return count;
        }
        
        $scope.displayLastEvent = function(){
        	$scope.loadEvents();
            $scope.previousCheckEventDate = $scope.getLastCheckEventDate();
            $scope.updateLastCheckEventDate();
        }

        $scope.loadEvents = function(){
        	EventBooking.query(function(result, headers){
        		$scope.events = result;        		
        	});
        }


        $scope.timeToReconnect = 1000;
        $scope.connectWebsocket = function() {
            if (Principal.isInAnyRole(['ROLE_MANAGER','ROLE_ADMIN','ROLE_COACH']) && $scope.statusWS === "CLOSED"){

            	$scope.statusWS = "PENDING";
            	$scope.wsMessage = "Connexion...";
	            $scope.stompClient = Stomp.over(new SockJS('/ws'));
	            $scope.stompClient.connect({}, 
            		function(frame) { //Tout ce qui arrive dans cette fonction n'est pas géré par angular
		            	$scope.statusWS = "CONNECTED";
		            	$scope.wsMessage = "Connecté";
		            	$scope.loadEvents();
	                    $scope.$apply();
		            	$scope.stompClient.subscribe('/topic/bookings', function(messageOutput) {
		                	var bookingEvent = JSON.parse(messageOutput.body);
		                	bookingEvent.bookingStartDate = DateUtils.convertDateTimeFromServer(bookingEvent.bookingStartDate);
		                	var now = new Date();
		                	
		                	//Si la réservation concerne une date future et concerne aujourd'hui, on reload les events
		                	if (bookingEvent.bookingStartDate > now 
		                			&& now.toDateString() === bookingEvent.bookingStartDate.toDateString()){
		                    	$scope.loadEvents();
			                    $scope.$apply();
		                	}
		                	else{
		                		//console.log("bookingEvent n'est pas aujourd'hui");
		                	}
		                	
		                });
		            }, 
		            function(error){
		            	$scope.statusWS = "ERROR";
		            	$scope.wsMessage = "Erreur: " + error;
		                $scope.$apply();
		                

		                if ($scope.timeToReconnect/1000 < 10){
		                	console.log("Tentative de reconnection dans " + $scope.timeToReconnect + "ms (encore " + (10-($scope.timeToReconnect/1000)) + " tentatives)");
							window.setTimeout(function(){
								$scope.closeWebsocket(function(){
									$scope.connectWebsocket();
								});
							}, $scope.timeToReconnect+=1000);
		                }
		            }
		        );
            }
        }
        
        $scope.closeWebsocket = function(callback) {
        	$scope.stompClient.disconnect(function() {
            	$scope.statusWS = "CLOSED";
            	$scope.wsMessage = "Déconnecté";
                $scope.$apply()
                callback();
    	    });
        }        

		
		
		function handleVisibilityChange() {
			if (document.hidden) {
				$scope.closeWebsocket();
			}
			else {
				window.setTimeout(function(){
					$scope.connectWebsocket();
	                $scope.$apply() 
				}, 2000);
			}
		}

        $scope.connectWebsocket();
    	document.addEventListener("visibilitychange", handleVisibilityChange, false);
});
