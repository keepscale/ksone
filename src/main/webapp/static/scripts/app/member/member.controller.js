'use strict';

angular.module('crossfitApp')
    .controller('MemberController', function ($scope, $window, Member, Membership, ParseLinks) {
        $scope.members = [];
        $scope.page = 1;
        $scope.per_page = 20;
        $scope.include_actif = true;
        $scope.include_not_ennabled = true;
        $scope.include_bloque = false;
        $scope.memberships = [];
        $scope.selectedMemberships = [];
        
        $scope.loadAll = function() {
            Member.query({
            	page: $scope.page, per_page: $scope.per_page, 
            	search: $scope.searchLike, 
            	include_memberships: $scope.selectedMemberships,
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
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
        $scope.reset = function() {
            $scope.page = 1;
            $scope.members = [];
            $scope.loadAll();
        };
        $scope.search = function() {
            $scope.reset();
        };

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
        
        $scope.toggleSelectedMembership = function toggleSelectedMembership(membershipId) {
			var idx = $scope.selectedMemberships.indexOf(membershipId);

			// Is currently selected
			if (idx > -1) {
				$scope.selectedMemberships.splice(idx, 1);
			}

			// Is newly selected
			else {
				$scope.selectedMemberships.push(membershipId);
			}
		};

					        
		$scope.export = function() {
			var params = "include_actif="+$scope.include_actif+
				"&include_not_enabled="+$scope.include_not_ennabled+
				"&include_bloque="+$scope.include_bloque;
			
			if ($scope.searchLike != undefined){
				params += "&search="+$scope.searchLike;
			}

            for (var i = 0; i < $scope.selectedMemberships.length; i++) {
            	params += "&include_memberships=" + $scope.selectedMemberships[i];
            }
        	
            
            
			$window.open("api/members.csv?"+params);
		};
		
        $scope.init = function(){
        	Membership.query({}, function(result){
            	$scope.memberships = result;
                for (var i = 0; i < result.length; i++) {
                	if (result[i].id != undefined)
                		$scope.selectedMemberships.push(result[i].id);
				}
                $scope.loadAll();
            });
        }
        
        $scope.init();
    });
