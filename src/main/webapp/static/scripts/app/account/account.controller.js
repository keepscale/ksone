'use strict';

angular.module('crossfitApp')
	.controller('AccountController', function ($rootScope, $scope, $state, $stateParams, $window, Principal, Account, Auth, AlertService) {
		
		$scope.socialEnabled = $rootScope.socialEnabled;
		$scope.password = "";
		$scope.confirmPassword = "";
		$scope.doNotMatch = false;


		Principal.identity().then(function(account) {
            $scope.account = account;
        });
        
		$scope.updatePersonnalInformation = function(){
			Account.update($scope.account, function(){
				AlertService.success("Informations mises à jour !");
			},
			function(){
				AlertService.error("Impossible de mettre à jour vos informations !");
			});
		}
		

        $scope.updatePassword = function () {
            if ($scope.password !== $scope.confirmPassword) {
                $scope.doNotMatch = 'ERROR';
            } else {
                $scope.doNotMatch = null;
                Auth.changePassword($scope.password).then(function () {
                    $scope.error = null;
    				AlertService.success("Mot de passe changé !");
                }).catch(function () {
    				AlertService.error("Impossible de mettre à jour votre mot de passe !");
                });
            }
        };
		
    });