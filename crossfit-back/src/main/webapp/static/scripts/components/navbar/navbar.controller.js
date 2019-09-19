'use strict';

angular.module('crossfitApp')
	.controller('NavbarListController', function ($scope, $location, $state, $window, DateUtils, Auth, Principal, EventBooking) {
		Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });
        $scope.$state = $state;
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
        	if (Principal.isInAnyRole(['ROLE_MANAGER','ROLE_ADMIN'])){
            	$scope.statusWS = "PENDING";
            	EventBooking.query(function(result, headers){
            		$scope.events = result;
	            	$scope.statusWS = "CONNECTED";
            	});
        	}
        }

		$scope.loadEvents();
});
