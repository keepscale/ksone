'use strict';

angular.module('crossfitApp')
    .factory('Bill', function ($resource, DateUtils) {
        return $resource('api/bills/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'save': {
                method: 'POST',
                transformRequest: function (data) {

                    for (var i = 0; i < data.subscriptions.length; i++) {
						var sub = data.subscriptions[i];
						sub.subscriptionStartDate = DateUtils.convertLocaleDateToServer(sub.subscriptionStartDate);
						sub.subscriptionEndDate = DateUtils.convertLocaleDateToServer(sub.subscriptionEndDate);
					}
                    
                    return angular.toJson(data);
                }
            }
        });
    });
