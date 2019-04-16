'use strict';

angular.module('crossfitApp')
    .factory('Signature', function ($resource, DateUtils) {
        return $resource('', {}, {
            'sign-subscription': {
                method: 'POST',
                url: 'api/sign/subscription', 
                transformRequest: function (data) {
                	var signature = {
                		id : data.id,
            			signatureDate: DateUtils.convertLocaleDateTimeToServer(data.signatureDate),
						signatureDataEncoded: data.signatureDataEncoded
                	}
                    
                    return angular.toJson(signature);
                }
            },
            'sign-mandate': {
                method: 'POST',
                url: 'api/sign/mandate', 
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
