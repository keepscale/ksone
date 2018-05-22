'use strict';

angular.module('crossfitApp')
    .factory('Stats', function ($resource, DateUtils) {
        return $resource('stats', {}, {
            'membership': {
                method: 'GET',
                url: 'stats/membership', isArray: true
            }
        });
    });
