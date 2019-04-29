'use strict';

angular.module('crossfitApp').controller('MemberDialogController',
    ['$q', '$scope', '$stateParams', '$state', '$uibModalInstance', 'Member', 'Membership', 'Booking', 'Authority', 'Bill', 'ContractModel',
        function($q, $scope, $stateParams, $state, $modalInstance, Member, Membership, Booking, Authority, Bill, ContractModel) {

    	$scope.now = new Date();

        $scope.member = {};
        $scope.memberBookings = [];
        $scope.view = $stateParams.view;

        if (!$stateParams.id){
            $scope.member = {
                title: 'MR', langKey: 'fr',
                telephonNumber: null, sickNoteEndDate: null,
                membershipStartDate: null, membershipEndDate: null,
                level: null, id: null,
                roles : ["ROLE_USER"],
                subscriptions : [
                    {
                        subscriptionStartDate : new Date(),
                        bookingCount: 0
                    }
                ],
                mandates: []
            };
        }

        $scope.memberships = Membership.query();
        $scope.roles = Authority.query();
        $scope.paymentMethods = Bill.paymentMethods();
        $scope.contractModels = ContractModel.query();
        
        $scope.$on('$locationChangeSuccess', function(event) { 
        	$scope.view = $stateParams.view;
        });

        $scope.isLoading = function(){
        	return $scope.member==null || $scope.memberBookings == null;
        }
        
        $scope.save = function (andQuit) {
        	var callBack = function(result){
                $scope.$emit('crossfitApp:memberUpdate', result);
            	if (andQuit){
                    $modalInstance.close(result);
            	}
            	else{
            	    if (!$stateParams.id){
                        $state.go('member.edit', {id: result.id});
            	    }
            	    else{
            	    	$scope.refresh();
            	    }
            	}
            };
            
            if ($scope.member.id != null) {
                Member.update($scope.member, callBack);
            } else {
                Member.save($scope.member, callBack);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };

        $scope.refresh = function(){
            $scope.loadMember();
            $scope.loadBooking();
        }
        
        $scope.loadMember = function(){
            if ($stateParams.id != null) {
                $scope.member = null;
                Member.get({id : $stateParams.id}, function(result){
                    $scope.member = result;
                });
            }
        }
        $scope.loadBooking = function(){
            if ($stateParams.id != null) {
                $scope.memberBookings = null;
	        	Booking.getByMember({memberId : $stateParams.id}, function(resultBookings) {
	            	$scope.memberBookings = resultBookings;
	            });
            }
        }

        $scope.openedSubscription = [];
        $scope.openedMandats = [];

        $scope.isOpen = function(arr, obj){
        	return arr.indexOf(obj) > -1;
        }

        
        $scope.toggle = function(event, arr, obj){

        	
        	var idx = arr.indexOf(obj);

			// Is currently selected
			if (idx > -1) {
				arr.splice(idx, 1);
			}

			// Is newly selected
			else {
				arr.push(obj);
			}
        }

        $scope.addSubscription = function() {
        	//TODO: Recupere les membership par defaut
        	$scope.member.subscriptions.push({
        		subscriptionStartDate : new Date(),
        		bookingCount: 0
        	});
        };
        $scope.deleteSubscription = function(subscription) {
        	var idx = $scope.member.subscriptions.indexOf(subscription);
        	$scope.member.subscriptions.splice(idx, 1);
        };


        $scope.addMandat = function() {
        	$scope.member.mandates.push({
        		rum : 'toto',
        		ics: 'ABS',
        		status: 'DRAFT'
        	});
        };

        $scope.deleteMandate = function(mandate) {
        	var idx = $scope.member.mandates.indexOf(mandate);
        	$scope.member.mandates.splice(idx, 1);
        };
        
        $scope.onSelectMembership = function(subscription){
        	if (!subscription.subscriptionEndDate){
        		$scope.addMonthToSubscriptionEndDate(subscription);
        	}
        	if (!subscription.priceTaxIncl && subscription.membership){
        		subscription.priceTaxIncl = subscription.membership.priceTaxIncl;
        	}
        }
        $scope.caculateCssDate = function(actual, prev, next, dir){

        	if (actual != null && actual.subscriptionEndDate != null){
	        	if (actual.subscriptionEndDate.getTime() < actual.subscriptionStartDate.getTime()){
	        		return "has-error";
	        	}
        	}
        	
        	var compareTo = dir === 'asc' ? prev : next;
        	
        	if (compareTo != null && compareTo.subscriptionEndDate != null && 
    				compareTo.subscriptionEndDate.getTime() > actual.subscriptionStartDate.getTime()){
    			return "has-error";
    		}
        }
        
        $scope.addMonthToSubscriptionEndDate = function(subscription){
        	if(!subscription.membership){
        		return;
        	}
        	
        	var d = new Date(subscription.subscriptionEndDate ? subscription.subscriptionEndDate : subscription.subscriptionStartDate);
        	
        	d.setMonth(d.getMonth() +  subscription.membership.nbMonthValidity);
        	
        	subscription.subscriptionEndDate = d;
        }
        $scope.paymentMethodChanged = function(subscription){
        	if (subscription.paymentMethod=='DIRECT_DEBIT' && !subscription.directDebit){
        		subscription.directDebit = {};
        		if ($scope.member.mandates.length > 0){ //Si des mandats, on initialise les valeurs
            		var defaultDate = new Date(subscription.subscriptionStartDate.getFullYear(), subscription.subscriptionStartDate.getMonth()+1, 0);
            		subscription.directDebit = {
            				atDayOfMonth: 5,
            				afterDate: defaultDate,
            				amount: subscription.membership.priceTaxIncl
            		}
        		}
        	}
        }
        $scope.cannotEditSubscription = function(s){
        	return s.signatureDate != null;
        }
        $scope.isSubscriptionInvalid = function(s){
        	var invalid = !s.membership || 
        		!s.subscriptionEndDate || !s.subscriptionStartDate &&
        		!s.paymentMethod;
        	
        	if (!invalid && $scope.mustFillDirectDebitInfo(s)){
        		var dd = s.directDebit;
        		invalid = !dd.afterDate ||
	        		!dd.atDayOfMonth ||
	        		dd.firstPaymentTaxIncl==null ||
	        		dd.amount==null ||
	        		!dd.firstPaymentMethod ||
	        		!dd.mandate;
        	}
        	
        	return invalid;
        }
        
        $scope.mustFillDirectDebitInfo = function(s){
        	var directDebit = s.directDebit || {};
    		var must =	s.paymentMethod=='DIRECT_DEBIT' && 
    			(
					directDebit.afterDate ||
					directDebit.atDayOfMonth ||
					directDebit.amount != null ||
					directDebit.firstPaymentTaxIncl != null ||
					directDebit.firstPaymentMethod ||
					directDebit.mandate
    		);
    		return must;
        }
        
        $scope.showView = function(viewname){
        	$scope.view = viewname;
        }
        

        $scope.quickDeleteBooking = function(booking){
        	if (confirm("Supprimer la réservation de "+booking.title+" ?")){
            	Booking.delete({id : booking.id}, function(){
            		$scope.loadBooking();
            	});
        	}
        }
        
        
        $scope.toggleSelectedRole = function toggleSelectedRole(role) {
			var idx = $scope.member.roles.indexOf(role);

			// Is currently selected
			if (idx > -1) {
				$scope.member.roles.splice(idx, 1);
			}

			// Is newly selected
			else {
				$scope.member.roles.push(role);
			}
		};

		$scope.refresh();

}]);
