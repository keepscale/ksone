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
                    
                    data.payAtDate = 
                    	DateUtils.convertLocaleDateFromServer(data.payAtDate);

                	data.createdBillDate = 
                		DateUtils.convertDateTimeFromServer(data.createdBillDate);
                	

                    for (var i = 0; i < data.lines.length; i++) {
                    	data.lines[i].periodStart = DateUtils.convertLocaleDateFromServer(data.lines[i].periodStart);
                    	data.lines[i].periodEnd = DateUtils.convertLocaleDateFromServer(data.lines[i].periodEnd);
					}
                    
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
            
            'validateBills' : {
                method: 'POST',
                url: 'api/bills/validate'
            },          
            
            'save': {
                method: 'POST',
                transformRequest: function (data) {

                	data.effectiveDate = DateUtils.convertLocaleDateToServer(data.effectiveDate);

                    
                    data.payAtDate = 
                    	DateUtils.convertLocaleDateToServer(data.payAtDate);


                    for (var i = 0; i < data.lines.length; i++) {
                    	data.lines[i].periodStart = DateUtils.convertLocaleDateToServer(data.lines[i].periodStart);
                    	data.lines[i].periodEnd = DateUtils.convertLocaleDateToServer(data.lines[i].periodEnd);
					}
                    
                    return angular.toJson(data);
                }
            },

            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                	data.effectiveDate = DateUtils.convertLocaleDateToServer(data.effectiveDate);

                    data.payAtDate = 
                    	DateUtils.convertLocaleDateToServer(data.payAtDate);


                    for (var i = 0; i < data.lines.length; i++) {
                    	data.lines[i].periodStart = DateUtils.convertLocaleDateToServer(data.lines[i].periodStart);
                    	data.lines[i].periodEnd = DateUtils.convertLocaleDateToServer(data.lines[i].periodEnd);
					}
                    
                    return angular.toJson(data);
                }
            },
        });
    });
