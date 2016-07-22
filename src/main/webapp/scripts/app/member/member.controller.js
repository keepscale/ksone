'use strict';

angular.module('crossfitApp')
    .controller('MemberController', function ($scope, Member, ParseLinks) {
        $scope.members = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Member.query({page: $scope.page, per_page: 20}, function(result, headers) {
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
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.loadAll();

        $scope.delete = function (id) {
            Member.get({id: id}, function(result) {
                $scope.member = result;
                $('#deleteMemberConfirmation').modal('show');
            });
        };

        $scope.confirmDelete = function (id) {
            Member.delete({id: id},
                function () {
                    $scope.reset();
                    $('#deleteMemberConfirmation').modal('hide');
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
        $scope.massActivation = function (){
            $('#massActivationConfirmation').modal('show');
        };
        
        $scope.confirmMassActivation = function () {
            Member.massActivation({},
                function () {
                    $scope.reset();
                    $('#massActivationConfirmation').modal('hide');
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
