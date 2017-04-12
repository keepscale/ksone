
'use strict';

angular.module('crossfitApp')
    .factory('Resource', function ($resource) {
        return $resource('api/resources/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'update': {
                method: 'PUT'
            },
            'save': {
                method: 'POST'
            },
            'delete': {
                method: 'DELETE'
            },
            'planning': {
            	method: 'GET',
                url: "api/resources/:id/planning", 
                isArray: true
            }
        });
    });
