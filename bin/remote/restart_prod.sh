#!/bin/bash

source /home/linkguardian/bin/maintenance_on.sh

sudo systemctl stop linkguardian-prod

sleep 30

source /home/linkguardian/bin/maintenance_off.sh
