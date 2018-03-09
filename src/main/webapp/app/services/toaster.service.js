'use strict';

angular.module('linkguardianApp')
    .factory('ToasterService', function (translateFilter, $timeout, $window,
                                         TEMPLATES_PATH, $mdToast) {

        var toastHideDelay = 2000;

        var service =  {

            hide : function(toast) {
                return $mdToast.hide(toast);
            },

            /**
             * display an error message
             * @param msg
             */
            displayError : function(msg) {
                var toast = $mdToast.simple()
                    .theme("error2")
                    .textContent(msg)
                    .position('top right')
                    .hideDelay(toastHideDelay);
                toast._options.templateUrl = TEMPLATES_PATH + 'customToast.html';
                return $mdToast.show(toast);
            },

            /** display a message with a spinner
             *
             * @param msg
             * @returns {*}
             */
            displaySpinner : function(msg, withTransition, callback) {
                var toast = $mdToast.simple()
                    .theme("info2")
                    .textContent(msg)
                    .position('top right')
                    .hideDelay(0);
                // override template to add spinner gif
                toast._options.templateUrl = TEMPLATES_PATH + 'spinnerToast.html';

                if (withTransition === false) {
                    //toast._options.templateUrl = TEMPLATES_PATH + 'spinnerToast-no-transition.html';
                }
                delete toast._options.template;

                var promise = $mdToast.show(toast);
                if (callback) {
                    $log.log("promise then");
                    promise.then(callback);
                }

                return toast;
            },

            /**
             * display a success message
             * @param msg
             */
            displaySuccess : function(msg) {
                var toast = $mdToast.simple()
                    .theme("success2")
                    .textContent(msg)
                    .position('top right')
                    .hideDelay(toastHideDelay);

                toast._options.templateUrl = TEMPLATES_PATH + 'customToast.html';
                return $mdToast.show(toast);
            }
        };
       service.displayI18nError = function(category, error) {
            if (error && error.data && error.data.messageCode) {
                return service.displayError(translateFilter('link.messages.' + category + '.' + error.data.messageCode));
            } else {
                return service.displayError(translateFilter('link.messages.global.unknown'));
            }
       };
       service.displayI18nSuccess = function(category, error) {
            if (error && error.data && error.data.messageCode) {
                return service.displaySuccess(translateFilter('link.messages.' + category + '.' + error.data.messageCode));
            } else {
                return service.displaySuccess(translateFilter('link.messages.' + category + '.success'));
            }
        }

        return service;
    });
