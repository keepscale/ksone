'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('sql', {
                parent: 'site',
                url: '/sql',
                data: {
                    roles: ['ROLE_ADMIN'],
                    pageTitle: 'crossfitApp.sql.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/admin/sql/sql.html',
                        controller: 'SQLController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('sql');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            });
    });
