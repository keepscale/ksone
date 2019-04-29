'use strict';

angular.module('crossfitApp')
    .factory('ContractModel', function ($resource, DateUtils) {
        return $resource('api/contractmodels/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'data': {
                method: 'GET',
                url: 'api/contractmodels/:id/data', 
                params : {id: '@id'}
            },
        });
    });
