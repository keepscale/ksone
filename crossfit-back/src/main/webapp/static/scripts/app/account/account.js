'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
        .state('account', {
        	abstract: true,
            parent: 'site'
        })
        .state('account.edit', {
                parent: 'account',
	            url: '/account',
	            data: {
	                roles: ['ROLE_USER','ROLE_MANAGER','ROLE_ADMIN'],
	                pageTitle: 'crossfitApp.account.home.title'
	            },
	            views: {
	                'content@': {
	                    templateUrl: 'scripts/app/account/account.html',
	                    controller: 'AccountController'
	                }
	            },
	            resolve: {
	                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
	                    $translatePartialLoader.addPart('account');
	                    $translatePartialLoader.addPart('member');
	                    $translatePartialLoader.addPart('global');
	                    return $translate.refresh();
	                }]
	            }
            });
    });
