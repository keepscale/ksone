
'use strict';

angular.module('crossfitApp')
    .factory('ResourceBooking', function ($resource, DateUtils) {
        return $resource('api/resource-bookings/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.date = DateUtils.convertLocaleDateToServer(data.date);
                    data.startHour = DateUtils.convertLocaleDateTimeToServer(data.startHour);
                    data.endHour = DateUtils.convertLocaleDateTimeToServer(data.endHour);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.date = DateUtils.convertLocaleDateToServer(data.date);
                    data.startHour = DateUtils.convertLocaleDateTimeToServer(data.startHour);
                    data.endHour = DateUtils.convertLocaleDateTimeToServer(data.endHour);
                    return angular.toJson(data);
                }
            },
            'delete': {
                method: 'DELETE'
            }
        });
    });
