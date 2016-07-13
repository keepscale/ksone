'use strict';

angular.module('crossfitApp')
    .service('DateUtils', function () {
      this.convertLocaleDateToServer = function(date) {
        if (date) {
          var utcDate = new Date();
          utcDate.setUTCDate(date.getDate());
          utcDate.setUTCMonth(date.getMonth());
          utcDate.setUTCFullYear(date.getFullYear());
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
      }
    });
