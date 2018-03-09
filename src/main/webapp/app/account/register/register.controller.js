'use strict';

angular.module('linkguardianApp')
    .controller('RegisterController', function ($scope, $translate, $timeout, Auth) {
        $scope.success = null;
        $scope.error = null;
        $scope.doNotMatch = null;
        $scope.errorUserExists = null;
        $scope.registerInProgress = false;
        $scope.registerAccount = {};
        $timeout(function (){angular.element('[ng-model="registerAccount.email"]').focus();});

        $scope.register = function () {
            // by default, do not allow to specify a login, use mail
            if ($scope.registerAccount.password !== $scope.confirmPassword) {
                $scope.doNotMatch = 'ERROR';
            } else {
                $scope.registerAccount.langKey = $translate.use();
                $scope.doNotMatch = null;
                $scope.error = null;
                $scope.errorUserExists = null;
                $scope.errorEmailExists = null;

                $scope.registerInProgress = true;

                Auth.createAccount($scope.registerAccount).then(function () {
                    $scope.success = 'OK';
                    $scope.registerInProgress = false;
                }).catch(function (response) {
                    $scope.registerInProgress = false;
                    $scope.success = null;
                    if (response.status === 400 && response.data === 'login already in use') {
                        $scope.errorUserExists = 'ERROR';
                    } else if (response.status === 400 && response.data === 'e-mail address already in use') {
                        $scope.errorEmailExists = 'ERROR';
                    } else {
                        $scope.error = 'ERROR';
                    }
                });
            }
        };
    });
