#!/bin/bash

PGUSER=postgres PGHOST=localhost PGPASSWORD=postgres PGDATABASE=prod PGPORT=5432 node_modules/nightwatch/bin/nightwatch --env chrome
#PGUSER=postgres PGHOST=localhost PGPASSWORD=postgres PGDATABASE=prod PGPORT=5432 node_modules/nightwatch/bin/nightwatch src/test/nightwatch/test/login2.js --env chrome
#PGUSER=postgres PGHOST=localhost PGPASSWORD=postgres PGDATABASE=prod PGPORT=5432 node_modules/nightwatch/bin/nightwatch src/test/nightwatch/test/login.js --env chrome
#PGUSER=postgres PGHOST=localhost PGPASSWORD=postgres PGDATABASE=prod PGPORT=5432 node_modules/nightwatch/bin/nightwatch --env chrome


