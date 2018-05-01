#!/bin/bash

#
# run it as linkguardian
#
#if [ "$user" != "linkguardian" ]; then
#    echo "have to be linkguardian... use : sudo su - linkguardian"
#    exit 1
#fi

cd ~/Dropbox/backup

# sync dropbox
~/bin/dropbox.py start

OUTPUT="$(~/bin/dropbox.py status)"
echo 'Dropbox synchronization launched...'
echo "output : ${OUTPUT}"

while [ "$OUTPUT" != "Up to date" ]
do
OUTPUT="$(~/bin/dropbox.py status)"
echo "output : ${OUTPUT}"
sleep 1
echo 'waiting for Dropbox to synchronize...'
done

echo 'Dropbox synchronized'

~/bin/dropbox.py stop
echo ''

echo 'dropbox status : '
~/bin/dropbox.py status
