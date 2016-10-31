'use strict';

angular.module('crossfitApp')
    .controller('NavbarController', function ($scope, $location, $state, $window, DateUtils, Auth, Principal) {
    	
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
    });
