'use strict';

angular.module('crossfitApp').controller('TimeSlotDialogBookingController',
    ['$scope', '$stateParams', '$state', '$uibModalInstance', 'entity', 'TimeSlot', 'TimeSlotType',
        function($scope, $stateParams, $state, $modalInstance, entity, TimeSlot, TimeSlotType) {

        $scope.timeSlotTypes = TimeSlotType.query();
        $scope.timeSlot = entity;
        $scope.load = function(id) {
            TimeSlot.get({id : id}, function(result) {
                $scope.timeSlot = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('crossfitApp:timeSlotUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.timeSlot.id != null) {
                TimeSlot.update($scope.timeSlot, onSaveFinished);
            } else {
                TimeSlot.save($scope.timeSlot, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };

        $scope.delete = function () {	
            $modalInstance.dismiss('cancel');
        	$state.go('timeSlot.delete', {id:$scope.timeSlot.id});
        };
}]);
