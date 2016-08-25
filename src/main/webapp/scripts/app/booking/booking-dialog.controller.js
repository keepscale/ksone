'use strict';

angular.module('crossfitApp').controller('BookingDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'Booking', 'DateUtils',
        function($scope, $stateParams, $modalInstance, entity, Booking, DateUtils) {

  
//    	Availability.availability({id : entity.id, date : entity.date}, function(result) {
//    		$scope.availability = result;
//        });

    	$scope.isReservable = entity.message;
    	

        var onSaveFinished = function (result) {
        	$scope.isReservable = false;
            $scope.$emit('crossfitApp:timeSlotUpdate', result);
            $modalInstance.close(result);
        };

        $scope.subscribe = function () {
        	Booking.save({id: entity.id, date : entity.date}, onSaveFinished);
        };
        
        $scope.unSubscribe = function (id) {
	          if (id != null) {
	              Booking.delete({id: id}, onSaveFinished);
	          } 
        };
        
        $scope.clear = function() {
        	$scope.isReservable = false;
            $modalInstance.dismiss('cancel');
        };
}]);
