'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('config', {
                parent: 'site',
                url: '/config',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'crossfitApp.config.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/admin/config/config.html',
                        controller: 'ConfigController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('config');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            });
    });
