'use strict';

angular.module('crossfitApp').controller('MemberDialogController',
    ['$scope', '$stateParams', '$state', '$uibModalInstance', 'entity', 'Member', 'Membership', 'Booking', 'Authority', 'Bill',
        function($scope, $stateParams, $state, $modalInstance, entity, Member, Membership, Booking, Authority, Bill) {

    	$scope.now = new Date();
    	
    	$scope.view = "infoperso";
    	$scope.showAllForm = ! $state.is('member.editMembership');
        $scope.member = entity;
        $scope.memberships = Membership.query();
        $scope.roles = Authority.query();
        $scope.paymentMethods = Bill.paymentMethods();
        
        

        var onSaveFinished = function (result) {
            $scope.$emit('crossfitApp:memberUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.member.id != null) {
                Member.update($scope.member, onSaveFinished);
            } else {
                Member.save($scope.member, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
        
        $scope.loadBooking = function(){
            if ($stateParams.id != null) {
	        	Booking.getByMember({memberId : $stateParams.id}, function(resultBookings) {
	            	$scope.member.bookings = resultBookings;
	            });
            }
        }


        $scope.addSubscription = function() {
        	//TODO: Recupere les membership par defaut
        	$scope.member.subscriptions.push({
        		subscriptionStartDate : new Date()
        	});
        };
        

        $scope.deleteSubscription = function(subscription) {
        	var idx = $scope.member.subscriptions.indexOf(subscription);
        	$scope.member.subscriptions.splice(idx, 1);
        };
        
        $scope.calculateEndDate = function(subscription){
        	if (!subscription.subscriptionEndDate){
        		$scope.addMonthToSubscriptionEndDate(subscription);
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

        $scope.loadBooking();
}]);
