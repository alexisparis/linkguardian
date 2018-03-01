(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .controller('TagDialogController', TagDialogController);

    TagDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Tag'];

    function TagDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Tag) {
        var vm = this;

        vm.tag = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.tag.id !== null) {
                Tag.update(vm.tag, onSaveSuccess, onSaveError);
            } else {
                Tag.save(vm.tag, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('linkguardianApp:tagUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
