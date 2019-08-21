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

    .directive('disabledForm', function() {
        return {
            restrict: 'AEC',
            scope: {
            	disabledForm: '='
            },
            link: function ($scope, $element, $attrs) {
            	$scope.$watch('disabledForm', function(disabledForm, oldValue) {
                    $element.find('.modal-body').each(function() {
                    	$(this).find('input,textarea[ng-model],select[ng-model],datetimepicker').not(".ignoreDisabledForm").each(function() {                            
                    		var $input = $(this);
                            if (disabledForm || !$input[0].hasAttribute('ng-disabled')){
                            	$input.prop('disabled', disabledForm);
                            }
                        });
                    });
                }, true);
            }
        };
    });
