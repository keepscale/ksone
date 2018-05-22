'use strict';

angular.module('crossfitApp')
    .factory('Membership', function ($resource, DateUtils) {
        return $resource('api/memberships/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    return angular.toJson(data);
                }
            }
        });
    });
