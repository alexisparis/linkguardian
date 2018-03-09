'use strict';

angular.module('linkguardianApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('home', {
                parent: 'app',
                url: '/',
                data: {
                    authorities: [],
                    pageTitle: 'main.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/home/main.html',
                        controller: 'MainController'
                    }
                },
                resolve: {
                    "check":function(Principal, $state) {
                        if (Principal && Principal.isAuthenticated === false) {
                            $state.go("login");
                        }
                    },
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                        $translatePartialLoader.addPart('main');
                        return $translate.refresh();
                    }],
                    freeTour: function() { return false; }
                }
            })
            .state('free_tour', {
                parent: 'app',
                url: '/demo',
                data: {
                    authorities: [],
                    pageTitle: 'main.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/home/main.html',
                        controller: 'MainController'
                    }
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                        $translatePartialLoader.addPart('main');
                        return $translate.refresh();
                    }],
                    freeTour: function() { return true; }
                }
            });
    });
