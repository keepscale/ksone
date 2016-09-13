'use strict';

angular.module('crossfitApp').controller('TimeSlotTypeDialogController',
    ['$scope', '$stateParams', '$state', '$uibModalInstance', 'entity', 'TimeSlotType', 
        function($scope, $stateParams, $state, $modalInstance, entity, TimeSlotType) {

        $scope.timeSlotType = entity;
        $scope.timeSlotTypes = TimeSlotType.query();
        
        $scope.load = function(id) {
            TimeSlotType.get({id : id}, function(result) {
                $scope.timeSlotType = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('crossfitApp:timeSlotTypeUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.timeSlotType.id != null) {
                TimeSlotType.update($scope.timeSlotType, onSaveFinished);
            } else {
                TimeSlotType.save($scope.timeSlotType, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
        
        
}]);
