(function() {
    'use strict';

    angular
        .module('linkguardianApp', [
            'ngStorage',
            'tmh.dynamicLocale',
            'pascalprecht.translate',
            'ngResource',
            'ngCookies',
            'ngAria',
            'ngCacheBuster',
            'ui.bootstrap',
            'ui.router',
            'infinite-scroll',
            'angular-loading-bar',
            'ngMaterial',
            'angular-jqcloud',
            'ngOnboarding',
            'angular-clipboard',
            'angularFileUpload',
            'ui.bootstrap.fontawesome'
            // jhipster-needle-angularjs-add-module JHipster will add new module here
        ])
        .config(function(ngOnboardingDefaultsProvider) {
            ngOnboardingDefaultsProvider.set({
                overlay: true,
                overlayOpacity: 0.2,
                showButtons: true,
                closeButtonText: '<md-icon md-svg-src="content/images/addfce46.ic_close_24px.svg" aria-label="Close dialog" class="ng-scope" role="img"><svg xmlns="http://www.w3.org/2000/svg" width="100%" height="100%" viewBox="0 0 24 24" fit="" preserveAspectRatio="xMidYMid meet" focusable="false"><path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"></path></svg></md-icon>'
            });
        })
        .config(function ($qProvider, $mdThemingProvider, $locationProvider) {
            // uncomment below to make alerts look like toast
            // AlertServiceProvider.showAsToast(true);
            $locationProvider.hashPrefix('');

            $qProvider.errorOnUnhandledRejections(false);

            // todo check if necessary
            $mdThemingProvider.theme('success2');
            $mdThemingProvider.theme('info2');
            $mdThemingProvider.theme('error2');
            $mdThemingProvider.theme("success");
            $mdThemingProvider.theme("error");
        })
        // Initialize material design
        .config(function () {
            $.material.init();

            angular.element(document).ready(function() {
                angular.element(".select").dropdown({"optionClass": "withripple"});
            });
        })
        .run(run)
    ;

    run.$inject = ['stateHandler', 'translationHandler'];

    function run(stateHandler, translationHandler) {
        stateHandler.initialize();
        translationHandler.initialize();
    }
})();
