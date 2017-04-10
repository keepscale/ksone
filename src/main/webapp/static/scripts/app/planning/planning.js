'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
	        .state('planningToDay', {
	            parent: 'site',
	            url: '/planning',
	            data: {
	                roles: ['ROLE_USER','ROLE_MANAGER','ROLE_ADMIN'],
	                pageTitle: 'crossfitApp.planning.home.title'
	            },
	            views: {
	                'content@': {
	                    templateUrl: 'scripts/app/planning/planning.html',
	                    controller: 'PlanningController'
	                }
	            }
	        })
	        .state('planning', {
	            parent: 'planningToDay',
	            url: '/:mode/:view/:startDate',
	            data: {
	                roles: ['ROLE_USER','ROLE_MANAGER','ROLE_ADMIN'],
	                pageTitle: 'crossfitApp.planning.home.title'
	            },
	            views: {
	                'content@': {
	                    templateUrl: 'scripts/app/planning/planning.html',
	                    controller: 'PlanningController'
	                }
	            },
	            resolve: {
	                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
	                    $translatePartialLoader.addPart('planning');
	                    $translatePartialLoader.addPart('global');
	                    return $translate.refresh();
	                }]
	            }
	        })
	        .state('planning.preparebooking', {
	                parent: 'planning',
	                url: '/:timeSlotId/:bookingDate',
	                data: {
	                    roles: ['ROLE_USER','ROLE_MANAGER','ROLE_ADMIN'],
	                    pageTitle: 'crossfitApp.planning.home.title'
	                },
	                views: {
	                    'content@': {
	                        templateUrl: 'scripts/app/planning/prepare.booking.html',
	                        controller: 'PrepareBookingController'
	                    }
	                },
	                resolve: {
	                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
	                        $translatePartialLoader.addPart('planning');
	                        $translatePartialLoader.addPart('global');
	                        return $translate.refresh();
	                    }]
	                }
	            })
		        .state('planningResourcesToDay', {
		            parent: 'site',
		            url: '/planning-resources',
		            data: {
		                roles: ['ROLE_RENTER'],
		                pageTitle: 'crossfitApp.planning.resources.home.title'
		            },
		            views: {
		                'content@': {
		                    templateUrl: 'scripts/app/planning/planning.resources.html',
		                    controller: 'PlanningResourcesController'
		                }
		            }
		        })
		        .state('planning-resources', {
		            parent: 'planningResourcesToDay',
		            url: '/:mode/:view/:startDate',
		            data: {
		                roles: ['ROLE_RENTER'],
		                pageTitle: 'crossfitApp.planning.resources.home.title'
		            },
		            views: {
		                'content@': {
		                    templateUrl: 'scripts/app/planning/planning.resources.html',
		                    controller: 'PlanningResourcesController'
		                }
		            },
		            resolve: {
		                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
		                    $translatePartialLoader.addPart('planning');
		                    $translatePartialLoader.addPart('global');
		                    return $translate.refresh();
		                }]
		            }
		        });
    });
