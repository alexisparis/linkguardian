(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .factory('MailService', MailService);

    MailService.$inject = ['$resource', '$log'];

    function MailService ($resource, $log) {
        var service = $resource('management/mail', {},
            {
                'sendToAdmin': {
                    method: 'POST',
                    params:{
                        title: '@title',
                        message: '@message',
                        to: 'ADMIN'
                    }},
                'sendToAll': {
                    method: 'POST',
                    params:{
                        title: '@title',
                        message: '@message',
                        to: 'ALL'
                    },
                }
            }
            );

        return service;
    }
})();
