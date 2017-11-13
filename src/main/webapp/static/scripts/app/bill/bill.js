'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('bill', {
                parent: 'site',
                url: '/bills',
                data: {
                    roles: ['ROLE_COMPTABLE', 'ROLE_ADMIN'],
                    pageTitle: 'crossfitApp.bill.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/bill/bills.html?v='+$stateProvider.VERSION,
                        controller: 'BillController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('bill');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('bill.new', {
                parent: 'bill',
                url: '/new',
                data: {
                    roles: ['ROLE_COMPTABLE', 'ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', '$rootScope', function($stateParams, $state, $modal, $rootScope) {
                    $modal.open({
                        templateUrl: 'scripts/app/bill/bill-dialog.html?v='+$rootScope.VERSION,
                        controller: 'BillDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                	id: null,
                                	status : "VALIDE"
                            	};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('bill', null, { reload: true });
                    }, function() {
                        $state.go('bill');
                    })
                }]
            })
            .state('bill.edit', {
                parent: 'bill',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_COMPTABLE', 'ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', '$rootScope', function($stateParams, $state, $modal, $rootScope) {
                    $modal.open({
                        templateUrl: 'scripts/app/bill/bill-dialog.html?v='+$rootScope.VERSION,
                        controller: 'BillDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Bill', function(Bill) {
                                return Bill.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('bill', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
