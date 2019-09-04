'use strict';

angular.module('crossfitApp')
	.controller('PlanningController', function ($rootScope, $scope, $state, $stateParams, $window, Planning, DateUtils, Principal) {

        
    	var parts = $stateParams.startDate ? $stateParams.startDate.split('-') : null;
    	var isMobile = $stateParams.mode == 'mobile';    	
    	var viewName = $rootScope.viewName == null ? 'agendaWeek' : $rootScope.viewName;
    	
        $scope.eventSources = [];
        
    	$scope.loadAll = function() {
        	$scope.eventSources.length = 0;
        	Planning.myPlanning({start:$stateParams.startDate, view:$stateParams.view}, function(result, headers) {
        		
                for (var i = 0; i < result.length; i++) {
                	 $scope.eventSources.push(result[i]);
                }
            });
        };
		
		if (!$stateParams.startDate){
	   		var w = $window.innerWidth;;
	        var mode;
	        var view;
	        var dStart;
	        if ( w >= 991){
	        	mode = 'desktop';
	        	view = 'week';
	        	dStart = moment().startOf('isoweek').toDate();
	        }
	        else{
	        	mode = 'mobile';
	        	view = 'day';
	        	dStart = new Date();
	        }
	        
	    	var start = DateUtils.formatDateAsDate(dStart);
	    	
	        $state.go('planning', {startDate:start, view:view, mode:mode});
	        
	        return;
		}
		else{
			
	    	
	    	$scope.uiConfig = { calendar:{
	    		
					height: isMobile ? 'auto' : 865,
					editable: false,
					eventDurationEditable: false,
					eventStartEditable: false,
					header:{
						left: isMobile ? '' : 'prev,today,next', 
						center: isMobile ? 'prev,today,next' : 'title', 
						right: isMobile ? '' : 'agendaDay,agendaWeek'
					},
									
					firstDay: 1,
					defaultDate: $stateParams.startDate ? new Date(parts[0], parts[1]-1, parts[2]) : new Date(),
					defaultView: $stateParams.view == 'day' ? (isMobile ? 'basicDay' : 'agendaDay' )  : (isMobile ? 'agendaWeek' : 'agendaWeek' ),
	
					allDaySlot: false,
					columnFormat: isMobile ? 'ddd D MMM' : 'dddd D',
					axisFormat: 'HH:mm',
					timeFormat: 'H:mm',
					titleFormat : {
						agenda : 'MMMM',
						month : 'MMMM'
					},
					minTime: "06:00:00",
					selectable: false,
					selectHelper: true,
					eventClick: function(calEvent, jsEvent, calendar) {
						if (calEvent.id){
					    	var eventDate = DateUtils.formatDateAsDate(new Date(calEvent.start));
				            $state.go('planning.preparebooking', {timeSlotId:calEvent.id, bookingDate:eventDate});
						}
				    },
				    viewRender : function(calendar, element){
				    	var startDateCalendar = new Date(calendar.start).toISOString().slice(0, 10);
				    	var view = calendar.type == ("agendaWeek" ) ? "week" : "day";
			            $state.go('planning', {startDate:startDateCalendar, view:view});
				    },
				    eventAfterRender : function(event, element){
				    	if (!isMobile)
				    		return;
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

	    	$scope.loadAll();
		
		}
    });