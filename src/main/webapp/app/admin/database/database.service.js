(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .factory('DatabaseService', DatabaseService);

    DatabaseService.$inject = ['$resource', '$log'];

    function DatabaseService ($resource, $log) {
        var service = $resource('database', {},
            {
                'getTableStatistics' : {
                    method: 'GET',
                    url: 'database/table/statistics',
                    isArray: true
                }
            }
            );

        return service;
    }
})();
