'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('version', {
                parent: 'site',
                url: '/version',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'crossfitApp.version.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/admin/version/version.html',
                        controller: 'VersionController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('version');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            });
    });
