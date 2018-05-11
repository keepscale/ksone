'use strict';

angular.module('crossfitApp')
    .factory('EventBooking', function ($resource, DateUtils) {
        return $resource('api/events/bookings/:id', {}, {
            'query': {
                method: 'GET',
                url: "api/events/bookings/today",
                isArray: true,
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.bookingStartDate = DateUtils.convertDateTimeFromServer(data.bookingStartDate);
                    data.eventDate = DateUtils.convertDateTimeFromServer(data.eventDate);
                    return data;
                }
            },
        });
    });
