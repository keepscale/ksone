'use strict';

angular.module('crossfitApp')
    .controller('BillController', function ($scope, $window, $state, Bill, Authority, Member, ParseLinks, DateUtils) {
        $scope.bills = [];
        $scope.page = 1;
        $scope.per_page = 50;
        
        $scope.periods = [];
        $scope.generate = {};
        $scope.paymentMethods = [];
        $scope.status = [];


        $scope.quickMemberLike = "";
        $scope.quickMemberSelected = {};
        $scope.quickBillMembers = [];
        
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
            $('#generateBillsConfirmation').modal('hide');
            $('#deleteDraftConfirmation').modal('hide');
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


        $scope.deleteDraft = function (){
            $('#deleteDraftConfirmation').modal('show');
        };
        
        $scope.confirmDeleteDraft = function (id) {
            Bill.deleteDraft({},
                function () {
	                $scope.reset();
	                $scope.clear();
                });
        };
        


        $scope.showQuickSelectMember = function(){
            $('#quickSelectMember').modal('show');
        }
        $scope.clearQuick = function(){

            $scope.quickMemberLike = "";
            $scope.quickMemberSelected = {};
            $scope.quickBillMembers = [];
            
            $('#quickSelectMember').modal('hide');
        }
        
        $scope.selectMemberForQuickBill = function(m){
        	$scope.quickMemberSelected = m;
        }
        
        $scope.searchMemberForQuickBill = function(){

            $scope.quickMemberSelected = {};
            $scope.quickBillMembers = [];
            
        	if ($scope.quickMemberLike.length <=2){
        		return;
        	}
        	 Member.queryQuick({
             	page: 1, per_page: 10, 
             	search: $scope.quickMemberLike, 
             	include_actif: true,
             	include_not_enabled: true,
             	include_bloque: false}, 
             	function(result, headers) {
 	                $scope.quickBillMembers = result;
             	});
        }
        
        $scope.quickSelectMember = function(){
        	var memberId = $scope.quickMemberSelected.id;
        	$scope.clearQuick();
        	
	        $state.go('bill.new', {memberId:memberId});
        }
        
        
        
		$scope.export = function() {
			var params = "t=1";
			
			if ($scope.searchLike != undefined){
				params += "&search="+$scope.searchLike;
			}
            
			$window.open("api/bills.csv?"+params);
		};
        
        $scope.confirmGenerateBillsForm = function(){
        	var params = $scope.generate;
        	


        	params.sinceDate = DateUtils.formatDateAsDate(params.sinceDate);
        	params.untilDate = DateUtils.formatDateAsDate(params.untilDate);
        	
        	if (params.dest == "CSV"){
    			$window.open("api/bills/generate?sinceDate="+params.sinceDate+"&untilDate="+params.untilDate+"&atDayOfMonth="+params.atDayOfMonth+"&status="+params.status+"&dest="+params.dest);
        	}
        	Bill.generate($scope.params,
                function (res) {
                    $scope.reset();
                    $scope.clear();
                });
        };
        
        $scope.init();
    });
