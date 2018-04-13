module.exports = {
    '@disabled': false,
    'demo works' : function (browser) {
        browser
            .url(browser.launchUrl);

        browser.waitForElementPresent('#explanation', 10000);
        browser.click("a[translate=\"login.demo.button\"]");

        var steps = [
            "welcome",
            "tools",
            "show-add-url-input",
            "show-add-url-tag-input",
            "show-add-url-button",
            "link-added",
            "action-button-on-link",
            "note-button-on-link",
            "show-search-panel",
            "show-cloud-of-tags",
            "end"
        ];

        // forward
        var index = 0;
        do {
            browser.waitForElementVisible(".demo-step-" + steps[index], 10000);
            browser.getText(".onboarding-popover-content p", function (result) {
                this.assert.ok(result.value.length > 0, "check that the content of the onboarding step is available when forward");
            });
            browser.assert.containsText(".onboarding-step-info", "Step " + (index + 1) + " of " + steps.length);
            browser.assert.urlEquals(browser.launchUrl + "/#/demo");

            browser.click("a[ng-click=\"next()\"]");
            index++;
        } while (index < steps.length);

        // back
        do {
            browser.click("a[ng-click=\"previous()\"]");
            index--;

            browser.waitForElementVisible(".demo-step-" + steps[index - 1], 10000);
            browser.getText(".onboarding-popover-content p", function (result) {
                this.assert.ok(result.value.length > 0, "check that the content of the onboarding step is available when backward");
            });
            browser.assert.containsText(".onboarding-step-info", "Step " + (index) + " of " + steps.length);
            browser.assert.urlEquals(browser.launchUrl + "/#/demo");

        } while (index > 1);

        // to the end, back to login
        index--;
        do {
            browser.waitForElementVisible(".demo-step-" + steps[index], 10000);
            browser.getText(".onboarding-popover-content p", function (result) {
                this.assert.ok(result.value.length > 0, "check that the content of the onboarding step is available when forward");
            });
            browser.assert.urlEquals(browser.launchUrl + "/#/demo");
            browser.assert.containsText(".onboarding-step-info", "Step " + (index + 1) + " of " + steps.length);

            browser.click("a[ng-click=\"next()\"]");
            index++;
        } while (index < steps.length);

        browser.click("a[ng-click=\"close()\"]");

        browser.waitForElementVisible('#explanation', 10000);

        browser.assert.urlEquals(browser.launchUrl + "/#/login");

        browser.end();
    }
};
