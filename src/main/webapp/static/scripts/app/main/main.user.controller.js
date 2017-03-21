'use strict';

angular.module('crossfitApp')
	.controller('MainUserController', function ($rootScope, $scope, $state, $stateParams, $window, TimeSlot, DateUtils, Principal) {
        
		Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
            
            if ($scope.isAuthenticated){

               
            }
            

    		$scope.webcalUrl = "webcal://" + location.hostname+(location.port ? ':'+location.port: '') + "/public/ical/"+$scope.account.uuid+"/booking.ics";
    		$scope.httpICalUrl = location.protocol + '//'+ location.hostname+(location.port ? ':'+location.port: '') + "/public/ical/"+$scope.account.uuid+"/booking.ics";
        });
		
		
		$scope.mustFillPersonnalInfo = function(){
			return $scope.isBlank($scope.account.firstName)
				|| $scope.isBlank($scope.account.lastName)
				|| $scope.isBlank($scope.account.city)
				|| $scope.isBlank($scope.account.address)
				|| $scope.isBlank($scope.account.zipCode)
				|| $scope.isBlank($scope.account.telephonNumber);
		}
		
		$scope.isBlank = function(str){
			 return (!str || /^\s*$/.test(str));
		}
    });