#!/bin/bash

#
# run as root
#

echo 'backing up postgresql...'
sudo -H -u linkguardian bash -c 'pg_dump > ~/backup_linkguardian_temp.sql'
echo 'backing up done'

echo 'drop integration database...'
sudo -H -u linkguardian bash -c 'dropdb linkguardian-integration'
echo 'integration database dropped'

echo 'creating integration database...'
sudo -H -u linkguardian bash -c 'createdb linkguardian-integration'
echo 'integration database created...'

echo 'restoring integration database from production backup...'
sudo -H -u linkguardian bash -c "psql -f ~/backup_linkguardian_temp.sql linkguardian-integration"
echo 'integration database from production backup restored...'

echo 'delete backup file...'
sudo -H -u linkguardian bash -c 'rm -f ~/backup_linkguardian_temp.sql'
echo 'backup file deleted...'

