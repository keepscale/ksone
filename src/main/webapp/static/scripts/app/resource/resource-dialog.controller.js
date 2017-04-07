'use strict';

angular.module('crossfitApp').controller('ResourceDialogController',
    ['$scope', '$stateParams', '$state', '$uibModalInstance', 'entity', 'Resource', 'Member', 
        function($scope, $stateParams, $state, $modalInstance, entity, Resource, Member) {

        $scope.resource = entity;
        $scope.availableMember = Member.queryUserTenants();
        
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
