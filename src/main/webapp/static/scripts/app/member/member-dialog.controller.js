'use strict';

angular.module('crossfitApp').controller('MemberDialogController',
    ['$scope', '$stateParams', '$state', '$uibModalInstance', 'entity', 'Member', 'Membership', 'Booking',
        function($scope, $stateParams, $state, $modalInstance, entity, Member, Membership, Booking) {

    	$scope.view = "infoperso";
    	$scope.showAllForm = ! $state.is('member.editMembership');
        $scope.member = entity;
        $scope.memberships = Membership.query();
        
        

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
        	Booking.getByMember({memberId : $stateParams.id}, function(resultBookings) {
            	$scope.member.bookings = resultBookings;
            });
        }


        $scope.addSubscription = function() {
        	//TODO: Recupere les membership par defaut
        	$scope.member.subscriptions.push({
        		subscriptionStartDate : new Date()
        	});
        };
        

        $scope.deleteSubscriptionAtIndex = function(idx) {
        	$scope.member.subscriptions.splice(idx, 1);
        };
        
        $scope.calculateEndDate = function(subscription){
        	if (!subscription.subscriptionEndDate){
        		$scope.addMonthToSubscriptionEndDate(subscription);
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
        

        $scope.loadBooking();
}]);
