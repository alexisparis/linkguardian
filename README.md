# linkguardian
This application was generated using JHipster 4.14.1, you can find documentation and help at [http://www.jhipster.tech/documentation-archive/v4.14.1](http://www.jhipster.tech/documentation-archive/v4.14.1).

## Development

Before you can build this project, you must install and configure the following dependencies on your machine:

1. [Node.js][]: We use Node to run a development web server and build the project.
   Depending on your system, you can install Node either from source or as a pre-packaged bundle.
2. [Yarn][]: We use Yarn to manage Node dependencies.
   Depending on your system, you can install Yarn either from source or as a pre-packaged bundle.

After installing Node, you should be able to run the following command to install development tools.
You will only need to run this command when dependencies change in [package.json](package.json).

    yarn install
    
to build :

    ./gradlew build

We use [Gulp][] as our build system. Install the Gulp command-line tool globally with:

    yarn global add gulp-cli

Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

    ./gradlew
    gulp

[Bower][] is used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in [bower.json](bower.json). You can also run `bower update` and `bower install` to manage dependencies.
Add the `-h` flag on any command to see how you can use it. For example, `bower update -h`.

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].



## Building for production

To optimize the linkguardian application for production, run:

    ./gradlew -Pprod clean bootRepackage

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references these new files.
To ensure everything worked, run:

    java -jar build/libs/*.war

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

Refer to [Using JHipster in production][] for more details.

## Testing

To launch your application's tests, run:

    ./gradlew test
    
## Releasing

    ./gradlew release 

### Client tests

Unit tests are run by [Karma][] and written with [Jasmine][]. They're located in [src/test/javascript/](src/test/javascript/) and can be run with:

    gulp test

UI end-to-end tests are powered by [Protractor][], which is built on top of WebDriverJS. They're located in [src/test/javascript/e2e](src/test/javascript/e2e)
and can be run by starting Spring Boot in one terminal (`./gradlew bootRun`) and running the tests (`gulp itest`) in a second one.
### Other tests

Performance tests are run by [Gatling][] and written in Scala. They're located in [src/test/gatling](src/test/gatling) and can be run with:

    ./gradlew gatlingRun

For more information, refer to the [Running tests page][].

## Using Docker to simplify development (optional)

You can use Docker to improve your JHipster development experience. A number of docker-compose configuration are available in the [src/main/docker](src/main/docker) folder to launch required third party services.

For example, to start a postgresql database in a docker container, run:

    docker-compose -f src/main/docker/postgresql.yml up -d

To stop it and remove the container, run:

    docker-compose -f src/main/docker/postgresql.yml down

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

    ./gradlew bootRepackage -Pprod buildDocker

Then run:

    docker-compose -f src/main/docker/app.yml up -d

For more information refer to [Using Docker and Docker-Compose][], this page also contains information on the docker-compose sub-generator (`jhipster docker-compose`), which is able to generate docker configurations for one or several JHipster applications.

## Continuous Integration (optional)

To configure CI for your project, run the ci-cd sub-generator (`jhipster ci-cd`), this will let you generate configuration files for a number of Continuous Integration systems. Consult the [Setting up Continuous Integration][] page for more information.

[JHipster Homepage and latest documentation]: http://www.jhipster.tech
[JHipster 4.14.1 archive]: http://www.jhipster.tech/documentation-archive/v4.14.1

[Using JHipster in development]: http://www.jhipster.tech/documentation-archive/v4.14.1/development/
[Service Discovery and Configuration with the JHipster-Registry]: http://www.jhipster.tech/documentation-archive/v4.14.1/microservices-architecture/#jhipster-registry
[Using Docker and Docker-Compose]: http://www.jhipster.tech/documentation-archive/v4.14.1/docker-compose
[Using JHipster in production]: http://www.jhipster.tech/documentation-archive/v4.14.1/production/
[Running tests page]: http://www.jhipster.tech/documentation-archive/v4.14.1/running-tests/
[Setting up Continuous Integration]: http://www.jhipster.tech/documentation-archive/v4.14.1/setting-up-ci/

[Gatling]: http://gatling.io/
[Node.js]: https://nodejs.org/
[Yarn]: https://yarnpkg.org/
[Bower]: http://bower.io/
[Gulp]: http://gulpjs.com/
[BrowserSync]: http://www.browsersync.io/
[Karma]: http://karma-runner.github.io/
[Jasmine]: http://jasmine.github.io/2.0/introduction.html
[Protractor]: https://angular.github.io/protractor/
[Leaflet]: http://leafletjs.com/
[DefinitelyTyped]: http://definitelytyped.org/

## run jacoco coverage report
    ./gradlew jacocoTestReport

    report is in build/reports/jacoco/test/html/index.html
    

## run locally
    ./gradlew bootRun
    
## build 
    ./gradlew clean build

## bootstrap-material
    how to change color on bootstrap-material : 
    edit
    
    bower_components/bootstrap-material-design/less/_variables.less
    
    and set one of the color defined in
    
    bower_components/bootstrap-material-design/less/_colors.less
    
    then, 
    
    npm install
    
    and then
    
    grunt serve --force
    
    all files in dist folders has been updated


## what to do if intellij has an infinite indexing while opening project?
try File => invalidate caches / Restart

if it does not work....
delete folders bower_components and node_modules

try to add :
- release
- node_modules
- src/main/webapp/bower_components
as excluded path in the configuration of the intellij project

## usefull links
### design, layout, icons
[ion icons]: http://ionicons.com/
[ion icons]: https://github.com/driftyco/ionicons
[color palette]: http://www.dtelepathy.com/blog/inspiration/beautiful-color-palettes-for-your-next-web-project
[material layout]: https://material.angularjs.org/latest/layout/options

### masonry

[masonry]: https://github.com/passy/angular-masonry
[dynamic layout]: https://www.npmjs.com/package/angular-dynamic-layout
[isotope modulo]: http://michieldewit.github.io/isotope-modulo-columns/
[isotope]: http://mankindsoftware.github.io/angular-isotope/
[angular isotope]: https://github.com/mankindsoftware/angular-isotope
[search isotope]: https://www.google.fr/search?q=angular+isotope+modulo&oq=angular+isotope+modulo&aqs=chrome..69i57.4807j0j7&sourceid=chrome&es_sm=91&ie=UTF-8
[on boarding]: https://github.com/adamalbrecht/ngOnboarding

### cloud of tags
[jQCloud]: https://github.com/mistic100/jQCloud

### input starts
[stars]: http://ngmodules.org/modules/angular-input-stars

## backups db
Autopostgresqlbackup is a shell script (usually executed from a cron job) designed to provide a fully automated tool to make periodic backups of PostgreSQL databases.

On Debian systems, autopostgresqlbackup can be configured by editing some options in file /etc/default/autopostresqlbackup

Install Autopostgresqlbackup on ubuntu

Open the terminal and run the following command

sudo apt-get install autopostgresqlbackup

The above command will complete the installation process

Using autopostresqlbackup

You need to run the following command to run the backup

sudo autopostgresqlbackup

you have the backups in /var/lib/autopostgresqlbackup organized by daily/weekly/monthly folders then databasename folder.

If you need to configure parameters such as host, restricted databases to backup, compression type, etc…, autopostgresqlbackup file in /etc/default/ is what you are looking for

sudo vi /etc/default/autopostgresqlbackup

Ubuntu installs a cron script with this program that will run it every day. It will organize the files to the appropriate directory.

## star module 
taken from http://www.angulartutorial.net/2014/03/rating-stars-in-angular-js-using.html

## linkguardian's icon
taken from http://www.flaticon.com/free-icon/security-shield_63801

## UI theme
taken from http://www.dtelepathy.com/blog/inspiration/beautiful-color-palettes-for-your-next-web-project
theme 10 : Our Little Projects

## angular dynamic layout
about fixing the height of the parent container : 
https://github.com/tristanguigue/angular-dynamic-layout/issues/24

about problem of performance : 
avoid ng-include in ng-repeat : 
http://www.bennadel.com/blog/2738-using-ngrepeat-with-nginclude-hurts-performance-in-angularjs.htm

## if ECONFLICT Unable to find suitable version for messageformat
try to install it manually :
    bower install messageformat --save
    
## bower lock
https://www.npmjs.com/package/bower-locker
bower-locker lock
bower-locker unlock

## npm lock
use :
    npm shrinkwrap
    
## nginx :
    conf dans /etc/nginx/sites-available/default 

## add a new entity
    jhipster entity author
    
    this will add a new liquibase change, adapt them before apply

## liquibase
http://www.jhipster.tech/v2-documentation/development/
http://www.liquibase.org/documentation/command_line.html

en cas de probleme de checksum :
    2018-02-05 22:10:00.451 ERROR 13745 --- [dian-Executor-1] o.b.l.c.liquibase.AsyncSpringLiquibase   : Liquibase could not start correctly, your database is NOT ready: Validation Failed:
         1 change sets check sum
              classpath:config/liquibase/changelog/00000000000000_initial_schema.xml::00000000000001::jhipster was: 7:a486c0c318ff0401b0ca1bcb5b5811bc but is now: 7:0a5a0ee7e081aa01dc7c5ea0792565ef
    
    
    liquibase.exception.ValidationFailedException: Validation Failed:
         1 change sets check sum
              classpath:config/liquibase/changelog/00000000000000_initial_schema.xml::00000000000001::jhipster was: 7:a486c0c318ff0401b0ca1bcb5b5811bc but is now: 7:0a5a0ee7e081aa01dc7c5ea0792565ef
    
        at liquibase.changelog.DatabaseChangeLog.validate(DatabaseChangeLog.java:266)
        at liquibase.Liquibase.update(Liquibase.java:210)
        at liquibase.Liquibase.update(Liquibase.java:192)
        at liquibase.integration.spring.SpringLiquibase.performUpdate(SpringLiquibase.java:431)
        at liquibase.integration.spring.SpringLiquibase.afterPropertiesSet(SpringLiquibase.java:388)
        at org.blackdog.linkguardian.config.liquibase.AsyncSpringLiquibase.initDb(AsyncSpringLiquibase.java:63)
        at org.blackdog.linkguardian.config.liquibase.AsyncSpringLiquibase.lambda$afterPropertiesSet$0(AsyncSpringLiquibase.java:49)
        at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
        at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
        at java.lang.Thread.run(Thread.java:745)
lancer mvn liquibase:clearCheckSums

    ./gradlew liquibaseDiffChangeLog

## ssh config
install cerbot in the host :
    wget https://dl.eff.org/certbot-auto
    chmod a+x certbot-auto
    
sudo certbot-auto certonly --webroot -w /opt/tomcat8/webapps/ -d linkguardian.io -d www.linkguardian.io    
    
## back up db
    pg_dump linkguardian | gzip > linkguardian.gz

## restore
    psql -f infile postgres
    
    locally : 
        psql -U postgres -f backup_linkguardian_2018-04-26_02_00_01_daily.sql prod2
    
    for gcp
    
    psql -h host -p port -d database -U user -W -f backup_linkguardian_2018-02-15_02_00_01_daily.sql
    
## launch nightwatch tests
    node_modules/nightwatch/bin/nightwatch
    
## launch nightwatch for a given environment
    node_modules/nightwatch/bin/nightwatch --env local
    
## launch a single test on nightwatch for a given environment
    node_modules/nightwatch/bin/nightwatch src/test/nightwatch/test/demo.js --env local
    node_modules/nightwatch/bin/nightwatch src/test/nightwatch/test/login.js --env local

