'use strict';

angular.module('crossfitApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('home', {
                parent: 'site',
                url: '/',
                data: {
                    roles: [],
                    pageTitle: 'global.title'
                },
                views: {
                    'content@': {

                        templateProvider: function(Principal, $stateParams, $templateFactory, $rootScope){
                       	 if(Principal.isInAnyRole(['ROLE_COACH', 'ROLE_MANAGER', 'ROLE_ADMIN'])){
        	                	return $templateFactory.fromUrl('scripts/app/main/main.manager.html?v='+$rootScope.VERSION, $stateParams);
        	                } 
        	                else {
        	                	
        	                	return $templateFactory.fromUrl('scripts/app/main/main.user.html?v='+$rootScope.VERSION, $stateParams);
        	                }
                        },
                        controllerProvider: function(Principal){
                        	 if(Principal.isInAnyRole(['ROLE_COACH', 'ROLE_MANAGER', 'ROLE_ADMIN'])){
                            	 return 'MainManagerController';
         	                } 
         	                else {
         	                	 return 'MainUserController';
         	                }
                    	}
                    }
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                        $translatePartialLoader.addPart('main');
                        $translatePartialLoader.addPart('booking');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            });
    });
