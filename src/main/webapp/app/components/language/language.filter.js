(function() {
    'use strict';

    angular
        .module('linkguardianApp')
        .filter('findLanguageFromKey', findLanguageFromKey)
        .filter('findLanguageRtlFromKey', findLanguageRtlFromKey);

    var languages = {
        'zh-cn': { name: '中文（简体）' },
        'en': { name: 'English' },
        'fr': { name: 'Français' },
        'de': { name: 'Deutsch' },
        'it': { name: 'Italiano' },
        'pt-pt': { name: 'Português' },
        'es': { name: 'Español' }
        // jhipster-needle-i18n-language-key-pipe - JHipster will add/remove languages in this object
    };

    function findLanguageFromKey() {
        return findLanguageFromKeyFilter;

        function findLanguageFromKeyFilter(lang) {
            return languages[lang].name;
        }
    }

    function findLanguageRtlFromKey() {
        return findLanguageRtlFromKeyFilter;

        function findLanguageRtlFromKeyFilter(lang) {
            return languages[lang].rtl;
        }
    }

})();
