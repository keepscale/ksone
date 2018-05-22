'use strict';

angular.module('crossfitApp')
    .factory('Version', function ($resource, DateUtils) {
        return $resource('api/version', {}, {
            'current': {
                method: 'GET', isArray: false,
                transformResponse: function(data, headersGetter, status) {
                    return {content: data};
                }
            }
        });
    });
