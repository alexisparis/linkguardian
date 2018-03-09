(function() {
    'use strict';
    angular
        .module('linkguardianApp')
        .factory('Tag', Tag)

        .factory('SearchTags', function($resource, $log) {
            return $resource('api/search/tags', {}, {
                'tags': {
                    method: 'GET',
                    params:{
                        filter: '@filter'
                    },
                    isArray: true
                }
            });
        })
    ;

    Tag.$inject = ['$resource'];

    function Tag ($resource) {
        var resourceUrl =  'api/tags/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
