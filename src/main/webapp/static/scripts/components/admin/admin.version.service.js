
'use strict';

angular.module('crossfitApp')
    .factory('AdminVersion', function ($resource, DateUtils) {
        return $resource('admin/version', {}, {
            'query': { method: 'GET', isArray: true},
	        'activate': {
	        	url: "admin/version/activate", 
	        	method: 'POST',
	        	isArray: false
	        }
        });
    });
