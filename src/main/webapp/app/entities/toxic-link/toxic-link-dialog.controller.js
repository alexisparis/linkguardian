(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .controller('ToxicLinkDialogController', ToxicLinkDialogController);

    ToxicLinkDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'ToxicLink'];

    function ToxicLinkDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, ToxicLink) {
        var vm = this;

        vm.toxicLink = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.toxicLink.id !== null) {
                ToxicLink.update(vm.toxicLink, onSaveSuccess, onSaveError);
            } else {
                ToxicLink.save(vm.toxicLink, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('linkguardianApp:toxicLinkUpdate', result);
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
