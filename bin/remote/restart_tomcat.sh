#!/bin/bash

source /home/linkguardian/bin/maintenance_on.sh

sudo service tomcat restart

sleep 40

source /home/linkguardian/bin/maintenance_off.sh
