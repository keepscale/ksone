'use strict';

angular.module('crossfitApp')
    .factory('CrossfitBox', function ($resource, DateUtils) {
        return $resource('api/boxs', {}, {
            'current': {
                method: 'GET',
                url: 'api/boxs/current'
            },
            'update': {
                method: 'PUT'
            }
        });
    });
