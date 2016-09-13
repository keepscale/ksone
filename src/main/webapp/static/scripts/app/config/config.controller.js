'use strict';

angular.module('crossfitApp')
    .controller('ConfigController', function ($scope, CrossfitBox, ParseLinks) {
 
    	$scope.box = CrossfitBox.current();
    	
    	

    	$scope.update = function(){
    		CrossfitBox.update($scope.box);
    	};
    });
