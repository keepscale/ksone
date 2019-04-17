'use strict';

angular.module('crossfitApp')
    .controller('SignMandateController', 
    		['$scope', '$stateParams', '$state', '$rootScope', 'Member', 'Signature',
	function($scope, $stateParams, $state, $rootScope, Member, Signature) {

    	$scope.box = {
    		sepa : {
    			logoURL: "/assets/images/sepa-logo.png",
    			headerTitle: "Mandat de prélèvement SEPA",
    			legalText: "En signant ce formulaire de mandat, vous autorisez la société KOOPER inc - CROSSFIT NANCY Affiliate\
							à envoyer des instructions à votre banque pour débiter votre compte,\
							et votre banque à débiter votre compte conformément aux instructions de KOOPER inc - CROSSFIT NANCY Affiliate.\
							Vous bénéficiez du droit d’être remboursé par votre banque selon les conditions décrites dans la convention que vous avez passée avec elle.\
							<br/>\
							Une demande de remboursement doit être présentée : dans les 8 semaines suivant la date de débit de votre compte pour un prélèvement autorisé,\
							sans tarder et au plus tard dans les 13 mois en cas de prélèvement non authorisé,\
							et vos droits concernant le présent mandat sont expliqués dans un document que vous pouvez obtenir auprès de votre banque.",
        		beneficiaire : "<b>Kooper inc</b> <br/>24/26 bd du 26ème Régiment d’Infanterie <br/>54000 Nancy"
    		}		
    	}
    			
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
            Signature.signmandate($scope.mandateSigned, function(){;
            	$scope.init();
            })
        }
        
        $scope.init();
	}
]);
