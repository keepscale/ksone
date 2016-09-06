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
            'prepareBooking': { 
                method: 'POST',
                params : {prepare: 'true'},
                transformRequest: function (data) {
                    data.date = DateUtils.convertLocaleDateToServer(data.date);
                    return angular.toJson(data);
                }
            }
        });
    });
