(function () {
    'use strict';

    angular
        .module('linkguardianApp')
        .factory('UserInitiation', UserInitiation);

    UserInitiation.$inject = ['$resource'];

    function UserInitiation ($resource) {
        var service = $resource('api/users/initiated', {}, {
            'isInitiated': {method: 'GET'},
        });

        return service;
    }
})();
