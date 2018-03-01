(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .controller('ToxicLinkDetailController', ToxicLinkDetailController);

    ToxicLinkDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'DataUtils', 'entity', 'ToxicLink'];

    function ToxicLinkDetailController($scope, $rootScope, $stateParams, previousState, DataUtils, entity, ToxicLink) {
        var vm = this;

        vm.toxicLink = entity;
        vm.previousState = previousState.name;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('linkguardianApp:toxicLinkUpdate', function(event, result) {
            vm.toxicLink = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
