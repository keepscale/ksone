'use strict';

angular.module('crossfitApp', ['LocalStorageModule', 'tmh.dynamicLocale', 'pascalprecht.translate', 'ui.calendar',
               'ui.bootstrap', // for modal dialogs
    'ngResource', 'ui.router', 'ngCookies', 'ngCacheBuster', 'ui.bootstrap.datetimepicker', 'ng-fastclick', 'nvd3', 'lr.upload', 'signature'])

    .run(function ($rootScope, $location, $window, $http, $state, $translate, DateUtils, Language, Auth, Principal, CrossfitBox, Version) {

        $rootScope.$on('$stateChangeStart', function (event, toState, toStateParams) {
            $rootScope.toState = toState;
            $rootScope.toStateParams = toStateParams;

            if (Principal.isIdentityResolved()) {
                Auth.authorize();
            }
            
            // Update the language
            Language.getCurrent().then(function (language) {
                $translate.use(language);
            });
            
        });

        $rootScope.$on('$stateChangeSuccess',  function(event, toState, toParams, fromState, fromParams) {
            var titleKey = 'global.title' ;

            $rootScope.previousStateName = fromState.name;
            $rootScope.previousStateParams = fromParams;

            // Set the page title key to the one configured in state or use default one
            if (toState.data.pageTitle) {
                titleKey = toState.data.pageTitle;
            }
            
            $translate(titleKey).then(function (title) {
                // Change window title with translated one
                $window.document.title = title;
            });
            
        });
        
        $rootScope.back = function() {
            // If previous state is 'activate' or do not exist go to 'home'
            if ($rootScope.previousStateName === 'activate' || $state.get($rootScope.previousStateName) === null) {
                $state.go('home');
            } else {
                $state.go($rootScope.previousStateName, $rootScope.previousStateParams);
            }
        };
        
        CrossfitBox.current(function(result){
        	$rootScope.socialEnabled = result.socialEnabled;
        	$rootScope.box = result;
        });
        
        Version.current(function(v){
            $rootScope.VERSION = v.content;
        });
    })
    .config(function ($compileProvider, $stateProvider, $urlRouterProvider, $httpProvider, $locationProvider, $translateProvider, tmhDynamicLocaleProvider, httpRequestInterceptorCacheBusterProvider) {

    	$stateProvider.VERSION = window.VERSION;
        
        //enable CSRF
        $httpProvider.defaults.xsrfCookieName = 'CSRF-TOKEN';
        $httpProvider.defaults.xsrfHeaderName = 'X-CSRF-TOKEN';

        //Cache everything except rest api requests
        httpRequestInterceptorCacheBusterProvider.setMatchlist([/.*api.*/, /.*protected.*/], true);

        $urlRouterProvider.otherwise('/');
        $stateProvider.state('site', {
            'abstract': true,
            views: {
                'navbar@': {
                    templateUrl: 'scripts/components/navbar/navbar.html?v='+$stateProvider.VERSION,
                    controller: 'NavbarController'
                },
                'navbarlist@': {
                    templateUrl: 'scripts/components/navbar/navbarlist.html?v='+$stateProvider.VERSION,
                    controller: 'NavbarListController'
                }
            },
            resolve: {
                authorize: ['Auth',
                    function (Auth) {
                        return Auth.authorize();
                    }
                ],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('global');
                }]
            }
        });

        $httpProvider.interceptors.push('errorHandlerInterceptor');
        $httpProvider.interceptors.push('authExpiredInterceptor');
        $httpProvider.interceptors.push('notificationInterceptor');
        
        // Initialize angular-translate
        $translateProvider.useLoader('$translatePartialLoader', {
            urlTemplate: 'i18n/{lang}/{part}.json'
        });

        $translateProvider.preferredLanguage('fr');
        $translateProvider.useCookieStorage();
        $translateProvider.useSanitizeValueStrategy('escaped');
        $translateProvider.addInterpolation('$translateMessageFormatInterpolation');

        tmhDynamicLocaleProvider.localeLocationPattern('bower_components/angular-i18n/angular-locale_{{locale}}.js');
        tmhDynamicLocaleProvider.useCookieStorage();
        tmhDynamicLocaleProvider.storageKey('NG_TRANSLATE_LANG_KEY');
        
        $compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|ftp|mailto|webcal|chrome-extension):/);

        
    })
    .filter('dayOfWeek', function($filter) {
		return function(input) {
			var d = new Date();
			var currentDay = d.getDay();
			var distance = input - currentDay;
			d.setDate(d.getDate() + distance);
		    return $filter('date')(d, 'EEEE');
		};
	})
    .filter('hour', function($filter) {
		return function(input) {
			if (!input)
				return;
		    return $filter('date')(input, 'H:mm');
		};
	});
