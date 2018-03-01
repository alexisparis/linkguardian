(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .factory('BookmarkBatchItemSearch', BookmarkBatchItemSearch);

    BookmarkBatchItemSearch.$inject = ['$resource'];

    function BookmarkBatchItemSearch($resource) {
        var resourceUrl =  'api/_search/bookmark-batch-items/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
