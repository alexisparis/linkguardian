(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .factory('LinkSearch', LinkSearch);

    LinkSearch.$inject = ['$resource'];

    function LinkSearch($resource) {
        var resourceUrl =  'api/_search/links/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
