'use strict';

angular.module('crossfitApp')
    .controller('SignSubscriptionController', 
    		['$scope', '$stateParams', '$state', '$rootScope', 'Member', 'Signature', 'ContractModel',
	function($scope, $stateParams, $state, $rootScope, Member, Signature, ContractModel) {
		
        $scope.init = function(){
        	$scope.member = Member.get({id : $stateParams.memberId}, function(result){
                $scope.member = result;
        	    $scope.sub = $scope.member.subscriptions.find(m => m.id == $stateParams.subscriptionId);
                ContractModel.data({id: $scope.sub.contractModel.id}, function(data){
                    $scope.contractModel = data;
                });
        	});

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
            Signature.signsubscription($scope.subSigned, function(){;
            	$scope.init();
            });
        }
        
        $scope.init();
	}
]);
