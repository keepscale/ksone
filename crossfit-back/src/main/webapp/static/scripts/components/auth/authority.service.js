'use strict';


angular.module('crossfitApp')
    .factory('Authority', function AuthorityService($resource) {
        return $resource('api/authorities', {}, {
            'query': { method: 'GET', isArray: true}
        });
    });
