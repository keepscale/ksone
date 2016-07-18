'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('home', {
                parent: 'site',
                url: '/',
                data: {
                    roles: []
                },
                templateProvider: function(Principal, $stateParams, $templateFactory){
	                if(Principal.isInAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')){
	                	return $templateFactory.fromUrl('scripts/app/main/main.admin.html', $stateParams);
	                } 
	                else {
	                	return $templateFactory.fromUrl('scripts/app/main/main.user.html', $stateParams);
	                }
                },
                controllerProvider: function(Principal){
                	 if(Principal.isInAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')){
                    	 return 'MainController';
 	                } 
 	                else {
 	                	 return 'MainController';
 	                }
            	},
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                        $translatePartialLoader.addPart('main');
                        return $translate.refresh();
                    }]
                }
            });
    });
