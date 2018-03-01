(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .controller('BookmarkBatchItemDetailController', BookmarkBatchItemDetailController);

    BookmarkBatchItemDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'BookmarkBatchItem'];

    function BookmarkBatchItemDetailController($scope, $rootScope, $stateParams, previousState, entity, BookmarkBatchItem) {
        var vm = this;

        vm.bookmarkBatchItem = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('linkguardianApp:bookmarkBatchItemUpdate', function(event, result) {
            vm.bookmarkBatchItem = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
