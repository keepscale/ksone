'use strict';

angular.module('crossfitApp').controller('ResourceDialogController',
    ['$scope', '$stateParams', '$state', '$uibModalInstance', 'entity', 'Resource', 'Member', 
        function($scope, $stateParams, $state, $modalInstance, entity, Resource, Member) {

        $scope.resource = entity;
        $scope.availableMember = Member.query({
        	page: 1, per_page: 500, 
        	include_roles: ["ROLE_RENTER"],
        	include_actif: true,
        	include_not_enabled: true,
        	include_bloque: false});
        
        $scope.load = function(id) {
            Resource.get({id : id}, function(result) {
                $scope.resource = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('crossfitApp:resourceUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.resource.id != null) {
                Resource.update($scope.resource, onSaveFinished);
            } else {
                Resource.save($scope.resource, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };

        $scope.addRules = function() {
        	$scope.resource.rules.push({});
        };

        $scope.deleteRulesAtIndex = function(idx) {
        	$scope.resource.rules.splice(idx, 1);
        };
}]);
