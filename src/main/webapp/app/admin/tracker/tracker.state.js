(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider', 'WEBSOCKET_ACTIVATED'];

    function stateConfig($stateProvider, WEBSOCKET_ACTIVATED) {
        $stateProvider.state('jhi-tracker', {
            parent: 'admin',
            url: '/tracker',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'tracker.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/admin/tracker/tracker.html',
                    controller: 'JhiTrackerController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('tracker');
                    return $translate.refresh();
                }]
            },
            onEnter: ['JhiTrackerService', function(JhiTrackerService) {
                // XXAP websocket
                if (WEBSOCKET_ACTIVATED) {
                    JhiTrackerService.subscribe();
                }
            }],
            onExit: ['JhiTrackerService', function(JhiTrackerService) {
                // XXAP websocket
                if (WEBSOCKET_ACTIVATED) {
                    JhiTrackerService.unsubscribe();
                }
            }]
        });
    }
})();
