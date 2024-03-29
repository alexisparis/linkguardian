(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .controller('NavbarController', NavbarController);

    NavbarController.$inject = ['$window', '$state', 'Auth', 'Principal', 'ProfileService', 'LoginService', '$mdDialog', 'TEMPLATES_PATH', 'ENV', 'VERSION'];

    function NavbarController ($window, $state, Auth, Principal, ProfileService, LoginService, $mdDialog, TEMPLATES_PATH, ENV, VERSION) {
        var vm = this;

        vm.isNavbarCollapsed = true;
        vm.isAuthenticated = Principal.isAuthenticated;

        ProfileService.getProfileInfo().then(function(response) {
            vm.inProduction = response.inProduction;
            vm.swaggerEnabled = response.swaggerEnabled;
        });

        vm.env = ENV;
        vm.login = login;
        vm.logout = logout;
        vm.toggleNavbar = toggleNavbar;
        vm.collapseNavbar = collapseNavbar;
        vm.$state = $state;

        function login() {
            collapseNavbar();
            LoginService.open();
        }

        function logout() {
            collapseNavbar();
            Auth.logout();
            $state.go('login');
        }

        function toggleNavbar() {
            vm.isNavbarCollapsed = !vm.isNavbarCollapsed;
        }

        function collapseNavbar() {
            vm.isNavbarCollapsed = true;
        }

        vm.reload = function() {
            $window.location.reload();
        }

        function AboutDialogController($scope, $mdDialog, VERSION) {

            $scope.version = VERSION;
            $scope.cancel = function() {
                $mdDialog.cancel();
            };
        };

        vm.showAboutDialog = function() {

            $mdDialog.show({
                controller: AboutDialogController,
                templateUrl: TEMPLATES_PATH + 'aboutDialog.html',
                parent: angular.element(document.body),
                clickOutsideToClose: true
            });
        };
    }
})();
