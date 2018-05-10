(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('database', {
            parent: 'admin',
            url: '/database',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'database.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/admin/database/database.html',
                    controller: 'DatabaseController',
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
