#!/bin/bash

dir="/home/linkguardian/"
#dir="/Users/para042/linkguardian/bin/remote/tmp/"

v=`ls -1q $dir/maintenance_on* 2>/dev/null | wc -l`
touch $dir/maintenance_on
chown linkguardian $dir/maintenance_on
# if more than 1 file, then create a lock
if [ "$v" -ge 1 ]; then
    echo "create lock"
    DATE=`date '+%Y-%m-%d_%H_%M_%S'`
    touch "$dir/maintenance_on_$DATE.lock"
    chown linkguardian "$dir/maintenance_on_$DATE.lock"
fi
v=`ls -1q $dir/maintenance_on_* 2>/dev/null | wc -l`
echo "maintenance on with $v locks"
