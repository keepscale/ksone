'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('booking', {
                parent: 'site',
                url: '/bookings',
                data: {
                    roles: ['ROLE_USER','ROLE_MANAGER','ROLE_ADMIN'],
                    pageTitle: 'crossfitApp.booking.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'static/scripts/app/booking/bookings.html',
                        controller: 'BookingController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('booking');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('booking.new', {
                parent: 'home',
                url: '/new/{id:int}/:date',
                data: {
                    roles: ['ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', 'DateUtils', function($stateParams, $state, $modal, DateUtils) {
                    $modal.open({
                        templateUrl: 'static/scripts/app/booking/booking-dialog.html',
                        controller: 'BookingDialogController',
                        size: 'sm',
                        resolve: {
                            entity: function () {
                            	
                                return {
                                	timeslot:null,
                                	timeslotId:$stateParams.id,
                                	date: $stateParams.date,
                                	owner: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('home', null, { reload: true });
                    }, function() {
                        $state.go('home');
                    })
                }]
            })
            .state('booking.impossible', {
                parent: 'home',
                url: '/:message',
                data: {
                    roles: ['ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', 'DateUtils', function($stateParams, $state, $modal, DateUtils) {
                    $modal.open({
                        templateUrl: 'static/scripts/app/booking/booking-dialog.html',
                        controller: 'BookingDialogController',
                        size: 'sm',
                        resolve: {
                            entity: function () {
                            	
                                return {
                                	message : $stateParams.message};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('home', null, { reload: true });
                    }, function() {
                        $state.go('home');
                    })
                }]
            });
    });
