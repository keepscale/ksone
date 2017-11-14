'use strict';

angular.module('crossfitApp')
    .controller('BillController', function ($scope, $window, Bill, Authority, ParseLinks) {
        $scope.bills = [];
        $scope.page = 1;
        $scope.per_page = 200;
        
        $scope.periods = [];
        $scope.generate = {};
        $scope.paymentMethods = [];
        $scope.status = [];
        
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
    	
        $scope.clear = function(){
        	var date = new Date();
        	var firstDay = new Date(date.getFullYear(), date.getMonth(), 1);
        	var lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0);
            $scope.generate = {
            	atDayOfMonth: 1,
            	status: 'DRAFT',
            	paymentMethod: 'DIRECT_DEBIT',
            	sinceDate: firstDay,
            	untilDate: lastDay
            };
            $('#resetAccountMemberConfirmation').modal('hide');
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

            
            Bill.periods({}, function(result, headers) {
            	$scope.periods = result;
            });

            Bill.paymentMethods({}, function(result, headers) {
            	$scope.paymentMethods = result;
            });
            
            Bill.status({}, function(result, headers) {
            	$scope.status = result;
            });
            
        	$scope.loadAll();
        	$scope.clear();
        }
        

        $scope.prepareGenerate = function (){
            $('#generateBillsConfirmation').modal('show');
        };
        
        $scope.confirmGenerateBillsForm = function(){
        	Bill.generate($scope.generate,
                function (res) {
                    $scope.reset();
                    $scope.clear();
                    alert(res+" factures ont été générées");
                });
        };
        
        $scope.init();
    });
