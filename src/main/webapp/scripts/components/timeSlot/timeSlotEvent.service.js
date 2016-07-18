'use strict';

angular.module('crossfitApp')
    .factory('TimeSlotEvent', function ($resource, DateUtils) {
        return $resource('/event', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
