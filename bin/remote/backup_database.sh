#!/bin/bash

#
# run it as linkguardian
#
#if [ "$user" != "linkguardian" ]; then
#    echo "have to be linkguardian... use : sudo su - linkguardian"
#    exit 1
#fi

# first argument should be on to manage maintenance file flag
# second argument should be a suffix for the backup file
manage_maintenance=$1
suffix=$2

if [ "$manage_maintenance" == "on" ]; then
    sudo -H -u linkguardian bash -c 'source ~/bin/maintenance_on.sh'
fi
cd ~/Dropbox/backup
# only keep the 100 most recent backup
ls -tp | grep -v '/$' | tail -n +100 | xargs -d '\n' -r rm --
DATE=`date '+%Y-%m-%d_%H_%M_%S'`
echo 'backing up postgresql...'
pg_dump | gzip > "backup_linkguardian_${DATE}_${suffix}.sql.gz"
echo 'backup done'

source /home/linkguardian/bin/sync_dropbox.sh

if [ "$manage_maintenance" == "on" ]; then
    sudo -H -u linkguardian bash -c 'source ~/bin/maintenance_off.sh'
fi
