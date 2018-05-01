#!/bin/bash

#
# run as root
#

# dropdb prod --username=postgres

file="/home/linkguardian/backup.sql"
if [ -f "$file" ]
then
    sudo -H -u linkguardian bash -c 'source ~/bin/maintenance_on.sh'
    sudo systemctl stop tomcat
    sudo -H -u linkguardian bash -c 'dropdb linkguardian'
    sudo -H -u linkguardian bash -c 'createdb linkguardian'
    sudo -H -u linkguardian bash -c "psql -f $file linkguardian"
    sudo -H -u linkguardian bash -c "source ~/bin/reset_sequences.sh"
    sudo systemctl start tomcat
    sudo -H -u linkguardian bash -c 'source ~/bin/maintenance_off.sh'

    echo ""
    echo "database linkguardian restored"
else
	echo "$file not found."
fi


