(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('bookmark-batch-item', {
            parent: 'entity',
            url: '/bookmark-batch-item?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'linkguardianApp.bookmarkBatchItem.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/bookmark-batch-item/bookmark-batch-items.html',
                    controller: 'BookmarkBatchItemController',
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
                    $translatePartialLoader.addPart('bookmarkBatchItem');
                    $translatePartialLoader.addPart('bookmarkBatchItemStatus');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('bookmark-batch-item-detail', {
            parent: 'bookmark-batch-item',
            url: '/bookmark-batch-item/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'linkguardianApp.bookmarkBatchItem.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/bookmark-batch-item/bookmark-batch-item-detail.html',
                    controller: 'BookmarkBatchItemDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('bookmarkBatchItem');
                    $translatePartialLoader.addPart('bookmarkBatchItemStatus');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'BookmarkBatchItem', function($stateParams, BookmarkBatchItem) {
                    return BookmarkBatchItem.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'bookmark-batch-item',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('bookmark-batch-item-detail.edit', {
            parent: 'bookmark-batch-item-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bookmark-batch-item/bookmark-batch-item-dialog.html',
                    controller: 'BookmarkBatchItemDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['BookmarkBatchItem', function(BookmarkBatchItem) {
                            return BookmarkBatchItem.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('bookmark-batch-item.new', {
            parent: 'bookmark-batch-item',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bookmark-batch-item/bookmark-batch-item-dialog.html',
                    controller: 'BookmarkBatchItemDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                url: null,
                                tags: null,
                                link_creation_date: null,
                                status: null,
                                error_msg_code: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('bookmark-batch-item', null, { reload: 'bookmark-batch-item' });
                }, function() {
                    $state.go('bookmark-batch-item');
                });
            }]
        })
        .state('bookmark-batch-item.edit', {
            parent: 'bookmark-batch-item',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bookmark-batch-item/bookmark-batch-item-dialog.html',
                    controller: 'BookmarkBatchItemDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['BookmarkBatchItem', function(BookmarkBatchItem) {
                            return BookmarkBatchItem.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('bookmark-batch-item', null, { reload: 'bookmark-batch-item' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('bookmark-batch-item.delete', {
            parent: 'bookmark-batch-item',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/bookmark-batch-item/bookmark-batch-item-delete-dialog.html',
                    controller: 'BookmarkBatchItemDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['BookmarkBatchItem', function(BookmarkBatchItem) {
                            return BookmarkBatchItem.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('bookmark-batch-item', null, { reload: 'bookmark-batch-item' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
