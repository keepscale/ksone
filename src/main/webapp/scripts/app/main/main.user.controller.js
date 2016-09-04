'use strict';

angular.module('crossfitApp')
	.controller('MainUserController', function ($rootScope, $scope, $state, $stateParams, $window, TimeSlot, DateUtils, Principal) {
        
		Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
            
            if ($scope.isAuthenticated){

                var w = $window.innerWidth;;
                var mode;
                var view;
                var dStart = new Date();
                var dEnd = new Date();
                if ( w >= 991){
                	mode = 'desktop';
                	view = 'week';
                	dEnd.setDate(dEnd.getDate() + 7); 
                }
                else{
                	mode = 'mobile';
                	view = 'day';
                	dEnd.setDate(dEnd.getDate() + 1);
                }
                
            	var start = DateUtils.formatDateAsDate(dStart);
            	var end = DateUtils.formatDateAsDate(dEnd);
            	
                $state.go('planning', {startDate:start, endDate:end, view:view, mode:mode});
            }
        });
        
    });