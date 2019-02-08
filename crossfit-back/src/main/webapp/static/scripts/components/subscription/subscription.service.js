'use strict';

angular.module('crossfitApp')
    .factory('Subscription', function ($resource, DateUtils) {
        return $resource('api/subscriptions/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);                 
                    data.subscriptionStartDate = DateUtils.convertLocaleDateFromServer(data.subscriptionStartDate);
                    data.subscriptionEndDate = DateUtils.convertLocaleDateFromServer(data.subscriptionEndDate);
                    return data;
                }
            },
            'sign': {
                method: 'POST',
                url: 'api/subscriptions/sign', 
                transformRequest: function (data) {
                	var signature = {
                		id : data.id,
            			signatureDate: DateUtils.convertLocaleDateTimeToServer(data.signatureDate),
						signatureDataEncoded: data.signatureDataEncoded
                	}
                    
                    return angular.toJson(signature);
                }
            },
        });
    });
