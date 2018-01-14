'use strict';

angular.module('crossfitApp').controller('BillDialogController',
    ['$scope', '$window', '$stateParams', '$state', '$uibModalInstance', 'entity', 'Bill', 'Member', 'Product', 'Membership','DateUtils',
        function($scope, $window, $stateParams, $state, $modalInstance, entity, Bill, Member, Product, Membership, DateUtils) {

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

        $scope.print = function() {
			$window.open("api/bills/"+$scope.bill.id+".pdf");
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
 	        		$scope.member = member;
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

        $scope.calculateCssClassSubscription = function(subscription){
        	var now = $scope.bill.effectiveDate;
        	var end = DateUtils.toUTCDate(new Date(subscription.subscriptionEndDate)).getTime();
        	var start = DateUtils.toUTCDate(new Date(subscription.subscriptionStartDate)).getTime();
        	
        	return end >= now && now >= start ? 'actif' : "inactif";	
        }
        
        $scope.selectElementForBillLine = function(line, element){
        	line.label = element.name;
        	line.priceTaxIncl = element.priceTaxIncl;
        	line.taxPerCent = element.taxPerCent;
        }
        
        $scope.selectSubscriptionForBillLine = function(line, subscription){
        	$scope.selectElementForBillLine(line, subscription.membership);

        	var firstDay = new Date($scope.bill.effectiveDate.getFullYear(), $scope.bill.effectiveDate.getMonth(), 1);
        	var lastDay  = new Date($scope.bill.effectiveDate.getFullYear(), $scope.bill.effectiveDate.getMonth() + 1, 0);
        	line.subscription = subscription;
        	line.periodStart = subscription.subscriptionStartDate < firstDay ? firstDay : subscription.subscriptionStartDate;
        	line.periodEnd = subscription.subscriptionEndDate > lastDay ? lastDay : subscription.subscriptionEndDate;
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
