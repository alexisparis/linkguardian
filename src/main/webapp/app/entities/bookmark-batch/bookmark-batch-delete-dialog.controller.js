(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .controller('BookmarkBatchDeleteController',BookmarkBatchDeleteController);

    BookmarkBatchDeleteController.$inject = ['$uibModalInstance', 'entity', 'BookmarkBatch'];

    function BookmarkBatchDeleteController($uibModalInstance, entity, BookmarkBatch) {
        var vm = this;

        vm.bookmarkBatch = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            BookmarkBatch.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
