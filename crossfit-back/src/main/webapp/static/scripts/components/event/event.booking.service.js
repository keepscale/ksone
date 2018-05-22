'use strict';

angular.module('crossfitApp')
    .factory('EventBooking', function ($resource, DateUtils) {
        return $resource('api/events/bookings/:id', {}, {
            'query': {
                method: 'GET',
                url: "api/events/bookings/today",
                isArray: true,
                transformResponse: function (data) {
                    var events = angular.fromJson(data);
                    if (events){
                        for (var i = 0; i < events.length; i++) {
                        	var event = events[i];
                        	event.bookingStartDate = DateUtils.convertDateTimeFromServer(event.bookingStartDate);
                        	event.eventDate = DateUtils.convertDateTimeFromServer(event.eventDate);
                        }
                    }
                    return events;
                }
            },
        });
    });
