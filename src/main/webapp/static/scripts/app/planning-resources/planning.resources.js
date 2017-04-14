'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
		        .state('planningResourcesToDay', {
		            parent: 'site',
		            url: '/planning-resources',
		            data: {
		                roles: ['ROLE_RENTER'],
		                pageTitle: 'crossfitApp.planning.resources.home.title'
		            },
		            views: {
		                'content@': {
		                    templateUrl: 'scripts/app/planning-resources/planning.resources.html',
		                    controller: 'PlanningResourcesController'
		                }
		            }
		        })
		        .state('planning-resources', {
		            parent: 'planningResourcesToDay',
		            url: '/:mode/:view/:startDate/:resourceId',
		            data: {
		                roles: ['ROLE_RENTER'],
		                pageTitle: 'crossfitApp.planning.resources.home.title'
		            },
		            views: {
		                'content@': {
		                    templateUrl: 'scripts/app/planning-resources/planning.resources.html',
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
		        })
		        .state('planning-resources.preparebooking', {
	                parent: 'planning-resources',
	                url: '/prepare',
	                data: {
	                    roles: ['ROLE_RENTER', 'ROLE_MANAGER', 'ROLE_ADMIN'],
	                    pageTitle: 'crossfitApp.planning.resources.home.title'
	                },
	                views: {
	                    'content@': {
	                        templateUrl: 'scripts/app/planning-resources/prepare.booking.html',
	                        controller: 'PrepareResourceBookingController'
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
		        .state('planning-resources.editbooking', {
	                parent: 'planning-resources',
	                url: '/edit/:id',
	                data: {
	                    roles: ['ROLE_RENTER', 'ROLE_MANAGER', 'ROLE_ADMIN'],
	                    pageTitle: 'crossfitApp.planning.resources.home.title'
	                },
	                views: {
	                    'content@': {
	                        templateUrl: 'scripts/app/planning-resources/edit.booking.html',
	                        controller: 'EditResourceBookingController'
	                    }
	                },
	                resolve: {
	                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
	                        $translatePartialLoader.addPart('planning');
	                        $translatePartialLoader.addPart('global');
	                        return $translate.refresh();
	                    }]
	                }
	            });;
    });
