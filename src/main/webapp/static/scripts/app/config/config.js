'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('config', {
                parent: 'site',
                url: '/config',
                data: {
                    roles: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                    pageTitle: 'crossfitApp.config.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'static/scripts/app/config/config.html',
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
