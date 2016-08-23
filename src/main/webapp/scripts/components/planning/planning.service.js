'use strict';

angular.module('crossfitApp')
    .factory('Planning', function ($resource, DateUtils) {
        return $resource('planning', {}, {
            'query': { 
            	method: 'GET', isArray: false,
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    for (var i = 0; i < data.days.length; i++) {
						var day = data.days[i];
						day.date = DateUtils.convertLocaleDateFromServer(day.date);
					}
                    return data;
                }
            }
        });
    });
