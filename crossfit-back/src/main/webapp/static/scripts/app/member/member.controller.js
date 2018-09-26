'use strict';

angular.module('crossfitApp')
    .controller('MemberController', function ($scope, $window, Member, Membership, Authority,DateUtils, ParseLinks) {
        $scope.members = [];
        $scope.page = 1;
        $scope.per_page = 20;
        $scope.include_actif = true;
        $scope.include_not_ennabled = true;
        $scope.include_bloque = false;
        $scope.memberships = [];
        $scope.membershipsIds = [];
        $scope.selectedMemberships = [];
        $scope.roles = [];
        $scope.selectedRoles = ["ROLE_USER"];
        $scope.healthIndicators = [];
        $scope.selectedHealthIndicators = [];
        $scope.selectedCustomCriteria = [];
        $scope.customCriteria = {
    		expire: new Date(),
    		encours: new Date()
        }
        
        $scope.loadAll = function() {
            Member.query({
            	page: $scope.page, per_page: $scope.per_page, 
            	search: $scope.searchLike, 
            	include_memberships: $scope.selectedMemberships, 
            	include_roles: $scope.selectedRoles,
            	with_healthindicators: $scope.selectedHealthIndicators,
            	with_customcriteria: $scope.selectedCustomCriteria,
            	with_customcriteria_expire: DateUtils.formatDateAsDate($scope.customCriteria.expire),
            	with_customcriteria_encours: DateUtils.formatDateAsDate($scope.customCriteria.encours),
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
        $scope.keyPressOnSearchField = function(keyEvent) {
    	  if (keyEvent.which === 13)
    		  $scope.search();
    	}

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
        
        $scope.isIndeterminate = function(selectedIds, allvalues){
        	return selectedIds && selectedIds.length > 0 && selectedIds.length != allvalues.length;
        }
        $scope.isChecked = function(selectedIds){
        	return selectedIds && selectedIds.length > 0;
        }
        $scope.toggleSelectAll = function(selectedIds, allvalues){
        	if (selectedIds.length > 0)
        		selectedIds.splice(0, selectedIds.length);
        	else{
        		Array.prototype.push.apply(selectedIds, allvalues);
        	}
        }
        
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
		
        
        $scope.toggleSelectedRole = function toggleSelectedRole(role) {
			var idx = $scope.selectedRoles.indexOf(role);

			// Is currently selected
			if (idx > -1) {
				$scope.selectedRoles.splice(idx, 1);
			}

			// Is newly selected
			else {
				$scope.selectedRoles.push(role);
			}
		};

        
        $scope.toggleSelectedHealthIndicator = function toggleSelectedHealthIndicator(hi) {
			var idx = $scope.selectedHealthIndicators.indexOf(hi);

			// Is currently selected
			if (idx > -1) {
				$scope.selectedHealthIndicators.splice(idx, 1);
			}

			// Is newly selected
			else {
				$scope.selectedHealthIndicators.push(hi);
			}
		};
        
        $scope.toggleSelectedCustomCriteria = function toggleSelectedCustomCriteria(criteria) {
			var idx = $scope.selectedCustomCriteria.indexOf(criteria);

			// Is currently selected
			if (idx > -1) {
				$scope.selectedCustomCriteria.splice(idx, 1);
			}

			// Is newly selected
			else {
				$scope.selectedCustomCriteria.push(criteria);
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
            for (var i = 0; i < $scope.selectedRoles.length; i++) {
            	params += "&include_roles=" + $scope.selectedRoles[i];
            }
            for (var i = 0; i < $scope.selectedHealthIndicators.length; i++) {
            	params += "&with_healthindicators=" + $scope.selectedHealthIndicators[i];
            }

            for (var i = 0; i < $scope.selectedCustomCriteria.length; i++) {
            	params += "&with_customcriteria=" + $scope.selectedCustomCriteria[i];
            }

        	params += "&with_customcriteria_expire=" + DateUtils.formatDateAsDate($scope.customCriteria.expire);
        	params += "&with_customcriteria_encours=" + DateUtils.formatDateAsDate($scope.customCriteria.encours);
        	
            
            
			$window.open("api/members.csv?"+params);
		};
		
        $scope.init = function(){
        	Membership.query({}, function(result){
            	$scope.memberships = result;
                for (var i = 0; i < result.length; i++) {
                	if (result[i].id != undefined)
                		$scope.selectedMemberships.push(result[i].id);
				}
                $scope.membershipsIds = $scope.memberships.map(b=>b.id);
                
                Authority.query({}, function(res){
                	$scope.roles = res;
                	$scope.loadAll();
                	
                });
            });
        	
        	Member.healthIndicators({}, function(result){
        		$scope.healthIndicators = result;
        	});
        }
        
        $scope.init();
    });
