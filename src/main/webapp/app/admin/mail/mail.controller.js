(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .controller('MailController', MailController);

    MailController.$inject = ['MailService', '$log', 'ToasterService'];

    function MailController (MailService, $log, ToasterService) {
        var vm = this;

        vm.mail_title = '';
        vm.mail_content = '<p>' +
            '</p>';
        vm.in_progress = false;

        var mailSendingErrorHandler = function (error) {
            vm.in_progress = false;
            ToasterService.displayError('Error while sending mail : ' + error.data.message, error);
        };
        var mailSendingSuccessHandler = function (data) {
            vm.in_progress = false;
            ToasterService.displaySuccess('Mail(s) sent : ' + data.message);
        };

        vm.sendToAdmin = function() {
            $log.log("send to admin");
            vm.in_progress = true;
            MailService.sendToAdmin({ title: vm.mail_title, message : vm.mail_content})
                .$promise.then(mailSendingSuccessHandler, mailSendingErrorHandler);

        };
        vm.sendToAll = function() {
            $log.log("send to all");
            vm.in_progress = true;
            MailService.sendToAll({ title: vm.mail_title, message : vm.mail_content})
                .$promise.then(mailSendingSuccessHandler, mailSendingErrorHandler);
        };
    }
})();
