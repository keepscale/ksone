'use strict';

angular.module('crossfitApp').controller('BillDialogController',
    ['$scope', '$stateParams', '$state', '$uibModalInstance', 'entity', 'Bill', 'Member',
        function($scope, $stateParams, $state, $modalInstance, entity, Bill, Member) {

    	$scope.view = "default";
        $scope.bill = entity;

        $scope.paymentMethods = [];
        $scope.status = [];

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
        
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
        
        $scope.refreshMember = function(){
        	 if ($stateParams.memberId != null) {
             	
 	        	Member.get({id : $stateParams.memberId}, function(member) {

 	            	$scope.bill.member = member;
 	            	$scope.bill.displayAddress = 
 	            		$scope.bill.member.address + "\n" + 
 	            		$scope.bill.member.zipCode + " " +
 	            		$scope.bill.member.city;
 	            	
 	            	$scope.bill.displayName = 
 	            		$scope.bill.member.title + " " +
 	            		$scope.bill.member.firstName + " " +
 	            		$scope.bill.member.lastName;
 	            	
 	        	});
             }
        }
        
        $scope.init = function(){

        	$scope.refreshMember();
            
        	Bill.paymentMethods({}, function(result, headers) {
            	$scope.paymentMethods = result;
            });
            
            Bill.status({}, function(result, headers) {
            	$scope.status = result;
            });
        }

        
        $scope.init();
        
}]);
