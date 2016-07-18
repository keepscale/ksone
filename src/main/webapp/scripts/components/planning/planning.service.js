'use strict';

angular.module('crossfitApp')
    .factory('Planning', function ($resource, DateUtils) {
        return $resource('planning', {}, {
            'query': { method: 'GET', isArray: false}
        });
    });
