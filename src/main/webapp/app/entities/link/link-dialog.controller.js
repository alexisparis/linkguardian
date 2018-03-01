(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .controller('LinkDialogController', LinkDialogController);

    LinkDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Link', 'User', 'Tag'];

    function LinkDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Link, User, Tag) {
        var vm = this;

        vm.link = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.users = User.query();
        vm.tags = Tag.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.link.id !== null) {
                Link.update(vm.link, onSaveSuccess, onSaveError);
            } else {
                Link.save(vm.link, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('linkguardianApp:linkUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.creation_date = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
