'use strict';

angular.module('crossfitApp')
    .controller('MemberController', function ($scope, Member, ParseLinks) {
        $scope.members = [];
        $scope.per_page = 20;
        $scope.include_actif = true;
        $scope.include_not_ennabled = true;
        $scope.include_bloque = false;
        $scope.loadAll = function() {
            Member.query({
            	page: 1, per_page: $scope.per_page, 
            	search: $scope.searchLike, 
            	include_actif: $scope.include_actif,
            	include_not_enabled: $scope.include_not_ennabled,
            	include_bloque: $scope.include_bloque}, 
            	function(result, headers) {
          
	                $scope.links = ParseLinks.parse(headers('link'));
	                $scope.totalMember = headers('X-Total-Count');
	                for (var i = 0; i < result.length; i++) {
	                    $scope.members.push(result[i]);
	                }
            	});
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.members = [];
            $scope.loadAll();
        };
        $scope.search = function() {
            $scope.members = [];
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.lock = function (id) {
            Member.get({id: id}, function(result) {
                $scope.member = result;
                $('#lockMemberConfirmation').modal('show');
            });
        };

        $scope.confirmLock = function (id) {
            Member.lock({id: id},
                function () {
                    $scope.reset();
                    $('#lockMemberConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.resetAccount = function (id){
            Member.get({id: id}, function(result) {
                $scope.member = result;
                $('#resetAccountMemberConfirmation').modal('show');
            });
        };
        
        $scope.confirmResetAccount = function (id) {
            Member.resetaccount({id: id},
                function () {
                    $scope.reset();
                    $('#resetAccountMemberConfirmation').modal('hide');
                    $scope.clear();
                });
        };

        $scope.refresh = function () {
            $scope.reset();
            $scope.clear();
        };

        $scope.clear = function () {
            $scope.member = {telephonNumber: null, sickNoteEndDate: null, membershipStartDate: null, membershipEndDate: null, level: null, id: null};
        };
    });
