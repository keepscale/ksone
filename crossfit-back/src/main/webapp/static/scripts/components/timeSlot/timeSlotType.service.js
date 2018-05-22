
'use strict';

angular.module('crossfitApp')
    .factory('TimeSlotType', function ($resource) {
        return $resource('api/timeSlotTypes/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'update': {
                method: 'PUT'
            },
            'save': {
                method: 'POST'
            },
            'delete': {
                method: 'DELETE'
            }
        });
    });
