(function() {
    'use strict';
    angular
        .module('linkguardianApp')
        .factory('BookmarkBatchItem', BookmarkBatchItem);

    BookmarkBatchItem.$inject = ['$resource', 'DateUtils'];

    function BookmarkBatchItem ($resource, DateUtils) {
        var resourceUrl =  'api/bookmark-batch-items/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.link_creation_date = DateUtils.convertDateTimeFromServer(data.link_creation_date);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
