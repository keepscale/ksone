'use strict';

angular.module('crossfitApp').controller('ClosedDayDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'ClosedDay',
        function($scope, $stateParams, $modalInstance, entity, ClosedDay) {

        $scope.closedDay = entity;
        $scope.load = function(id) {
            ClosedDay.get({id : id}, function(result) {
                $scope.closedDay = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('crossfitApp:closedDayUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.closedDay.id != null) {
                ClosedDay.update($scope.closedDay, onSaveFinished);
            } else {
                ClosedDay.save($scope.closedDay, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
