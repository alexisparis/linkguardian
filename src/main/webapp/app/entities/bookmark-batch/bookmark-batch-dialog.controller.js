(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .controller('BookmarkBatchDialogController', BookmarkBatchDialogController);

    BookmarkBatchDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'BookmarkBatch', 'User', 'BookmarkBatchItem'];

    function BookmarkBatchDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, BookmarkBatch, User, BookmarkBatchItem) {
        var vm = this;

        vm.bookmarkBatch = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.users = User.query();
        vm.bookmarkbatchitems = BookmarkBatchItem.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.bookmarkBatch.id !== null) {
                BookmarkBatch.update(vm.bookmarkBatch, onSaveSuccess, onSaveError);
            } else {
                BookmarkBatch.save(vm.bookmarkBatch, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('linkguardianApp:bookmarkBatchUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.creation_date = false;
        vm.datePickerOpenStatus.status_date = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
