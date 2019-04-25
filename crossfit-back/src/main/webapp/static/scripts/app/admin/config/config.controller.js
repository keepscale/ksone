'use strict';

angular.module('crossfitApp')
    .controller('ConfigController', function ($scope, CrossfitBox) {
 
    	$scope.box = CrossfitBox.current();
    	$scope.contractModels = CrossfitBox.contractmodels();
    	$scope.versionFormats = CrossfitBox.contractmodelsVersionformat();

    	$scope.addContractModel = function(){
    	    $scope.contractModels.push({});
    	}
    	$scope.update = function(){
    		CrossfitBox.update($scope.box);
    	};
    	$scope.saveContractModel = function(c){
    		CrossfitBox.updatecontractmodel(c, function(){
    	        $scope.contractModels = CrossfitBox.contractmodels();
    		});
    	};
    });
