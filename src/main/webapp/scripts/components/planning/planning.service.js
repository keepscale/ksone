'use strict';

angular.module('crossfitApp')
    .factory('Planning', function ($resource, DateUtils) {
        return $resource('', {}, {
            'boxPlanning': { 
            	method: 'GET', isArray: false,
                url: 'private/planning', 
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    for (var i = 0; i < data.days.length; i++) {
						var day = data.days[i];
						day.date = DateUtils.convertLocaleDateFromServer(day.date);
					}
                    return data;
                }
            },

            'myPlanning': { 
            	method: 'GET', isArray: true,
                url: 'protected/planning'
            }
        });
    });
