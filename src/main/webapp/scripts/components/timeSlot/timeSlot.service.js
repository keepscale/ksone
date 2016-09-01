
'use strict';

angular.module('crossfitApp')
    .factory('TimeSlot', function ($resource, DateUtils) {
        return $resource('api/timeSlots/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'queryAsEvent': { method: 'GET', isArray: true, params:{"view":"event"}},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.startTime = DateUtils.parseDateAsTime(data.startTime);
                    data.endTime = DateUtils.parseDateAsTime(data.endTime);
                    data.visibleAfter = DateUtils.convertLocaleDateFromServer(data.visibleAfter);
                    data.visibleBefore = DateUtils.convertLocaleDateFromServer(data.visibleBefore);

                    for (var i = 0; i < data.exclusions.length; i++) {
                    	data.exclusions[i].date = DateUtils.convertLocaleDateFromServer(data.exclusions[i].date);
					}
                    
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.startTime = DateUtils.formatDateAsTime(data.startTime);
                    data.endTime = DateUtils.formatDateAsTime(data.endTime);
                    
//                    for (var i = 0; i < data.exclusions.length; i++) {
//                    	data.exclusions[i] = DateUtils.convertLocaleDateToServer(data.exclusions[i]);
//					}
                    
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.startTime = DateUtils.formatDateAsTime(data.startTime);
                    data.endTime = DateUtils.formatDateAsTime(data.endTime);
                    
//                    for (var i = 0; i < data.exclusions.length; i++) {
//                    	data.exclusions[i] = DateUtils.convertLocaleDateToServer(data.exclusions[i]);
//					}
//                    
                    
                    return angular.toJson(data);
                }
            }
        });
    });
