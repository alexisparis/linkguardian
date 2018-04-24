(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('mail', {
            parent: 'admin',
            url: '/mail',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'mail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/admin/mail/mail.html',
                    controller: 'MailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    //$translatePartialLoader.addPart('mail');
                    return $translate.refresh();
                }]
            }
        });
    }
})();
