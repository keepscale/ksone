'use strict';

angular.module('crossfitApp')
    .controller('MainManagerController', function ($scope, Principal, Planning, Member) {
    	$scope.planning = [];
        $scope.page = 0;
        $scope.selectedIndex = 0;
        $scope.quickbooking = {};
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
            $scope.loadAll();
        });
        
        
        $scope.loadAll = function() {
            Planning.query({page: $scope.page, per_page: 14}, function(result, headers) {
                for (var i = 0; i < result.days.length; i++) {
                    $scope.planning.push(result.days[i]);
                }
            });
        };

        $scope.select = function(index) {
            $scope.selectedIndex = index;
        };

        $scope.showQuickAddBooking = function(slot){
            $scope.quickbooking = {
            	timeSlot: slot,
            	date: $scope.planning[$scope.selectedIndex].date
            };
              
            $('#quickAddBooking').modal('show');
        }
        $scope.searchUser = function(){
        	if ($scope.quickbookingLike.length >= 3)
        	Member.query({
              	page: 1, per_page: 5, 
              	search: $scope.quickbookingLike, 
              	include_actif: true,
              	include_not_enabled: true,
              	include_bloque: false}, 
              	function(result, headers) {
            
  	                $scope.quickbookingUsers = [];
  	                for (var i = 0; i < result.length; i++) {
  	                    $scope.quickbookingUsers.push(result[i]);
  	                }
              	});
        }
        $scope.quickAddBooking = function(){
        	alert("quickAddBooking" + $scope.quickbooking.date)        	
            $('#quickAddBooking').modal('hide');  	
        }
    })
    .controller('MainUserController', function ($scope, Principal, Planning) {
        Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });
    });
