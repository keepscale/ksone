'use strict';

angular.module('crossfitApp').controller('TimeSlotDialogSupprController',
    ['$scope', '$stateParams', '$state', '$modalInstance', 'entity', 'TimeSlot',
        function($scope, $stateParams, $state, $modalInstance, entity, TimeSlot) {

        $scope.timeSlot = entity;
        $scope.load = function(id) {
            TimeSlot.get({id : id}, function(result) {
                $scope.timeSlot = result;
            });
        };


        var onDeleteFinished = function (result) {
            $scope.$emit('crossfitApp:timeSlotDelete', result);
            $modalInstance.close(result);
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };

        $scope.confirmDelete = function () {
        	if ($scope.timeSlot.id != null) {
                TimeSlot.delete({id: $scope.timeSlot.id}, onDeleteFinished);
        	}
        };
}]);
