'use strict';

angular.module('crossfitApp').controller('ProductDialogController',
    ['$scope', '$stateParams', '$state', '$uibModalInstance', 'entity', 'Product', 'TimeSlotType', 
        function($scope, $stateParams, $state, $modalInstance, entity, Product, TimeSlotType) {

        $scope.product = entity;
        
        $scope.load = function(id) {
            Product.get({id : id}, function(result) {
                $scope.product = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('crossfitApp:productUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.product.id != null) {
                Product.update($scope.product, onSaveFinished);
            } else {
                Product.save($scope.product, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
}]);
