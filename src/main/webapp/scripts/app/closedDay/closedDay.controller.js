'use strict';

angular.module('crossfitApp')
    .controller('ClosedDayController', function ($scope, ClosedDay) {
        $scope.closedDays = [];
        $scope.loadAll = function() {
            ClosedDay.query(function(result) {
               $scope.closedDays = result;
            });
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            ClosedDay.get({id: id}, function(result) {
                $scope.closedDay = result;
                $('#deleteClosedDayConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            ClosedDay.delete({id: id},
                function () {
                    $scope.loadAll();
                    $('#deleteClosedDayConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.loadAll();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.closedDay = {name: null, startAt: null, endAt: null, id: null};
        };
    });
