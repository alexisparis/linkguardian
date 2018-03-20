'use strict';

angular.module('linkguardianApp')
    .controller('LoginController', function ($rootScope, $scope, $state, $timeout, Auth, $log, $window) {
        $scope.user = {};
        $scope.errors = {};
        $scope.signInInProgress = false;

        $scope.rememberMe = true;
        $timeout(function (){angular.element('[ng-model="username"]').focus();});
        $scope.login = function (event) {
            console.log("call login");
            event.preventDefault();
            $scope.signInInProgress = true;
            Auth.login({
                username: $scope.username,
                password: $scope.password,
                rememberMe: $scope.rememberMe
            }).then(function () {
                $scope.signInInProgress = false;
                $scope.authenticationError = false;
                $scope.blockedError = false;
                $state.go('home');
            }).catch(function (error) {
                $log.log("error while auth", error);
                $scope.signInInProgress = false;
                $scope.authenticationError = false;
                $scope.blockedError = false;
                if (error && error.data && error.data.message == 'blocked') {
                    $scope.blockedError = true;
                } else {
                    $scope.authenticationError = true;
                }
            });
        };
    });


// (function() {
//     'use strict';
//
//     angular
//         .module('linkguardianApp')
//         .controller('LoginController', LoginController);
//
//     LoginController.$inject = ['$rootScope', '$state', '$timeout', 'Auth', '$uibModalInstance'];
//
//     function LoginController ($rootScope, $state, $timeout, Auth, $uibModalInstance) {
//         var vm = this;
//
//         vm.authenticationError = false;
//         vm.cancel = cancel;
//         vm.credentials = {};
//         vm.login = login;
//         vm.password = null;
//         vm.register = register;
//         vm.rememberMe = true;
//         vm.requestResetPassword = requestResetPassword;
//         vm.username = null;
//
//         $timeout(function (){angular.element('#username').focus();});
//
//         function cancel () {
//             vm.credentials = {
//                 username: null,
//                 password: null,
//                 rememberMe: true
//             };
//             vm.authenticationError = false;
//             $uibModalInstance.dismiss('cancel');
//         }
//
//         function login (event) {
//             event.preventDefault();
//             Auth.login({
//                 username: vm.username,
//                 password: vm.password,
//                 rememberMe: vm.rememberMe
//             }).then(function () {
//                 vm.authenticationError = false;
//                 $uibModalInstance.close();
//                 if ($state.current.name === 'register' || $state.current.name === 'activate' ||
//                     $state.current.name === 'finishReset' || $state.current.name === 'requestReset') {
//                     $state.go('home');
//                 }
//
//                 $rootScope.$broadcast('authenticationSuccess');
//
//                 // previousState was set in the authExpiredInterceptor before being redirected to login modal.
//                 // since login is successful, go to stored previousState and clear previousState
//                 if (Auth.getPreviousState()) {
//                     var previousState = Auth.getPreviousState();
//                     Auth.resetPreviousState();
//                     $state.go(previousState.name, previousState.params);
//                 }
//             }).catch(function () {
//                 vm.authenticationError = true;
//             });
//         }
//
//         function register () {
//             $uibModalInstance.dismiss('cancel');
//             $state.go('register');
//         }
//
//         function requestResetPassword () {
//             $uibModalInstance.dismiss('cancel');
//             $state.go('requestReset');
//         }
//     }
// })();
