'use strict';

angular.module('crossfitApp')
    .controller('MembershipController', function ($scope, Membership, ParseLinks) {

    	$scope.memberships = [];
        $scope.loadAll = function() {
        	Membership.query({
            	page: 1, per_page: -1}, 
            	function(result, headers) {          
	                for (var i = 0; i < result.length; i++) {
	                    $scope.memberships.push(result[i]);
	                }
            	});
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.memberships = [];
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Membership.get({id: id}, function(result) {
                $scope.membership = result;
                $('#deleteMembershipConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
        	Membership.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteMembershipConfirmation').modal('hide');
                    $scope.clear();
                });
        };


        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.membership = {id: null};
        };
    });
