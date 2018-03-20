(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .controller('RequestResetController', RequestResetController);

    RequestResetController.$inject = ['$timeout', 'Auth', 'errorConstants'];

    function RequestResetController ($timeout, Auth, errorConstants) {
        var vm = this;

        vm.error = null;
        vm.errorEmailNotExists = null;
        vm.requestReset = requestReset;
        vm.resetAccount = {};
        vm.success = null;
        vm.resetInProgress = false;

        $timeout(function (){angular.element('#email').focus();});

        function requestReset () {

            vm.resetInProgress = true;
            vm.error = null;
            vm.errorEmailNotExists = null;

            Auth.resetPasswordInit(vm.resetAccount.email).then(function () {
                vm.resetInProgress = false;
                vm.success = 'OK';
            }).catch(function (response) {
                vm.resetInProgress = false;
                vm.success = null;
                if (response.status === 400 && angular.fromJson(response.data).type === errorConstants.EMAIL_NOT_FOUND_TYPE) {
                    vm.errorEmailNotExists = 'ERROR';
                } else {
                    vm.error = 'ERROR';
                }
            });
        }
    }
})();
