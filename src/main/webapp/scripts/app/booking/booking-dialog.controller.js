'use strict';

angular.module('crossfitApp').controller('BookingDialogController',
    ['$scope', '$stateParams','$window', '$uibModalInstance', 'entity', 'Booking', 'DateUtils',
        function($scope, $stateParams, $window, $modalInstance, entity, Booking, DateUtils) {

  
//    	Availability.availability({id : entity.id, date : entity.date}, function(result) {
//    		$scope.availability = result;
//        });

    	$scope.isReservable = entity.message;
    	

        var onSaveFinished = function (result) {
        	console.log(result);
        	$scope.isReservable = false;
            $scope.$emit('crossfitApp:timeSlotUpdate', result);
            $modalInstance.close(result);
        };
        
        var erreur = function(result){
        	$window.alert(result.data.message);
        };

        $scope.subscribe = function () {
        	console.log(entity);
        	$scope.booking = {
                	timeslot: null,
                	timeslotId: entity.timeslotId,
                	date: new Date(entity.date),
                	owner: {}
                };
        	Booking.save($scope.booking , onSaveFinished, erreur);
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
