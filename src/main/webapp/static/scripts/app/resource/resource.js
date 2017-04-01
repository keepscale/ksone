'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('resource', {
                parent: 'site',
                url: '/resources',
                data: {
                    roles: ['ROLE_DIRECTOR', 'ROLE_ADMIN'],
                    pageTitle: 'crossfitApp.resource.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/resource/resources.html',
                        controller: 'ResourceController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('resource');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('resource.new', {
                parent: 'resource',
                url: '/new',
                data: {
                    roles: ['ROLE_DIRECTOR', 'ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/resource/resource-dialog.html',
                        controller: 'ResourceDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('resource', null, { reload: true });
                    }, function() {
                        $state.go('resource');
                    })
                }]
            })
            .state('resource.edit', {
                parent: 'resource',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_DIRECTOR', 'ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/resource/resource-dialog.html',
                        controller: 'ResourceDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Resource', function(Resource) {
                                return Resource.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('resource', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
