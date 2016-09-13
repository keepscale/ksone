'use strict';

angular.module('crossfitApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
