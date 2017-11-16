'use strict';

angular.module('crossfitApp').controller('BillDialogController',
    ['$scope', '$stateParams', '$state', '$uibModalInstance', 'entity', 'Bill', 'Member',
        function($scope, $stateParams, $state, $modalInstance, entity, Bill, Member) {

    	$scope.view = "default";
        $scope.bill = entity;

        $scope.paymentMethods = [];
        $scope.status = [];

        $scope.quickMemberLike = "";
        $scope.quickMemberSelected = {};
        $scope.quickBillMembers = [];

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
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
            
        	if ($scope.quickMemberLike.length <=3){
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
        	$scope.bill.member = $scope.quickMemberSelected;
        	$scope.bill.displayAddress = 
        		$scope.bill.member.address + "\n" + 
        		$scope.bill.member.zipCode + " " +
        		$scope.bill.member.city;
        	
        	$scope.bill.displayName = 
        		$scope.bill.member.title + " " +
        		$scope.bill.member.firstName + " " +
        		$scope.bill.member.lastName;
        	
        	$scope.clearQuick();
        }

        
        $scope.calculateTotal = function(line){
        	return line.quantity * line.priceTaxIncl;
        }
        

        $scope.calculateTotalBill = function(){
        	if ($scope.bill.lines == null){
        		return;
        	}
        	var total = 0;
            for (var i = 0; i < $scope.bill.lines.length; i++) {
            	total += ($scope.bill.lines[i].quantity * $scope.bill.lines[i].priceTaxIncl);
            }
            return total;
        }
        
        $scope.addLine = function(){
        	$scope.bill.lines.push({
        		quantity: 1
        	});
        }
        
        
        $scope.init = function(){

        	Bill.paymentMethods({}, function(result, headers) {
            	$scope.paymentMethods = result;
            });
            
            Bill.status({}, function(result, headers) {
            	$scope.status = result;
            });
        }

        
        $scope.init();
        
}]);
