'use strict';

angular.module('crossfitApp')
    .controller('TimeSlotTypeController', function ($scope, TimeSlotType, ParseLinks) {

    	$scope.timeSlotTypes = [];
        $scope.loadAll = function() {
        	TimeSlotType.query({
            	page: 1, per_page: -1}, 
            	function(result, headers) {          
	                for (var i = 0; i < result.length; i++) {
	                    $scope.timeSlotTypes.push(result[i]);
	                }
            	});
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.timeSlotTypes = [];
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            TimeSlotType.get({id: id}, function(result) {
                $scope.timeSlotType = result;
                $('#deleteTimeSlotTypeConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
        	TimeSlotType.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteTimeSlotTypeConfirmation').modal('hide');
                    $scope.clear();
                });
        };


        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.timeSlotType = {id: null};
        };
    });
