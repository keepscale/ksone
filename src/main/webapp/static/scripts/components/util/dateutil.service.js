'use strict';

angular.module('crossfitApp')
    .service('DateUtils', function () {
      this.convertLocaleDateToServer = function(date) {
        if (date) {
			var utcDate = new Date();
			utcDate.setUTCDate(date.getDate());
			utcDate.setUTCMonth(date.getMonth());
			utcDate.setUTCFullYear(date.getFullYear());
			utcDate.setUTCHours(0);
			utcDate.setUTCMinutes(0);
			utcDate.setUTCSeconds(0);
			utcDate.setUTCMilliseconds(0);
          return utcDate;
        } else {
          return null;
        }
      };
      this.convertLocaleDateTimeToServer = function(date) {
          if (date) {
            var utcDate = this.convertLocaleDateToServer(date);
            utcDate.setUTCHours(date.getHours());
            utcDate.setUTCMinutes(date.getMinutes());
            utcDate.setUTCSeconds(date.getSeconds());
            utcDate.setUTCMilliseconds(0);
            return utcDate;
          } else {
            return null;
          }
        };
      this.convertLocaleDateFromServer = function(date) {
        if (date) {
          var dateString = date.split("-");
          return new Date(dateString[0], dateString[1] - 1, dateString[2]);
        }
        return null;
      };
      this.convertDateTimeFromServer = function(date) {
        if (date) {
          return new Date(date);   
        } else {
          return null;
        }
      };
      this.formatDateAsDate = function(date){
      	return date.getFullYear() + "-" + ("0" + (date.getMonth() + 1)).slice(-2) + "-" + ("0" + date.getDate()).slice(-2);
    };
    this.formatDateAsTime = function(date){
    	return (date.getHours()) + ":" + (date.getMinutes()) + ":" + date.getSeconds();
  };

      this.formatDateAsTimeUTC = function(date){
        	return (date.getUTCHours()) + ":" + (date.getUTCMinutes()) + ":" + date.getUTCSeconds();
      };
      this.parseDateAsTime = function(timestr){
		var parts = timestr.split(':');
		var utcDate = new Date();
		utcDate.setHours(parts[0]);
		utcDate.setMinutes(parts[1]);
		utcDate.setSeconds(parts[2]);
		utcDate.setMilliseconds(0);
		return utcDate;
       };
	this.parseDateAsDate = function(datestr){
		var parts = datestr.split('-');
        var utcDate = new Date();
        utcDate.setUTCDate(parts[2]);
        utcDate.setUTCMonth(parts[1]-1);
        utcDate.setUTCFullYear(parts[0]);
		utcDate.setUTCHours(0);
		utcDate.setUTCMinutes(0);
		utcDate.setUTCSeconds(0);
		utcDate.setUTCMilliseconds(0);
		return utcDate;
	}
      
    });
