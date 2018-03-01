(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .controller('BookmarkBatchItemDeleteController',BookmarkBatchItemDeleteController);

    BookmarkBatchItemDeleteController.$inject = ['$uibModalInstance', 'entity', 'BookmarkBatchItem'];

    function BookmarkBatchItemDeleteController($uibModalInstance, entity, BookmarkBatchItem) {
        var vm = this;

        vm.bookmarkBatchItem = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            BookmarkBatchItem.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
