'use strict';

angular.module('crossfitApp')
    .controller('NavbarController', function ($scope, $location, $state, $window, DateUtils, Auth, Principal, ENV) {
    	
    	Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });
    	
        $scope.$state = $state;
        $scope.inProduction = ENV === 'prod';

        $scope.logout = function () {
        	$scope.account = null;
            Auth.logout();
            $state.go('home');
        };
        $scope.planning = function () {
        	
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
             
        };
    });
