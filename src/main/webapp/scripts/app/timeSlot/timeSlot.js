'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('timeSlot', {
                parent: 'site',
                url: '/timeSlots/:startDate/:endDate',
                data: {
                    roles: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                    pageTitle: 'crossfitApp.timeSlot.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/timeSlot/timeSlots.html',
                        controller: 'TimeSlotController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('timeSlot');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('timeSlot.new', {
                parent: 'timeSlot',
                url: '/new/:dayOfWeek/:start/:end',
                data: {
                    roles: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/timeSlot/timeSlot-dialog.html',
                        controller: 'TimeSlotDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                            	var d = new Date($stateParams.startDate);
                                d.setDate(d.getDate() + ($stateParams.dayOfWeek - 1));
                                return {recurrent:'DAY_OF_WEEK', date: d, dayOfWeek: parseInt($stateParams.dayOfWeek), 
                                	startTime: $stateParams.start, endTime: $stateParams.end, maxAttendees: 12, requiredLevel: 'NOVICE', id: null};
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('timeSlot', null, { reload: true });
                    }, function() {
                        $state.go('timeSlot');
                    })
                }]
            })
            .state('timeSlot.edit', {
                parent: 'timeSlot',
                url: '/{id}/edit',
                data: {
                    roles: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/timeSlot/timeSlot-dialog.html',
                        controller: 'TimeSlotDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['TimeSlot', function(TimeSlot) {
                                return TimeSlot.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('timeSlot', null, { reload: true });
                    }, function() {
                    })
                }]
            })
            .state('timeSlot.delete', {
                parent: 'timeSlot',
                url: '/{id}/delete',
                data: {
                    roles: ['ROLE_MANAGER', 'ROLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$modal', function($stateParams, $state, $modal) {
                    $modal.open({
                        templateUrl: 'scripts/app/timeSlot/timeSlot-dialog-suppr.html',
                        controller: 'TimeSlotDialogSupprController',
                        size: 'lg',
                        resolve: {
                            entity: ['TimeSlot', function(TimeSlot) {
                                return TimeSlot.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('timeSlot', null, { reload: true });
                    }, function() {
                    })
                }]
            });
    });
