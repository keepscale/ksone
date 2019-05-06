/* globals $ */
'use strict';

angular.module('crossfitApp')
	.directive('showValidation', function() {
	    return {
	        restrict: 'A',
	        require: 'form',
	        link: function (scope, element) {
	            element.find('.form-group').each(function() {
	                var $formGroup = $(this);
	                var $inputs = $formGroup.find('input[ng-model],textarea[ng-model],select[ng-model]');
	
	                if ($inputs.length > 0) {
	                    $inputs.each(function() {
	                        var $input = $(this);
	                        scope.$watch(function() {
	                            return $input.hasClass('ng-invalid') && $input.hasClass('ng-dirty');
	                        }, function(isInvalid) {
	                            $formGroup.toggleClass('has-error', isInvalid);
	                        });
	                    });
	                }
	            });
	        }
	    };
	})

    .directive('disableableForm', function() {
        return {
            restrict: 'A',
            scope: {
            	disabled: '@'
            },
            controller: function ($scope, $element, $attrs) {
            	$scope.$watch('disabled', function(disabled) {
                	$element.find('input[ng-model],textarea[ng-model],select[ng-model]').each(function() {
                        var $input = $(this);                        
                        $input.prop('disabled', disabled);
                    });
                });
            }
        };
    });
