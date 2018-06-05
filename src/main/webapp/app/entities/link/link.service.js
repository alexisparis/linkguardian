(function() {
    'use strict';
    angular
        .module('linkguardianApp')
        .factory('Link', Link)
        .factory('MyLinks', function($resource, $log) {
            return $resource('api/my_links', {}, {
                'getByPage': {
                    method: 'GET',
                    isArray: true
                },
                'countPerTags' : {
                    method: 'GET',
                    url: 'api/my_links/count_per_tags',
                    isArray: true
                },
                'addLink' : {
                    method : 'POST',
                    params:{
                        newurl: '@newurl',
                        tag: '@tag'
                    },
                    interceptor: {
                        response: function(response) {
                            var result = response.resource;
                            console.log("result ", result);
                            result.status = response.status;
                            return result;
                        }
                    }
                },
                'addLinkManually' : {
                    method : 'POST',
                    url: 'api/my_links/manual',
                    params:{
                        newurl: '@newurl',
                        tag: '@tag',
                        description: '@description'
                    },
                    interceptor: {
                        response: function(response) {
                            var result = response.resource;
                            result.status = response.status;
                            return result;
                        }
                    }
                },
                'addTag' : {
                    method : 'PUT',
                    url: 'api/my_links/tag',
                    params:{
                        id: '@id',
                        tag: '@tag'
                    }
                },
                'removeTag' : {
                    method : 'DELETE',
                    url: 'api/my_links/tag',
                    params:{
                        id: '@id',
                        tag: '@tag'
                    }
                },
                'deleteLink' : {
                    method : 'DELETE',
                    params:{
                        id: '@id'
                    },
                    transformResponse: function (data) {
                        console.log("dt : " + data);
                        try {
                            data = angular.fromJson(data);
                            return data;
                        } catch(e) {
                            $log.error("unable to parse " + data);
                        }
                    }
                },
                'markAsRead' : {
                    url : 'api/my_links/read',
                    method : 'PUT',
                    params:{
                        id: '@id'
                    }
                },
                'markAsUnread' : {
                    url : 'api/my_links/unread',
                    method : 'PUT',
                    params:{
                        id: '@id'
                    }
                },
                'updateNote' : {
                    url : 'api/my_links/note',
                    method : 'PUT',
                    params:{
                        id: '@id',
                        score : '@score'
                    }
                },
                'markAsLocked' : {
                    url : 'api/my_links/lock',
                    method : 'PUT',
                    params:{
                        id: '@id'
                    }
                },
                'markAsUnlocked' : {
                    url : 'api/my_links/unlock',
                    method : 'PUT',
                    params:{
                        id: '@id'
                    }
                },
            });
        });
    ;

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
