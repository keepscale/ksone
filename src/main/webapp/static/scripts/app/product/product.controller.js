'use strict';

angular.module('crossfitApp')
    .controller('ProductController', function ($scope, Product, ParseLinks) {

    	$scope.products = [];
        $scope.loadAll = function() {
        	Product.query({
            	page: 1, per_page: -1}, 
            	function(result, headers) {          
	                for (var i = 0; i < result.length; i++) {
	                    $scope.products.push(result[i]);
	                }
            	});
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.products = [];
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Product.get({id: id}, function(result) {
                $scope.product = result;
                $('#deleteProductConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
        	Product.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteProductConfirmation').modal('hide');
                    $scope.clear();
                });
        };


        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.product = {id: null};
        };
    });
