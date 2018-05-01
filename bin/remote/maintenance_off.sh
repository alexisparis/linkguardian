#!/bin/bash

# use $1 with force to force maintenance off

dir="/home/linkguardian/"
#dir="/Users/para042/linkguardian/bin/remote/tmp/"

if [ "$1" == "force" ]; then
    rm -f $dir/maintenance_on*
    echo "maintenance off"
else
    # count locks, if there are some existing, then remove the latest on
    v=`ls -1q $dir/maintenance_on_* 2>/dev/null | wc -l`
    # if more than 1 file, then create a lock
    if [ "$v" -gt 0 ]; then
        echo "remove one lock"
        ls -tp $dir/maintenance_on_* 2>/dev/null | grep -v '/$' | head -1 | xargs -d '\n' -r rm --

        v=`ls -1q $dir/maintenance_on_* 2>/dev/null | wc -l`
        echo "maintenance STILL on with $v locks"
    else
        rm -f $dir/maintenance_on
        echo "maintenance off"
    fi
fi
