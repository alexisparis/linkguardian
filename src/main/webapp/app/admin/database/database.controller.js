(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .controller('DatabaseController', DatabaseController);

    DatabaseController.$inject = ['DatabaseService', '$log', 'ToasterService'];

    function DatabaseController (DatabaseService, $log, ToasterService) {
        var vm = this;

        vm.in_progress = false;
        vm.stats = [];

        vm.refresh = function() {
            $log.log("send to admin");
            vm.in_progress = true;
            DatabaseService.getTableStatistics()
                .$promise.then(
                    function(data) {
                        vm.stats = data;
                        vm.in_progress = false;
                        ToasterService.displaySuccess('Content refreshed');
                    },
                    function(error) {
                        vm.in_progress = false;
                        ToasterService.displayError('Error while retrieving data : ' + error.data.message, error);
                    });

        };
        vm.refresh();
    }
})();
