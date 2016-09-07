'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
	        .state('planning', {
	            parent: 'site',
	            url: '/planning',
	            data: {
	                roles: ['ROLE_USER'],
	                pageTitle: 'crossfitApp.planning.home.title'
	            },
	            onEnter: function($state, $stateParams, $window, DateUtils){
		            if ($stateParams.startDate){
		            	return;
		            }
	           		var w = $window.innerWidth;;
                    var mode;
                    var view;
                    var dStart = new Date();
                    var dEnd = new Date();
                    if ( w >= 991){
                    	mode = 'desktop';
                    	view = 'week';
                    	dEnd.setDate(dEnd.getDate() + 7); 
                    }
                    else{
                    	mode = 'mobile';
                    	view = 'day';
                    	dEnd.setDate(dEnd.getDate() + 1);
                    }
                    
                	var start = DateUtils.formatDateAsDate(dStart);
                	var end = DateUtils.formatDateAsDate(dEnd);
                	
                    $state.transitionTo('planning.detail', {startDate:start, endDate:end, view:view, mode:mode});
	            }
	        })
	        .state('planning.detail', {
	            parent: 'site',
	            url: '/planning/:mode/:view/:startDate',
	            data: {
	                roles: ['ROLE_USER'],
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
	                parent: 'planning.detail',
	                url: '/:timeSlotId/:bookingDate',
	                data: {
	                    roles: ['ROLE_USER'],
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
	            });
    });
