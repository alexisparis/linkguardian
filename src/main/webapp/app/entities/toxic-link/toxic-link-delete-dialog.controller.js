(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .controller('ToxicLinkDeleteController',ToxicLinkDeleteController);

    ToxicLinkDeleteController.$inject = ['$uibModalInstance', 'entity', 'ToxicLink'];

    function ToxicLinkDeleteController($uibModalInstance, entity, ToxicLink) {
        var vm = this;

        vm.toxicLink = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ToxicLink.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
