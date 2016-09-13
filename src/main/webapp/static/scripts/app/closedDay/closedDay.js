'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('closedDay', {
                parent: 'site',
                url: '/closedDays',
                data: {
                    roles: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                    pageTitle: 'crossfitApp.closedDay.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'static/scripts/app/closedDay/closedDays.html',
                        controller: 'ClosedDayController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('closedDay');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('closedDay.new', {
                parent: 'closedDay',
                url: '/new',
                data: {
                    roles: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'static/scripts/app/closedDay/closedDay-dialog.html',
                        controller: 'ClosedDayDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {name: null, startAt: null, endAt: null, id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('closedDay', null, { reload: true });
                    }, function() {
                        $state.go('closedDay');
                    })
                }]
            })
            .state('closedDay.edit', {
                parent: 'closedDay',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'static/scripts/app/closedDay/closedDay-dialog.html',
                        controller: 'ClosedDayDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['ClosedDay', function(ClosedDay) {
                                return ClosedDay.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('closedDay', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
