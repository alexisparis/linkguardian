(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('bookmark-batch', {
            parent: 'entity',
            url: '/bookmark-batch?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'linkguardianApp.bookmarkBatch.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/bookmark-batch/bookmark-batches.html',
                    controller: 'BookmarkBatchController',
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
                    $translatePartialLoader.addPart('bookmarkBatch');
                    $translatePartialLoader.addPart('bookmarkBatchStatus');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('bookmark-batch-detail', {
            parent: 'bookmark-batch',
            url: '/bookmark-batch/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'linkguardianApp.bookmarkBatch.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/bookmark-batch/bookmark-batch-detail.html',
                    controller: 'BookmarkBatchDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('bookmarkBatch');
                    $translatePartialLoader.addPart('bookmarkBatchStatus');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'BookmarkBatch', function($stateParams, BookmarkBatch) {
                    return BookmarkBatch.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'bookmark-batch',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('bookmark-batch-detail.edit', {
            parent: 'bookmark-batch-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bookmark-batch/bookmark-batch-dialog.html',
                    controller: 'BookmarkBatchDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['BookmarkBatch', function(BookmarkBatch) {
                            return BookmarkBatch.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('bookmark-batch.new', {
            parent: 'bookmark-batch',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bookmark-batch/bookmark-batch-dialog.html',
                    controller: 'BookmarkBatchDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                creation_date: null,
                                status_date: null,
                                status: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('bookmark-batch', null, { reload: 'bookmark-batch' });
                }, function() {
                    $state.go('bookmark-batch');
                });
            }]
        })
        .state('bookmark-batch.edit', {
            parent: 'bookmark-batch',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bookmark-batch/bookmark-batch-dialog.html',
                    controller: 'BookmarkBatchDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['BookmarkBatch', function(BookmarkBatch) {
                            return BookmarkBatch.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('bookmark-batch', null, { reload: 'bookmark-batch' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('bookmark-batch.delete', {
            parent: 'bookmark-batch',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bookmark-batch/bookmark-batch-delete-dialog.html',
                    controller: 'BookmarkBatchDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['BookmarkBatch', function(BookmarkBatch) {
                            return BookmarkBatch.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('bookmark-batch', null, { reload: 'bookmark-batch' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
