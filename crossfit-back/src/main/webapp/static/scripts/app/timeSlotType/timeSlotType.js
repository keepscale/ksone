'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('timeSlotType', {
                parent: 'site',
                url: '/timeSlotTypes',
                data: {
                    roles: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                    pageTitle: 'crossfitApp.timeSlotType.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/timeSlotType/timeSlotTypes.html?v='+$stateProvider.VERSION,
                        controller: 'TimeSlotTypeController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('timeSlotType');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('timeSlotType.new', {
                parent: 'timeSlotType',
                url: '/new',
                data: {
                    roles: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', '$rootScope', function($stateParams, $state, $modal, $rootScope) {
                    $modal.open({
                        templateUrl: 'scripts/app/timeSlotType/timeSlotType-dialog.html?v='+$rootScope.VERSION,
                        controller: 'TimeSlotTypeDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('timeSlotType', null, { reload: true });
                    }, function() {
                        $state.go('timeSlotType');
                    })
                }]
            })
            .state('timeSlotType.edit', {
                parent: 'timeSlotType',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', '$rootScope', function($stateParams, $state, $modal, $rootScope) {
                    $modal.open({
                        templateUrl: 'scripts/app/timeSlotType/timeSlotType-dialog.html?v='+$rootScope.VERSION,
                        controller: 'TimeSlotTypeDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['TimeSlotType', function(TimeSlotType) {
                                return TimeSlotType.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('timeSlotType', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
