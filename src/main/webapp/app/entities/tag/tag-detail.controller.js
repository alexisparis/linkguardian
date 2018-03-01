(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .controller('TagDetailController', TagDetailController);

    TagDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Tag'];

    function TagDetailController($scope, $rootScope, $stateParams, previousState, entity, Tag) {
        var vm = this;

        vm.tag = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('linkguardianApp:tagUpdate', function(event, result) {
            vm.tag = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
