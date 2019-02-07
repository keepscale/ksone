'use strict';

angular.module('crossfitApp')
    .controller('SignSubscriptionController', 
    		['$scope', '$stateParams', '$state', 'Member', 'Subscription',
	function($scope, $stateParams, $state, Member, Subscription) {

        $scope.init = function(){
        	$scope.member = Member.get({id : $stateParams.memberId});
        	$scope.sub = Subscription.get({id : $stateParams.subscriptionId});
        }
        
        $scope.readAndApproved = function(){
        	$scope.sub.signatureDate = new Date();
            $('#signature').modal('show');
        }
        
        $scope.init();
	}
]);
