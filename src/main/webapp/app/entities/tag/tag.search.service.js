(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .factory('TagSearch', TagSearch);

    TagSearch.$inject = ['$resource'];

    function TagSearch($resource) {
        var resourceUrl =  'api/_search/tags/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
