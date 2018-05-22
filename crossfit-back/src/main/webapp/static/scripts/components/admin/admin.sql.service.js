
'use strict';

angular.module('crossfitApp')
    .factory('AdminSQL', function ($resource, DateUtils) {
        return $resource('admin/query', {}, {
            'executeSelect': {
                url: "admin/query/select", 
                method: 'POST',
                isArray: true
            },
	        'executeUpdate': {
	        	url: "admin/query/update", 
	        	method: 'POST',
	        	isArray: true
	        }
        });
    });
