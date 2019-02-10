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
        	$scope.subSigned = {
    			id: $scope.sub.id,
      			signatureDate: new Date(),
      			signatureDataEncoded : null        			
        	};
            $('#signature').modal('show');
        }
        
        $scope.sign = function(){
            $('#signature').modal('hide');
        	Subscription.sign($scope.subSigned);
            $scope.init();
        }
        
        $scope.init();
	}
]);
