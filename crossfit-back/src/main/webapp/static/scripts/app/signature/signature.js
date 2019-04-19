'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
	        .state('sign-subscription', {
	            parent: 'site',
	            url: '/signature/{memberId}/subscriptions/{subscriptionId}',
	            data: {
	                roles: ['ROLE_MANAGER', 'ROLE_ADMIN'],
	                pageTitle: 'crossfitApp.signature.subscription.title'
	            },
	            views: {
	                'content@': {
	                    templateUrl: 'scripts/app/signature/sign-subscription.html?v='+$stateProvider.VERSION,
	                    controller: 'SignSubscriptionController'
	                }
	            },
	            resolve: {
	                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
	                    $translatePartialLoader.addPart('signature');
                        $translatePartialLoader.addPart('bill');
	                    $translatePartialLoader.addPart('global');
	                    return $translate.refresh();
	                }]
	            }
	        })	        
            .state('sign-mandate', {
                parent: 'site',
                url: '/signature/{memberId}/mandates/{mandateId}',
                data: {
                    roles: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                    pageTitle: 'crossfitApp.signature.mandate.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/signature/sign-mandate.html?v='+$stateProvider.VERSION,
                        controller: 'SignMandateController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('signature');
                        $translatePartialLoader.addPart('bill');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            });
    });
