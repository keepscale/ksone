'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('membership', {
                parent: 'site',
                url: '/memberships',
                data: {
                    roles: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                    pageTitle: 'crossfitApp.membership.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/membership/memberships.html',
                        controller: 'MembershipController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('membership');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('membership.new', {
                parent: 'membership',
                url: '/new',
                data: {
                    roles: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/membership/membership-dialog.html',
                        controller: 'MembershipDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('membership', null, { reload: true });
                    }, function() {
                        $state.go('membership');
                    })
                }]
            })
            .state('membership.edit', {
                parent: 'membership',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/membership/membership-dialog.html',
                        controller: 'MembershipDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Membership', function(Membership) {
                                return Membership.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('membership', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
