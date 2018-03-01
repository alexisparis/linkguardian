(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .factory('ToxicLinkSearch', ToxicLinkSearch);

    ToxicLinkSearch.$inject = ['$resource'];

    function ToxicLinkSearch($resource) {
        var resourceUrl =  'api/_search/toxic-links/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
