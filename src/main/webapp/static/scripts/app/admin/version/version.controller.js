'use strict';

angular.module('crossfitApp')
    .controller('VersionController', function ($scope, AdminVersion) {
     	
    	$scope.versions = AdminVersion.query();
    		
    });
