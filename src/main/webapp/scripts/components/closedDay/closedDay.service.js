'use strict';

angular.module('crossfitApp')
    .factory('ClosedDay', function ($resource, DateUtils) {
        return $resource('api/closedDays/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.startAt = DateUtils.convertDateTimeFromServer(data.startAt);
                    data.endAt = DateUtils.convertDateTimeFromServer(data.endAt);
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.startAt = DateUtils.convertLocaleDateTimeToServer(data.startAt);
                    data.endAt = DateUtils.convertLocaleDateTimeToServer(data.endAt);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.startAt = DateUtils.convertLocaleDateTimeToServer(data.startAt);
                    data.endAt = DateUtils.convertLocaleDateTimeToServer(data.endAt);
                    return angular.toJson(data);
                }
            }
        });
    });
