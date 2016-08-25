'use strict';

angular.module('crossfitApp')
	.controller('MainUserController', function ($rootScope, $scope, $state, $stateParams, $window, TimeSlot, TimeSlotEvent, DateUtils, Principal) {
        
		Principal.identity().then(function(account) {
            $scope.account = account;
            $scope.isAuthenticated = Principal.isAuthenticated;
        });
        
        $scope.nbBookingPossible = 3;
        
        
        
        
        
        $scope.eventSources = [];
    	var parts = $stateParams.startDate ? $stateParams.startDate.split('-') : null;

    	var viewName = $rootScope.viewName == null ? 'agendaWeek' : $rootScope.viewName;
    	
    	var uiConfigDesktop = { calendar:{
				height: 865,
				editable: false,
				header:{
					left: 'prev,today,next', center: 'title', right: 'agendaDay,agendaWeek,month'
				},
				firstDay: 1,
				defaultDate: $stateParams.startDate ? new Date(parts[0], parts[1]-1, parts[2]) : new Date(),
				defaultView: viewName,
				allDaySlot: false,
				columnFormat: 'dddd D',
				axisFormat: 'HH:mm',
				timeFormat: 'H:mm',
				titleFormat : {
					agenda : 'MMMM',
					month : 'MMMM'
				},
				minTime: "06:00:00",
				selectable: false,
				selectHelper: true,
			    viewRender : function(view, element){

			    	$scope.startDateCalendar = new Date(view.start).toISOString().slice(0, 10);
			    	$scope.endDateCalendar = new Date(view.end).toISOString().slice(0, 10);
		            $state.go('home', {startDate:$scope.startDateCalendar, endDate:$scope.endDateCalendar},{notify:false});
		            $scope.loadAll();
			    }
			}
    	};
    		
    	var uiConfigMobile = { calendar:{
				height: "auto",
				editable: false,
				header:{
					left: '', center: 'prev,today,next', right: ''
				},
				defaultDate: $stateParams.startDate ? new Date(parts[0], parts[1]-1, parts[2]) : new Date(),
				defaultView: 'basicDay',
				columnFormat: 'ddd D MMM',
				timeFormat: 'H:mm',
				selectable: false,
				selectHelper: true,
				
			    viewRender : function(view, element){
			    	$scope.startDateCalendar = new Date(view.start).toISOString().slice(0, 10);
			    	$scope.endDateCalendar = new Date(view.end).toISOString().slice(0, 10);
		            $state.go('home', {startDate:$scope.startDateCalendar, endDate:$scope.endDateCalendar},{notify:false});
		            $scope.loadAll();
			    },
			    
			    eventAfterRender : function(event, element){
			    	$(".fc-time").css("font-size","x-large");
			    	$(".fc-time").css("display","block");
			    	$(".fc-time").css("text-align","center");
			    	
			    	$(".fc-title").css("font-size","small");
			    	$(".fc-title").css("display","block");
			    	$(".fc-title").css("text-align","center");
			    	
		            element.height(50);
		            $('.fc-time').css('display:block;');
			    }
			}
    	};
    	
    	$scope.loadAll = function() {
        	$scope.eventSources.length = 0;
        	TimeSlotEvent.query({end:$scope.endDateCalendar,start:$scope.startDateCalendar}, function(result, headers) {
                for (var i = 0; i < result.length; i++) {
                	 $scope.eventSources.push(result[i]);
                }
            });
        };
    	

        var w = angular.element($window);
        $scope.getWindowDimensions = function () {

            return {
                'w': w.width()
            };
        };
        $scope.$watch($scope.getWindowDimensions, function (newValue, oldValue) {
            
            $scope.windowWidth = newValue.w;

            if($scope.windowWidth >= 991){
            	$scope.uiConfig = uiConfigDesktop;
            }else{
            	$scope.uiConfig = uiConfigMobile;
            }
        }, true);

        w.bind('resize', function () {
            $scope.$apply();
        });
        
        
        
        
        
        
        
    });