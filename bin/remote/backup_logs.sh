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
rm -Rf /home/linkguardian/Dropbox/logs/*
cp -R /home/linkguardian/apps/logs/* /home/linkguardian/Dropbox/logs
chown linkguardian /home/linkguardian/Dropbox/logs
chmod ugo+r /home/linkguardian/Dropbox/logs -R

#source /home/linkguardian/bin/sync_dropbox.sh
