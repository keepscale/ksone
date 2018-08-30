'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('stats', {
                parent: 'site',
                url: '/stats',
                data: {
                    roles: ['ROLE_DIRECTOR', 'ROLE_ADMIN'],
                    pageTitle: 'crossfitApp.stats.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/stats/stats.html?v='+$stateProvider.VERSION,
                        controller: 'StatsController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('stats');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            });
    });
