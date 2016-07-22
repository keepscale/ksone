'use strict';

angular.module('crossfitApp')
    .factory('Member', function ($resource, DateUtils) {
        return $resource('api/members/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.sickNoteEndDate = DateUtils.convertLocaleDateFromServer(data.sickNoteEndDate);
                    data.membershipStartDate = DateUtils.convertLocaleDateFromServer(data.membershipStartDate);
                    data.membershipEndDate = DateUtils.convertLocaleDateFromServer(data.membershipEndDate);
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.sickNoteEndDate = DateUtils.convertLocaleDateToServer(data.sickNoteEndDate);
                    data.membershipStartDate = DateUtils.convertLocaleDateToServer(data.membershipStartDate);
                    data.membershipEndDate = DateUtils.convertLocaleDateToServer(data.membershipEndDate);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.sickNoteEndDate = DateUtils.convertLocaleDateToServer(data.sickNoteEndDate);
                    data.membershipStartDate = DateUtils.convertLocaleDateToServer(data.membershipStartDate);
                    data.membershipEndDate = DateUtils.convertLocaleDateToServer(data.membershipEndDate);
                    return angular.toJson(data);
                }
            }
        });
    });
