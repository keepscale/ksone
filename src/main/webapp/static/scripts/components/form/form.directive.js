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
	.directive('lowerThan', [
	  function() {
	
	    var link = function($scope, $element, $attrs, ctrl) {
	
	      var validate = function(viewValue) {
	        var comparisonModel = $attrs.lowerThan;
	
	        if(!viewValue || !comparisonModel){
	          // It's valid because we have nothing to compare against
	          ctrl.$setValidity('lowerThan', true);
	        }
	        else{
		        ctrl.$setValidity('lowerThan', comparisonModel > viewValue);
		        // It's valid if model is lower than the model we're comparing against
	        }
	
	        return viewValue;
	      };
	
	      ctrl.$parsers.unshift(validate);
	
	    };
	
	    return {
	      require: 'ngModel',
	      link: link
	    };
	
	  }
	])
	.directive('higherThan', [
	  function() {
	
	    var link = function($scope, $element, $attrs, ctrl) {
	
	      var validate = function(viewValue) {
	        var comparisonModel = $attrs.higherThan;
	
	        if(!viewValue || !comparisonModel){
	          // It's valid because we have nothing to compare against
	          ctrl.$setValidity('higherThan', true);
	        }
	        else{
		        ctrl.$setValidity('higherThan', comparisonModel < viewValue);
		        // It's valid if model is higher than the model we're comparing against
	        }
	
	        return viewValue;
	      };
	
	      ctrl.$parsers.unshift(validate);
	
	    };
	
	    return {
	      require: 'ngModel',
	      link: link
	    };
	
	  }
	]);
