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
                method: 'GET',
                url: 'api/bills/generate'
            },
            'deleteDraft': {
                method: 'DELETE',
                url: 'api/bills/draft'
            },
                
            'save': {
                method: 'POST',
                transformRequest: function (data) {

                	data.effectiveDate = DateUtils.convertLocaleDateToServer(data.effectiveDate);
                    
                    return angular.toJson(data);
                }
            }
        });
    });
