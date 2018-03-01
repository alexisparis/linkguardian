(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('toxic-link', {
            parent: 'entity',
            url: '/toxic-link?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'linkguardianApp.toxicLink.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/toxic-link/toxic-links.html',
                    controller: 'ToxicLinkController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('toxicLink');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('toxic-link-detail', {
            parent: 'toxic-link',
            url: '/toxic-link/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'linkguardianApp.toxicLink.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/toxic-link/toxic-link-detail.html',
                    controller: 'ToxicLinkDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('toxicLink');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ToxicLink', function($stateParams, ToxicLink) {
                    return ToxicLink.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'toxic-link',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('toxic-link-detail.edit', {
            parent: 'toxic-link-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/toxic-link/toxic-link-dialog.html',
                    controller: 'ToxicLinkDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ToxicLink', function(ToxicLink) {
                            return ToxicLink.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('toxic-link.new', {
            parent: 'toxic-link',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/toxic-link/toxic-link-dialog.html',
                    controller: 'ToxicLinkDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                email: null,
                                url: null,
                                creation_date: null,
                                error: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('toxic-link', null, { reload: 'toxic-link' });
                }, function() {
                    $state.go('toxic-link');
                });
            }]
        })
        .state('toxic-link.edit', {
            parent: 'toxic-link',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/toxic-link/toxic-link-dialog.html',
                    controller: 'ToxicLinkDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ToxicLink', function(ToxicLink) {
                            return ToxicLink.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('toxic-link', null, { reload: 'toxic-link' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('toxic-link.delete', {
            parent: 'toxic-link',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/toxic-link/toxic-link-delete-dialog.html',
                    controller: 'ToxicLinkDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ToxicLink', function(ToxicLink) {
                            return ToxicLink.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('toxic-link', null, { reload: 'toxic-link' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
