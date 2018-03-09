angular.module('linkguardianApp')
    .directive( 'updateLanguage',
        function updateLanguage( $rootScope ) {
            return {
                link: function( scope, element ) {
                    var listener = function( event, translationResp ) {
                        var defaultLang = "en",
                            currentlang = translationResp.language;

                        element.attr("lang", currentlang || defaultLang );
                    };

                    $rootScope.$on('$translateChangeSuccess', listener);
                }
            };
        });
