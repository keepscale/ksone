'use strict';

angular.module('crossfitApp')
	.controller('MainUserController', function ($scope, Principal, Planning) {
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });
        
        $scope.nbBookingPossible = 3;
        
    });