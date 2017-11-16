'use strict';

angular.module('crossfitApp')
    .factory('Bill', function ($resource, DateUtils) {
        return $resource('api/bills/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { 
            	method: 'GET',
            	transformResponse: function (data) {
                    data = angular.fromJson(data);
                    
                    data.effectiveDate = 
                    	DateUtils.convertLocaleDateFromServer(data.effectiveDate);

                	data.createdBillDate = 
                		DateUtils.convertDateTimeFromServer(data.createdBillDate);
                    
                    return data;
                }
            },
            'periods': { 
            	method: 'GET',
                url: 'api/bills/periods',  
                isArray: true
            },
            'paymentMethods': { 
            	method: 'GET',
                url: 'api/bills/paymentMethods',  
                isArray: true
            },
            'status': { 
            	method: 'GET',
                url: 'api/bills/status',  
                isArray: true
            },


            'generate': {
                method: 'PUT',
                url: 'api/bills/generate',  
                timeout: 5 * 60 * 1000,
                transformRequest: function (data) {

                	data.sinceDate = DateUtils.convertLocaleDateToServer(data.sinceDate);
                	data.untilDate = DateUtils.convertLocaleDateToServer(data.untilDate);
                    
                    return angular.toJson(data);
                }
            },
            'deleteDraft': {
                method: 'DELETE',
                url: 'api/bills/draft'
            },
                
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
