(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .factory('errorHandlerInterceptor', errorHandlerInterceptor);

    errorHandlerInterceptor.$inject = ['$q', '$rootScope', '$log'];

    function errorHandlerInterceptor ($q, $rootScope, $log) {
        var service = {
            responseError: responseError
        };

        return service;

        function responseError (response) {
            $log.error("error handler interceptor received : ", response);

            if (!(response.status === 401 && (response.data === '' || (response.data.path && response.data.path.indexOf('/api/account') === 0 )))) {
                $rootScope.$emit('linkguardianApp.httpError', response);
            }
            return $q.reject(response);
        }
    }
})();
