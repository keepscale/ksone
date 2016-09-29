'use strict';

angular.module('crossfitApp')
    .factory('Booking', function ($resource, DateUtils) {
        return $resource('api/bookings/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.date = DateUtils.convertLocaleDateFromServer(data.date);
                    return data;
                }
            },
            'delete': {
                method: 'DELETE'
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.date = DateUtils.convertLocaleDateToServer(data.date);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.date = DateUtils.convertLocaleDateToServer(data.date);
                    return angular.toJson(data);
                }
            },
            'findBookings': {
                method: 'GET',
                url: "api/bookings/:date/:timeSlotId", 
                params : {date: '@date', timeSlotId: '@timeSlotId'}, isArray: true
            },
            'status': {
                method: 'GET',
                url: "api/bookings/:date/:timeSlotId/status", 
                params : {date: '@date', timeSlotId: '@timeSlotId'}
            },
            'subscribe': {
                method: 'POST',
                url: "api/bookings/:date/:timeSlotId/subscribe", 
                params : {date: '@date', timeSlotId: '@timeSlotId'}
            },
            'prepareBooking': { 
                method: 'POST',
                params : {prepare: 'true'},
                transformRequest: function (data) {
                    data.date = DateUtils.convertLocaleDateToServer(data.date);
                    return angular.toJson(data);
                }
            },
            'getPast': {
                method: 'GET',
                url: "api/bookings/pastbookings",
                isArray: true,
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.date = DateUtils.convertLocaleDateFromServer(data.date);
                    return data;
                }
            },
        });
    });
