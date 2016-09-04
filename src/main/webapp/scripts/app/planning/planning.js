'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('planning', {
                parent: 'site',
                url: '/planning/:mode/:view/:startDate',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'crossfitApp.planning.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/planning/planning.html',
                        controller: 'PlanningController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('planning');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            });
    });
