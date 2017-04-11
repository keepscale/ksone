'use strict';

angular.module('crossfitApp')
	.controller('PlanningResourcesController', function ($rootScope, $scope, $state, $stateParams, $window, DateUtils, Principal, Resource) {

        
    	var parts = $stateParams.startDate ? $stateParams.startDate.split('-') : null;
    	var isMobile = $stateParams.mode == 'mobile';    	
    	var viewName = $rootScope.viewName == null ? 'agendaWeek' : $rootScope.viewName;
    	
    	Resource.query({}, function(result){
    		$scope.availableResources = result;
    		if($stateParams.resourceId){
    			for (var i = 0; i < $scope.availableResources.length; i++) {
                	 var r = $scope.availableResources[i];
                	 if (r.id == $stateParams.resourceId){
                		 $scope.currentResource = r;
                		 break;
                	 }
                }
    		}
    		if ($scope.currentResource == null){
            	$scope.currentResource = result[0];
    		}
    		$scope.refreshSelectResource($scope.currentResource);
		});
        $scope.eventSources = [];
        
        $scope.prepareRent = function(){
        	$state.go('planning-resources.preparebooking', {resourceId:$scope.currentResource.id});
        }
        
        $scope.refreshSelectResource = function(resource){
        	$state.go('planning-resources', {resourceId:$scope.currentResource.id}, {notify:false});
	    	$scope.loadAll();		
        };
        
    	$scope.loadAll = function() {
        	$scope.eventSources.length = 0;
        	console.log("Affichage du planning " + $scope.currentResource.name)
        	/*Planning.myPlanning({start:$stateParams.startDate, view:$stateParams.view}, function(result, headers) {
        		
                for (var i = 0; i < result.length; i++) {
                	 $scope.eventSources.push(result[i]);
                }
            });*/
        };
		
		if (!$stateParams.startDate){
	   		var w = $window.innerWidth;;
	        var mode;
	        var view;
	        var dStart = new Date();
	        var dEnd = new Date();
	        if ( w >= 991){
	        	mode = 'desktop';
	        	view = 'week';
	        	dEnd.setDate(dEnd.getDate() + 7); 
	        }
	        else{
	        	mode = 'mobile';
	        	view = 'day';
	        	dEnd.setDate(dEnd.getDate() + 1);
	        }
	        
	    	var start = DateUtils.formatDateAsDate(dStart);
	    	var end = DateUtils.formatDateAsDate(dEnd);
	    	
	        $state.go('planning-resources', {startDate:start, endDate:end, view:view, mode:mode});
	        
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
					selectHelper: false,
				    viewRender : function(calendar, element){
				    	var startDateCalendar = new Date(calendar.start).toISOString().slice(0, 10);
				    	var view = calendar.type == ("agendaWeek" ) ? "week" : "day";
			            $state.go('planning-resources', {startDate:startDateCalendar, view:view});
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

		}
    });