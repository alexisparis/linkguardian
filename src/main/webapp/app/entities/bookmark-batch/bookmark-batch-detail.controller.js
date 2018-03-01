(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .controller('BookmarkBatchDetailController', BookmarkBatchDetailController);

    BookmarkBatchDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'BookmarkBatch', 'User', 'BookmarkBatchItem'];

    function BookmarkBatchDetailController($scope, $rootScope, $stateParams, previousState, entity, BookmarkBatch, User, BookmarkBatchItem) {
        var vm = this;

        vm.bookmarkBatch = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('linkguardianApp:bookmarkBatchUpdate', function(event, result) {
            vm.bookmarkBatch = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
