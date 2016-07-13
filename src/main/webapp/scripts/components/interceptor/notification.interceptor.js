 'use strict';

angular.module('crossfitApp')
    .factory('notificationInterceptor', function ($q, AlertService) {
        return {
            response: function(response) {
                var alertKey = response.headers('X-crossfitApp-alert');
                if (angular.isString(alertKey)) {
                    AlertService.success(alertKey, { param : response.headers('X-crossfitApp-params')});
                }
                return response;
            },
        };
    });