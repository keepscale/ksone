'use strict';

angular.module('crossfitApp').controller('BillDialogController',
    ['$scope', '$stateParams', '$state', '$uibModalInstance', 'entity', 'Bill', 'Member', 'Product', 'Membership',
        function($scope, $stateParams, $state, $modalInstance, entity, Bill, Member, Product, Membership) {

    	$scope.view = "default";
        $scope.bill = entity;

        $scope.paymentMethods = [];
        $scope.status = [];



        var onSaveFinished = function (result) {
            $scope.$emit('crossfitApp:billUpdate', result);
            $modalInstance.close(result);
            
            $state.go("bill.edit", {id:result.id})
        };

        
        $scope.save = function () {
            if ($scope.bill.id != null) {
                alert("Cette facture ne peut pas être modifiée.")
            } else {
            	Bill.save($scope.bill, onSaveFinished);
            }
        };
        
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
 	        		$scope.bill.member = {id:member.id};
 	        		
 	            	$scope.bill.displayAddress = 
 	            		member.address + "\n" + 
 	            		member.zipCode + " " +
 	            		member.city;
 	            	
 	            	$scope.bill.displayName = 
 	            		member.title + " " +
 	            		member.firstName + " " +
 	            		member.lastName;
 	            	
 	        	});
             }
        }
        
        $scope.selectElementForBillLine = function(line, element){
        	line.label = element.name;
        	line.priceTaxIncl = element.price;
        }
        
        $scope.init = function(){

        	$scope.refreshMember();
            
        	Bill.paymentMethods({}, function(result, headers) {
            	$scope.paymentMethods = result;
            });
            
            Bill.status({}, function(result, headers) {
            	$scope.status = result;
            });

            Product.query({}, function (result, headers){
            	$scope.products = result;
            });

            Membership.query({}, function (result, headers){
            	$scope.membership = result;
            });
        }

        
        $scope.init();
        
}]);
