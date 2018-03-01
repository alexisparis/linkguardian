(function() {
    'use strict';
    angular
        .module('linkguardianApp')
        .factory('Link', Link);

    Link.$inject = ['$resource', 'DateUtils'];

    function Link ($resource, DateUtils) {
        var resourceUrl =  'api/links/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.creation_date = DateUtils.convertDateTimeFromServer(data.creation_date);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
