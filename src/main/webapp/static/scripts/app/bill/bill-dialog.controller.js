'use strict';

angular.module('crossfitApp').controller('BillDialogController',
    ['$scope', '$stateParams', '$state', '$uibModalInstance', 'entity', 'Bill',
        function($scope, $stateParams, $state, $modalInstance, entity, Bill) {

    	$scope.view = "default";
        $scope.bill = entity;
        

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
        
}]);
