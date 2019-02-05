'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('subscription-sign', {
                parent: 'member',
                url: '/{memberId}/subscriptions/{subscriptionId}/sign',
                data: {
                    roles: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                    pageTitle: 'crossfitApp.subscription.sign.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/member/subscription/sign-subscription.html?v='+$stateProvider.VERSION,
                        controller: 'SignSubscriptionController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('subscription');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            });
    });
