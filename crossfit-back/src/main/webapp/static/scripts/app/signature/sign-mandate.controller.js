'use strict';

angular.module('crossfitApp')
    .controller('SignMandateController', 
    		['$scope', '$stateParams', '$state', '$rootScope', 'Member', 'Signature',
	function($scope, $stateParams, $state, $rootScope, Member, Signature) {

        $scope.init = function(){
        	
        	$scope.logoUrl = $rootScope.box.billLogoUrl;
        	
        	$scope.member = Member.get({id : $stateParams.memberId}, function(result){
                $scope.member = result;
            	$scope.mandate = $scope.member.mandates.find(m => m.id == $stateParams.mandateId);

                $scope.mandateSigned = {
                    id: $scope.mandate.id,
                    signatureDate: $scope.mandate.signatureDate != null ? $scope.mandate.signatureDate : new Date(),
                    signatureDataEncoded : $scope.mandate.signatureDataEncoded
                };
            });
        	
        }

        $scope.sign = function(){
            Signature.signmandate($scope.mandateSigned);
            $scope.init();
        }
        
        $scope.init();
	}
]);
