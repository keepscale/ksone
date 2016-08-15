'use strict';

angular.module('crossfitApp').controller('MembershipDialogController',
    ['$scope', '$stateParams', '$state', '$uibModalInstance', 'entity', 'Membership', 'TimeSlotType', 
        function($scope, $stateParams, $state, $modalInstance, entity, Membership, TimeSlotType) {

        $scope.membership = entity;
        $scope.timeSlotTypes = TimeSlotType.query();
        
        $scope.load = function(id) {
            Membership.get({id : id}, function(result) {
                $scope.membership = result;
            });
        };

        var onSaveFinished = function (result) {
            $scope.$emit('crossfitApp:membershipUpdate', result);
            $modalInstance.close(result);
        };

        $scope.save = function () {
            if ($scope.membership.id != null) {
                Membership.update($scope.membership, onSaveFinished);
            } else {
                Membership.save($scope.membership, onSaveFinished);
            }
        };

        $scope.clear = function() {
            $modalInstance.dismiss('cancel');
        };
        

        $scope.addRules = function() {
        	var rule = {
        			applyForTimeSlotTypes : []
        	};
        	$scope.membership.membershipRules.push(rule);
        };
        

        $scope.deleteRulesAtIndex = function(idx) {
        	$scope.membership.membershipRules.splice(idx, 1);
        };
        
        $scope.indexOfTimeSlotType = function indexOfTimeSlotType(rule, timeSlotType) {
        	for (var idx = 0; idx < rule.applyForTimeSlotTypes.length; idx++) {
				var element = rule.applyForTimeSlotTypes[idx];
				if (element.id === timeSlotType.id){
					return idx;
				}
			}
        	return -1;
        }
        
        $scope.containsTimeSlotType = function containsTimeSlotType(rule, timeSlotType) {
            return $scope.indexOfTimeSlotType(rule, timeSlotType) > -1;
        };
        
        $scope.toggleSelection = function toggleSelection(rule, timeSlotType) {
            var idx = $scope.indexOfTimeSlotType(rule, timeSlotType);

            // is currently selected
            if (idx > -1) {
            	rule.applyForTimeSlotTypes.splice(idx, 1);
            }

            // is newly selected
            else {
            	rule.applyForTimeSlotTypes.push(timeSlotType);
            }
          };
        
}]);
