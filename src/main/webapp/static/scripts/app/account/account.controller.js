'use strict';

angular.module('crossfitApp')
	.controller('AccountController', function ($rootScope, $scope, $state, $stateParams, $window, Principal) {
		
		$scope.socialEnabled = $rootScope.socialEnabled;

		
		Principal.identity().then(function(account) {
            $scope.account = account;
        });
		
    });