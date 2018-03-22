module.exports = {
    '@disabled': false,
    'show about dialog' : function (browser) {
        browser
            .url(browser.launchUrl);

        // click on about
        browser.waitForElementPresent("a[ng-click=\"vm.showAboutDialog()\"]", 10000);

        browser.execute(function () {
            document.querySelector("a[ng-click=\"vm.showAboutDialog()\"]").click()
        });
        browser.waitForElementVisible('.about-container', 10000);
        browser.expect.element(".about-container").to.be.visible;
        browser.expect.element("md-dialog-content a").text.to.equal('linkguardian@blackdog-project.org');

        // close
        browser.click("button[ng-click=\"cancel()\"]");

        browser.waitForElementNotPresent('.md-dialog-container"', 10000);

        browser.end();
    }
};
