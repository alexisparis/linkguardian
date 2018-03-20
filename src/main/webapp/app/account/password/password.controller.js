(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .controller('PasswordController', PasswordController);

    PasswordController.$inject = ['Auth', 'Principal', '$timeout'];

    function PasswordController (Auth, Principal, $timeout) {
        var vm = this;

        vm.changePassword = changePassword;
        vm.doNotMatch = null;
        vm.error = null;
        vm.success = null;
        vm.resetInProgress = false;

        Principal.identity().then(function(account) {
            vm.account = account;
        });

        $timeout(function (){angular.element('[ng-model="vm.password"]').focus();});

        function changePassword () {
            if (vm.password !== vm.confirmPassword) {
                vm.error = null;
                vm.success = null;
                vm.doNotMatch = 'ERROR';
            } else {
                vm.doNotMatch = null;

                vm.resetInProgress = true;
                Auth.changePassword(vm.password).then(function () {
                    vm.resetInProgress = false;
                    vm.error = null;
                    vm.success = 'OK';
                }).catch(function () {
                    vm.resetInProgress = false;
                    vm.success = null;
                    vm.error = 'ERROR';
                });
            }
        }
    }
})();
