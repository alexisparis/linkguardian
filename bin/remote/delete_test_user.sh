#!/bin/bash

user=`whoami`

if [ "$user" != "linkguardian" ]; then
    echo "have to be linkguardian... use : sudo su - linkguardian"
    exit 1
fi

mail="linkguardian@blackdog-project.org"
psql -d linkguardian -c "DELETE FROM link_tag where links_id IN  (select id FROM link where user_id IN (select id FROM jhi_user where email like '$mail'));"
psql -d linkguardian -c "DELETE FROM link where user_id IN (select id FROM jhi_user where email like '$mail');"
psql -d linkguardian -c "DELETE FROM jhi_user_authority where user_id IN (select id FROM jhi_user where email like '$mail');"
psql -d linkguardian -c "DELETE FROM jhi_persistent_token where user_id IN (select id FROM jhi_user where email like '$mail');"
psql -d linkguardian -c "DELETE FROM jhi_user where email like '$mail';"
