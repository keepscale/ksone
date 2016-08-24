'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('booking', {
                parent: 'site',
                url: '/bookings',
                data: {
                    roles: ['ROLE_USER'],
                    pageTitle: 'crossfitApp.booking.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/booking/bookings.html',
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
            });
    });
