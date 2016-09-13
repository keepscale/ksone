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
                        templateUrl: 'static/scripts/app/member/members.html',
                        controller: 'MemberController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('member');
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
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'static/scripts/app/member/member-dialog.html',
                        controller: 'MemberDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                	title: 'MR', langKey: 'fr',
                                	telephonNumber: null, sickNoteEndDate: null, 
                                	membershipStartDate: null, membershipEndDate: null, 
                                	level: null, id: null,
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
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'static/scripts/app/member/member-dialog.html',
                        controller: 'MemberDialogController',
                        size: 'lg',
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
