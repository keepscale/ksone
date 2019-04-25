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
            },
            'contractmodels': {
                method: 'GET', isArray: true,
                url: 'admin/contractmodels'
            },
            'updatecontractmodel': {
                method: 'PUT',
                url: 'admin/contractmodels'
            },

            'contractmodelsVersionformat': {
            	method: 'GET',
                url: 'admin/contractmodels/versionformat',
                isArray: true
            },
        });
    });
