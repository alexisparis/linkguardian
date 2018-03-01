(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .factory('BookmarkBatchSearch', BookmarkBatchSearch);

    BookmarkBatchSearch.$inject = ['$resource'];

    function BookmarkBatchSearch($resource) {
        var resourceUrl =  'api/_search/bookmark-batches/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
