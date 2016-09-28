'use strict';

angular.module('crossfitApp')
    .factory('Password', function ($resource) {
        return $resource('api/account/change_password', {}, {
            'recover': {
                method: 'POST',
                url: "recover/password"
            }
        });
    });