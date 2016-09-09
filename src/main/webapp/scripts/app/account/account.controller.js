'use strict';

angular.module('crossfitApp')
	.controller('AccountController', function ($rootScope, $scope, $state, $stateParams, $window, Principal) {
		
		Principal.identity().then(function(account) {
            $scope.account = account;
        });
		
    });