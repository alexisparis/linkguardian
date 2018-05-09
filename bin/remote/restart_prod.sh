#!/bin/bash

source /home/linkguardian/bin/maintenance_on.sh

sudo systemctl restart linkguardian-prod

sleep 30

source /home/linkguardian/bin/maintenance_off.sh
