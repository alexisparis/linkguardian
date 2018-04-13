var chromedriver = require('chromedriver');
var pg = require('pg');

module.exports = {
    // dbUrl : dbUrl,
    mail : 'linkguardian@blackdog-project.org',
    login : 'testlinkguardian',
    password : 'aaaaa',
    new_password : 'bbbbb',
    before : function(done) {
        chromedriver.start();

        done();
    },

    after : function(done) {
        chromedriver.stop();

        // console.log("delete test user data...");
        // const exec = require('child_process').exec;
        // var purgeDb = exec('bash bin/delete_test_user.sh',
        //         function(error, stdout, stderr) {
        //         console.log(stdout);
        //     console.log(stderr);
        //     if (error !== null) {
        //         console.log("exec error: ${error}");
        //     }
        // });

        // remove trash data
        var client = new pg.Client();
        client.connect();

        var mailDomainUsedForTest = 'linkguardian@blackdog-project.org';

        client.query("DELETE FROM link_tag where links_id IN " +
            "(select id FROM link where user_id IN " +
            "(select id FROM jhi_user where email like '" + mailDomainUsedForTest + "'));", function (err, res) {
            console.log(err, res);
            client.query("DELETE FROM link where user_id IN " +
                "(select id FROM jhi_user where email like '" + mailDomainUsedForTest + "');", function (err, res) {
                console.log(err, res);
                client.query("DELETE FROM jhi_user_authority where user_id IN " +
                    "(select id FROM jhi_user where email like '" + mailDomainUsedForTest + "');", function (err, res) {
                    console.log(err, res);
                    client.query("DELETE FROM jhi_persistent_token where user_id IN (" +
                        "select id FROM jhi_user where email like '" + mailDomainUsedForTest + "');", function (err, res) {
                        console.log(err, res);
                        client.query("DELETE FROM jhi_user where email like '" + mailDomainUsedForTest + "';", function (err, res) {
                            console.log(err, res);
                            // todo : remove bookmark_batch_item and bookmark_batch
                            client.end();
                            done();
                        });
                    });

                });
            });
        });

        done();
    }
};

module.exports.assertion = function (selector, count) {
    this.message = 'Testing if element <' + selector + '> has count: ' + count;
    this.expected = count;
    this.pass = function (val) {
        return val === this.expected;
    }
    this.value = function (res) {
        return res.value;
    }
    this.command = function (cb) {
        var self = this;
        return this.api.execute(function (selector) {
            return document.querySelectorAll(selector).length;
        }, [selector], function (res) {
            cb.call(self, res);
        });
    }
}

