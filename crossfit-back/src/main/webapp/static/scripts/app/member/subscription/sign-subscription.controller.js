'use strict';

angular.module('crossfitApp')
    .controller('SignSubscriptionController', 
    		['$scope', '$stateParams', '$state',
	function($scope, $stateParams, $state) {

        $scope.init = function(){
        	console.log("Loading subscription " + $stateParams.id)
        }
        
        $scope.init();
	}
]);
