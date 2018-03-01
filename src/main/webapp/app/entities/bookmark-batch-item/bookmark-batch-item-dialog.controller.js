(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .controller('BookmarkBatchItemDialogController', BookmarkBatchItemDialogController);

    BookmarkBatchItemDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'BookmarkBatchItem'];

    function BookmarkBatchItemDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, BookmarkBatchItem) {
        var vm = this;

        vm.bookmarkBatchItem = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.bookmarkBatchItem.id !== null) {
                BookmarkBatchItem.update(vm.bookmarkBatchItem, onSaveSuccess, onSaveError);
            } else {
                BookmarkBatchItem.save(vm.bookmarkBatchItem, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('linkguardianApp:bookmarkBatchItemUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.link_creation_date = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
