(function() {
    'use strict';
    angular
        .module('linkguardianApp')
        .factory('BookmarkBatch', BookmarkBatch);

    BookmarkBatch.$inject = ['$resource', 'DateUtils'];

    function BookmarkBatch ($resource, DateUtils) {
        var resourceUrl =  'api/bookmark-batches/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.creation_date = DateUtils.convertDateTimeFromServer(data.creation_date);
                        data.status_date = DateUtils.convertDateTimeFromServer(data.status_date);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
