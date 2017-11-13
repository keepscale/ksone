'use strict';

angular.module('crossfitApp')
    .controller('BillController', function ($scope, $window, Bill, Authority, ParseLinks) {
        $scope.bills = [];
        $scope.page = 1;
        $scope.per_page = 20;
        
        $scope.loadAll = function() {
            Bill.query({
            	page: $scope.page, per_page: $scope.per_page, 
            	search: $scope.searchLike}, 
            	function(result, headers) {
          
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.totalBill = headers('X-Total-Count');
	                for (var i = 0; i < result.length; i++) {
	                    $scope.bills.push(result[i]);
	                }
            	});
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.bills = [];
            $scope.loadAll();
        };
        $scope.search = function() {
            $scope.reset();
        };

        $scope.refresh = function () {
            $scope.reset();
        };

        		
        $scope.init = function(){
        	$scope.loadAll();
        }
        
        $scope.init();
    });
