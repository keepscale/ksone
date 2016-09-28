'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
        .state('login', {
            parent: 'account',
            url: '/login',
            data: {
                roles: [], 
                pageTitle: 'login.title'
            },
            views: {
                'content@': {
                    templateUrl: 'scripts/app/account/login/login.html',
                    controller: 'LoginController'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('login');
                    return $translate.refresh();
                }]
            }
        })
        .state('forgotpassword', {
            parent: 'account',
            url: '/forgotpassword',
            data: {
                roles: [], 
                pageTitle: 'forgotpassword.title'
            },
            views: {
                'content@': {
                    templateUrl: 'scripts/app/account/password/forgotpassword.html',
                    controller: 'LoginController'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('login');
                    return $translate.refresh();
                }]
            }
        });
    });
