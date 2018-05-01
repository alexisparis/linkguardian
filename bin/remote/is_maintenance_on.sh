#!/bin/bash

dir="/home/linkguardian/"
#dir="/Users/para042/linkguardian/bin/remote/tmp/"

if [ -f "$dir/maintenance_on" ]
then
    v=`ls -1q $dir/maintenance_on_* 2>/dev/null  | wc -l`
    echo "maintenance ON with $v locks"
else
    echo "maintenance OFF"
fi
