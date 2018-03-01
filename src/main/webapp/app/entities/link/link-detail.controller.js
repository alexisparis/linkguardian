(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .controller('LinkDetailController', LinkDetailController);

    LinkDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Link', 'User', 'Tag'];

    function LinkDetailController($scope, $rootScope, $stateParams, previousState, entity, Link, User, Tag) {
        var vm = this;

        vm.link = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('linkguardianApp:linkUpdate', function(event, result) {
            vm.link = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
