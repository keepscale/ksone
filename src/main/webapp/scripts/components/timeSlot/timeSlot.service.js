
'use strict';

angular.module('crossfitApp')
    .factory('TimeSlot', function ($resource, DateUtils) {
        return $resource('api/timeSlots/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.startTime = DateUtils.parseDateAsTime(data.startTime);
                    data.endTime = DateUtils.parseDateAsTime(data.endTime);
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    return angular.toJson(data);
                }
            }
        });
    });
