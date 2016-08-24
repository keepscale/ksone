'use strict';

angular.module('crossfitApp')
    .factory('TimeSlotEvent', function ($resource, DateUtils) {
        return $resource('/myplanning', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
