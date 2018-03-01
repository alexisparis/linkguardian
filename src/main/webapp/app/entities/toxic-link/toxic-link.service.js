(function() {
    'use strict';
    angular
        .module('linkguardianApp')
        .factory('ToxicLink', ToxicLink);

    ToxicLink.$inject = ['$resource', 'DateUtils'];

    function ToxicLink ($resource, DateUtils) {
        var resourceUrl =  'api/toxic-links/:id';

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
