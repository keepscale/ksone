'use strict';

angular.module('crossfitApp')
    .controller('ResourceController', function ($scope, Resource, ParseLinks) {

    	$scope.resources = [];
        $scope.loadAll = function() {
        	Resource.query({
            	page: 1, per_page: -1}, 
            	function(result, headers) {          
	                for (var i = 0; i < result.length; i++) {
	                    $scope.resources.push(result[i]);
	                }
            	});
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.resources = [];
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Resource.get({id: id}, function(result) {
                $scope.resource = result;
                $('#deleteResourceConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
        	Resource.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteResourceConfirmation').modal('hide');
                    $scope.clear();
                });
        };


        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.resource = {id: null};
        };
    });
