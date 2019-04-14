'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('member', {
                parent: 'site',
                url: '/members?search',
                data: {
                    roles: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                    pageTitle: 'crossfitApp.member.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/member/members.html?v='+$stateProvider.VERSION,
                        controller: 'MemberController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('member');
                        $translatePartialLoader.addPart('bill');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('member.new', {
                parent: 'member',
                url: '/new?view',
                reloadOnSearch : false,
                params: {
                	view: 'infoperso'
                },
                data: {
                    roles: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', '$rootScope', function($stateParams, $state, $modal, $rootScope) {
                    $modal.open({
                        templateUrl: 'scripts/app/member/member-dialog.html?v='+$rootScope.VERSION,
                        controller: 'MemberDialogController',
                        size: 'xl'
                    }).result.then(function(result) {
                        $state.go('member', null, { reload: true });
                    }, function() {
                        $state.go('member', null, { reload: true });
                    })
                }]
            })
            .state('member.edit', {
                parent: 'member',
                url: '/{id}/edit?view',
                reloadOnSearch : false,
                params: {
                	view: 'infoperso'
                },
                data: {
                    roles: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', '$rootScope', function($stateParams, $state, $modal, $rootScope) {
                    $modal.open({
                        templateUrl: 'scripts/app/member/member-dialog.html?v='+$rootScope.VERSION,
                        controller: 'MemberDialogController',
                        size: 'xl'
                    }).result.then(function(result) {
                        $state.go('member', null, { reload: true });
                    }, function() {
                        $state.go('member', null);
                    })
                }]
            });
    });
