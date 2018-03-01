var pg = require('pg')
module.exports = {
    '@disabled': false,
    '1 login form ok': function (browser) {
        browser
            .url(browser.launchUrl);

        browser.waitForElementVisible('#explanation', 4000);

        // description is available
        browser.expect.element("#explanation").to.be.visible;

        // demo button is available
        browser.expect.element("a[href=\"#/demo\"]").to.be.present.before(1000);

        // there is a title
        browser.expect.element(".signin-title").to.be.visible;

        // login form is displayed
        browser.expect.element("#username").to.be.visible;
        browser.expect.element("#password").to.be.visible;
        browser.expect.element("#rememberMe").to.be.visible;
        browser.expect.element("button[type=\"submit\"]").to.be.present.before(1000);

        // and reset / register links are available
        browser.expect.element(".register-button").to.be.present.before(1000);
        browser.expect.element(".reset-password-button").to.be.present.before(1000);

        browser.end();
    },

    '2 forgot password form ok': function (browser) {
        browser
            .url(browser.launchUrl);

        browser.waitForElementVisible(".reset-password-button", 4000);
        browser.click(".reset-password-button");

        browser.assert.urlEquals(browser.launchUrl + "/#/reset/request");

        browser.waitForElementVisible("h1[translate=\"reset.request.title\"]", 4000);

        browser.expect.element("h1[translate=\"reset.request.title\"]").to.be.visible;

        browser.expect.element("#email").to.be.visible;

        // back available
        browser.expect.element("a[href=\"#/login\"]").to.be.present.before(1000);
        // and
        browser.expect.element("button[type=\"submit\"]").to.be.present.before(1000);
        browser.expect.element('button[type=\"submit\"]').to.be.not.enabled;

        // click on back return to login
        browser.execute(function () {
            document.querySelector('a[href=\"#/login\"]').click()
        });

        browser.waitForElementPresent('#explanation', 6000);

        browser.assert.urlEquals(browser.launchUrl + "/#/login");

        // okay, return to forgot password
        browser.click(".reset-password-button");

        browser.setValue('#email', 'nightwatch');

        browser.waitForElementVisible('p[translate=\"global.messages.validate.email.invalid\"]', 3000);

        browser.setValue('#email', 'alexis.rt.paris@gmail.com');

        browser.waitForElementNotVisible('p[translate=\"global.messages.validate.email.invalid\"]', 3000);

        browser.expect.element('button[type=\"submit\"]').to.be.enabled;

        browser.end();
    },

    '3 register form ok and register': function (browser) {
        browser
            .url(browser.launchUrl);

        browser.waitForElementVisible('#explanation', 4000);

        browser.click(".register-button");

        browser.waitForElementVisible("h1[translate=\"register.title\"]", 4000);
        browser.waitForElementVisible("#email", 3000);
        browser.waitForElementVisible("#login", 3000);
        browser.waitForElementVisible("#password", 3000);
        browser.waitForElementVisible("#confirmPassword", 3000);

        // cancel available
        browser.expect.element("a[href=\"#/login\"]").to.be.present.before(1000);
        // and
        browser.expect.element("button[type=\"submit\"]").to.be.present.before(1000);
        browser.expect.element('button[type=\"submit\"]').to.be.not.enabled;

        // click on back return to login
        browser.execute(function () {
            document.querySelector('a[href=\"#/login\"]').click()
        });

        browser.waitForElementPresent('#explanation', 6000);

        browser.assert.urlEquals(browser.launchUrl + "/#/login");

        // go to register again
        browser.click(".register-button");

        browser.setValue('#email', 'nightwatch');
        browser.waitForElementVisible('p[translate=\"global.messages.validate.email.invalid\"]', 3000);
        browser.clearValue('#email');
        browser.setValue('#email', browser.globals.mail);
        browser.waitForElementNotVisible('p[translate=\"global.messages.validate.email.invalid\"]', 3000);
        // and
        browser.expect.element('button[type=\"submit\"]').to.be.not.enabled;

        browser.setValue('#login', 'a');
        browser.clearValue('#login');
        browser.waitForElementVisible('p[translate=\"register.messages.validate.login.required\"]', 3000);
        browser.setValue('#login', browser.globals.login);
        browser.waitForElementNotVisible('p[translate=\"register.messages.validate.login.required\"]', 3000);
        // and
        browser.expect.element('button[type=\"submit\"]').to.be.not.enabled;

        browser.setValue('#password', 'aaa');
        browser.waitForElementVisible('p[translate=\"global.messages.validate.newpassword.minlength\"]', 3000);
        browser.clearValue('#password');
        browser.setValue('#password', browser.globals.password);
        browser.waitForElementNotVisible('p[translate=\"global.messages.validate.newpassword.minlength\"]', 3000);
        // and
        browser.expect.element('button[type=\"submit\"]').to.be.not.enabled;

        browser.setValue('#confirmPassword', 'bbb');
        browser.waitForElementVisible('p[translate=\"global.messages.validate.confirmpassword.minlength\"]', 3000);
        browser.clearValue('#confirmPassword');
        browser.setValue('#confirmPassword', 'aaaab');
        browser.waitForElementNotVisible('p[translate=\"global.messages.validate.confirmpassword.minlength\"]', 3000);
        // and
        browser.expect.element("button[type=\"submit\"]").to.be.present.before(1000);
        browser.expect.element('button[type=\"submit\"]').to.be.enabled;

        browser.click('button[type=\"submit\"]');

        browser.waitForElementVisible('div[translate=\"global.messages.error.dontmatch\"]', 3000);

        browser.clearValue('#confirmPassword');
        browser.setValue('#confirmPassword', browser.globals.password);

        // click on back return to login
        // browser.click('#register-user-button');
        browser.execute(function () {
            document.querySelector('#register-user-button').click()
        });


        // browser.saveScreenshot("test.png");

        // success
        browser.waitForElementVisible('div[translate=\"register.messages.success\"]', 10000);

        var activation_key = "61102073510127508723";

        // ok, now the new user is registered, normally, user must wait for the email to get the activation link
        // access the database and get the activation key
        // browser.perform(function (self, done) {
        //
        //     var client = new pg.Client();
        //     client.connect();
        //
        //     client.query("SELECT activation_key FROM jhi_user where email like '" + browser.globals.mail + "';",
        //         function (err, res) {
        //             console.log("res", res);
        //             var activation_key = res.rows[0].activation_key;
        //             console.log("activation_key : " + activation_key);
        //             client.end();
        //             console.log("client end()");
        //
        //             console.log("before pause 1");
        //             // try with a wrong activation key
        //             browser.pause( 1000 );
        //             console.log("after pause 1");
        //             browser.url(browser.launchUrl + '/#/activate?key=aaa');// + activation_key + '_bad');
        //             // browser.pause( 1000 );
        //             // browser.waitForElementPresent('h1[translate=\"activate.title\"]', 10000);
        //
        //             console.log("before pause 2");
        //             // browser.pause( 1000 );
        //             console.log("after pause 2");
        //             // browser.saveScreenshot("test.png");
        //
        //             // browser.waitForElementPresent('[translate=\"activate.messages.error\"]', 10000);
        //             browser.waitForElementPresent('#activate-message-error', 5000);
        //
        //             browser.saveScreenshot("test.png");
        //             browser.pause( 1000 );
        //             // activate user
        //             browser.url(browser.launchUrl + '/#/activate?key=' + activation_key);
        //             // browser.saveScreenshot("test.png");
        //             // browser.pause( 1000 );
        //
        //             done();
        //         }
        //     );
        // });

        // XXAP

        browser.url(browser.launchUrl + '/#/activate?key=' + activation_key + '_bad');
        // browser.pause( 1000 );
        // browser.waitForElementPresent('h1[translate=\"activate.title\"]', 10000);

        console.log("before pause 2");
        // browser.pause( 1000 );
        console.log("after pause 2");
        // browser.saveScreenshot("test.png");

        browser.waitForElementPresent('div[translate=\"activate.messages.error\"]', 10000);
        // browser.waitForElementPresent('#activate-message-error', 5000);

        browser.saveScreenshot("test.png");
        browser.pause( 1000 );
        // activate user
        browser.url(browser.launchUrl + '/#/activate?key=' + activation_key);
        // XXAP

        browser.waitForElementPresent('[translate=\"activate.messages.success\"]', 5000);
        browser.waitForElementPresent('a[href="#/login"]', 5000);

        // click on login link
        browser.execute(function () {
            document.querySelector('a[href=\"#/login\"]').click()
        });

        browser.waitForElementPresent('#explanation', 6000);

        browser.assert.urlEquals(browser.launchUrl + "/#/login");

        // insert mail and password
        browser.expect.element("#username").to.be.visible;
        browser.expect.element("#password").to.be.visible;
        browser.setValue('#username', browser.globals.mail);
        browser.setValue('#password', browser.globals.password);

        browser.execute(function () {
            document.querySelector('button[type="submit"]').click()
        });

        browser.waitForElementVisible('.panel-add', 5000);
        browser.waitForElementVisible('.panel-search', 5000);

        // logout
        browser.execute(function () {
            document.querySelector('#account-menu').click()
        });
        browser.execute(function () {
            document.querySelector('#logout').click()
        });

        browser.waitForElementPresent('#explanation', 6000);

        browser.assert.urlEquals(browser.launchUrl + "/#/login");

        browser.end();
    },

    '4 password forgotten': function (browser) {
        browser
            .url(browser.launchUrl);

        browser.waitForElementVisible('#explanation', 4000);

        browser.execute(function () {
            document.querySelector('a[href=\"#/reset/request\"]').click()
        });

        browser.assert.urlEquals(browser.launchUrl + "/#/reset/request");

        browser.waitForElementVisible('p[translate=\"reset.request.messages.info\"]', 5000);

        // click on back
        browser.execute(function () {
            document.querySelector('a[href=\"#/login\"]').click()
        });
        browser.assert.urlEquals(browser.launchUrl + "/#/login");

        browser.execute(function () {
            document.querySelector('a[href=\"#/reset/request\"]').click()
        });

        browser.assert.urlEquals(browser.launchUrl + "/#/reset/request");

        browser.waitForElementVisible('p[translate=\"reset.request.messages.info\"]', 5000);

        browser.expect.element('button[type=\"submit\"]').to.be.not.enabled;

        browser.setValue('#email', browser.globals.mail);

        browser.expect.element('button[type=\"submit\"]').to.be.enabled;
        browser.execute(function () {
            document.querySelector('button[type="submit"]').click();
        });

        // browser.saveScreenshot("test.png");

        // success
        browser.waitForElementVisible('p[translate=\"reset.request.messages.success\"]', 10000);

        // ok, now the new user is registered, normally, user must wait for the email to get the activation link
        // access the database and get the activation key
        browser.perform(function (self, done) {

            var client = new pg.Client();
            client.connect();

            client.query("SELECT reset_key FROM jhi_user where email like '" + browser.globals.mail + "';",
                function (err, res) {
                    console.log("res", res);
                    var reset_key = res.rows[0].reset_key;
                    console.log("activation_key : " + reset_key);
                    client.end();

                    // try with a wrong activation key
                    browser.url(browser.launchUrl + '/#/reset/finish?key=' + reset_key);
                    done();
                }
            );
        });

        browser.waitForElementVisible('p[translate=\"reset.finish.messages.info\"]', 5000);

        browser.expect.element("#password").to.be.visible;
        browser.expect.element("#confirmPassword").to.be.visible;
        browser.setValue('#password', browser.globals.new_password);
        browser.setValue('#confirmPassword', browser.globals.new_password);

        browser.execute(function () {
            document.querySelector('button[type="submit"]').click()
        });

        browser.waitForElementVisible('p[translate=\"reset.finish.messages.success\"]', 5000);

        // go to login
        browser.execute(function () {
            document.querySelector('a[href=\"#/login\"]').click()
        });

        browser.waitForElementVisible('#explanation', 4000);

        browser.expect.element("#username").to.be.visible;
        browser.expect.element("#password").to.be.visible;
        browser.setValue('#username', browser.globals.mail);
        browser.setValue('#password', browser.globals.new_password);

        browser.execute(function () {
            document.querySelector('button[type="submit"]').click()
        });

        browser.waitForElementVisible('.panel-add', 5000);
        browser.waitForElementVisible('.panel-search', 5000);

        // logout
        browser.execute(function () {
            document.querySelector('#account-menu').click()
        });
        browser.execute(function () {
            document.querySelector('#logout').click()
        });

        browser.end();
    },

    '5 password change': function (browser) {
        browser
            .url(browser.launchUrl);

        browser.waitForElementVisible('#explanation', 4000);

        browser.setValue('#username', browser.globals.mail);
        browser.setValue('#password', browser.globals.new_password);

        browser.execute(function () {
            document.querySelector('button[type="submit"]').click()
        });

        browser.waitForElementVisible('.panel-add', 5000);

        // change password
        browser.execute(function () {
            document.querySelector('#account-menu').click()
        });
        browser.execute(function () {
            document.querySelector('a[href=\"#/password\"]').click()
        });

        browser.waitForElementVisible('h2[translate=\"password.title\"]', 15000);

        browser.expect.element('button[type=\"submit\"]').to.be.not.enabled;

        browser.expect.element("#password").to.be.visible;
        browser.expect.element("#confirmPassword").to.be.visible;
        browser.setValue('#password', browser.globals.dbUrl);
        browser.setValue('#confirmPassword', browser.globals.password);

        browser.execute(function () {
            document.querySelector('button[type="submit"]').click()
        });

        browser.waitForElementVisible('div[translate=\"global.messages.error.dontmatch\"]', 5000);

        browser.clearValue('#password');
        browser.setValue('#password', browser.globals.password);

        browser.expect.element('button[type=\"submit\"]').to.be.enabled;

        browser.execute(function () {
            document.querySelector('button[type="submit"]').click()
        });

        browser.waitForElementVisible('div[translate=\"password.messages.success\"]', 5000);

        // go to login
        browser.execute(function () {
            document.querySelector('a[href=\"#/login\"]').click()
        });

        browser.waitForElementVisible('.panel-add', 5000);
        browser.waitForElementVisible('.panel-search', 5000);

        // logout
        browser.execute(function () {
            document.querySelector('#account-menu').click()
        });
        browser.execute(function () {
            document.querySelector('#logout').click()
        });

        browser.end();
    },

    '5 add link': function (browser) {
        browser
            .url(browser.launchUrl);

        browser.waitForElementVisible('#explanation', 4000);

        browser.setValue('#username', browser.globals.mail);
        browser.setValue('#password', browser.globals.password);

        browser.execute(function () {
            document.querySelector('button[type="submit"]').click()
        });

        browser.waitForElementVisible('.panel-add', 5000);

        // add a link
        browser.setValue('#urlInput', "lequipe.fr");
        browser.setValue('#tagInput', "sport live");

        browser.execute(function () {
            document.querySelector('#submitUrlButton').click()
        });

        browser.waitForElementVisible('md-card.link', 4000);

        // logout
        browser.execute(function () {
            document.querySelector('#account-menu').click()
        });
        browser.execute(function () {
            document.querySelector('#logout').click()
        });

        browser.end();
    }
};
