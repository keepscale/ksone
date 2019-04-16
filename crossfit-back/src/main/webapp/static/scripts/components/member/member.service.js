'use strict';

angular.module('crossfitApp')
    .factory('Member', function ($resource, DateUtils) {
        return $resource('api/members/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'queryQuick': { 
            	method: 'GET', 
                url: 'api/members/quick', 
                isArray: true
            },
            'health': { 
            	method: 'GET', 
                url: 'api/members/health', 
                isArray: false},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.medicalCertificateDate = DateUtils.convertLocaleDateFromServer(data.medicalCertificateDate);
                    for (var i = 0; i < data.subscriptions.length; i++) {
						var sub = data.subscriptions[i];
						sub.subscriptionStartDate = DateUtils.convertLocaleDateFromServer(sub.subscriptionStartDate);
						sub.subscriptionEndDate = DateUtils.convertLocaleDateFromServer(sub.subscriptionEndDate);
						sub.directDebitAfterDate = DateUtils.convertLocaleDateFromServer(sub.directDebitAfterDate);
						sub.signatureDate = DateUtils.convertDateTimeFromServer(sub.signatureDate);
					}
                    for (var i = 0; i < data.mandates.length; i++) {
						var m = data.mandates[i];
						m.signatureDate = DateUtils.convertDateTimeFromServer(m.signatureDate);
					}
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {

                    data.medicalCertificateDate = DateUtils.convertLocaleDateToServer(data.medicalCertificateDate);
                    for (var i = 0; i < data.subscriptions.length; i++) {
						var sub = data.subscriptions[i];
						sub.subscriptionStartDate = DateUtils.convertLocaleDateToServer(sub.subscriptionStartDate);
						sub.subscriptionEndDate = DateUtils.convertLocaleDateToServer(sub.subscriptionEndDate);
						sub.directDebitAfterDate = DateUtils.convertLocaleDateToServer(sub.directDebitAfterDate);
                    }
                    
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {

                    data.medicalCertificateDate = DateUtils.convertLocaleDateToServer(data.medicalCertificateDate);
                    for (var i = 0; i < data.subscriptions.length; i++) {
						var sub = data.subscriptions[i];
						sub.subscriptionStartDate = DateUtils.convertLocaleDateToServer(sub.subscriptionStartDate);
						sub.subscriptionEndDate = DateUtils.convertLocaleDateToServer(sub.subscriptionEndDate);
						sub.directDebitAfterDate = DateUtils.convertLocaleDateToServer(sub.directDebitAfterDate);
                    }
                    
                    return angular.toJson(data);
                }
            },
            'resetaccount': {
                method: 'PUT',
                url: 'api/members/:id/resetaccount', 
                params : {id: '@id'}
            },
            'lock': {
                method: 'PUT',
                url: 'api/members/:id/lock', 
                params : {id: '@id'}
            },
            'healthIndicators': { 
            	method: 'GET',
                url: 'api/members/healthIndicators',  
                isArray: true
            }
        });
    });
