'use strict';

angular.module('crossfitApp')
    .controller('LoginController', function ($rootScope, $scope, $state, $timeout, Auth, Password) {
        $scope.user = {};
        $scope.errors = {};

        $scope.recoveryEmail = "";
        
        $scope.rememberMe = true;
        $timeout(function (){angular.element('[ng-model="username"]').focus();});
        $scope.login = function (event) {
            event.preventDefault();
            Auth.login({
                username: $scope.username,
                password: $scope.password,
                rememberMe: $scope.rememberMe
            }).then(function () {
                $scope.authenticationError = false;
                $rootScope.back();
            }).catch(function () {
                $scope.authenticationError = true;
            });
        };

        $scope.sendPassword = function () {
			$scope.recoverySuccess = "";
			$scope.recoveryError = "";
        	Password.recover($scope.recoveryEmail, 
				function(){
        			$scope.recoverySuccess = "Un nouveau mot de passe vient de vous être envoyé par mail !";
	        	}, 
	        	function(){
        			$scope.recoveryError = "Ce mail n'existe pas ou le compte est verrouillé !";
	        	}
        	);
        }
    });
