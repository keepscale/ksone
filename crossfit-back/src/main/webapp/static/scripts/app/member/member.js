'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('member', {
                parent: 'site',
                url: '/members',
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
                url: '/new',
                data: {
                    roles: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', '$rootScope', function($stateParams, $state, $modal, $rootScope) {
                    $modal.open({
                        templateUrl: 'scripts/app/member/member-dialog.html?v='+$rootScope.VERSION,
                        controller: 'MemberDialogController',
                        size: 'xl',
                        resolve: {
                            entity: function () {
                                return {
                                	title: 'MR', langKey: 'fr',
                                	telephonNumber: null, sickNoteEndDate: null, 
                                	membershipStartDate: null, membershipEndDate: null, 
                                	level: null, id: null,
                                	roles : ["ROLE_USER"],
                                	subscriptions : [
                                		{
                                    		subscriptionStartDate : new Date()
                                		}
                                	]
                            	};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('member', null, { reload: true });
                    }, function() {
                        $state.go('member');
                    })
                }]
            })
            .state('member.edit', {
                parent: 'member',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', '$rootScope', function($stateParams, $state, $modal, $rootScope) {
                    $modal.open({
                        templateUrl: 'scripts/app/member/member-dialog.html?v='+$rootScope.VERSION,
                        controller: 'MemberDialogController',
                        size: 'xl',
                        resolve: {
                            entity: ['Member', function(Member) {
                                return Member.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('member', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
