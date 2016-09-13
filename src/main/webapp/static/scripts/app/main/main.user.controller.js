'use strict';

angular.module('crossfitApp')
	.controller('MainUserController', function ($rootScope, $scope, $state, $stateParams, $window, TimeSlot, DateUtils, Principal) {
        
		Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
            
            if ($scope.isAuthenticated){

               
            }
        });
        
    });