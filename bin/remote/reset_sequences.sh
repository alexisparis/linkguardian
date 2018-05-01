#!/bin/bash

user=`whoami`

if [ "$user" != "linkguardian" ]; then
    echo "have to be linkguardian... use : sudo su - linkguardian"
    exit 1
fi

psql -d linkguardian -c "SELECT pg_catalog.setval(pg_get_serial_sequence('public.jhi_user', 'id'), max(id)) FROM jhi_user;"
psql -d linkguardian -c "SELECT pg_catalog.setval(pg_get_serial_sequence('public.link', 'id'), max(id)) FROM public.link;"
psql -d linkguardian -c "SELECT pg_catalog.setval(pg_get_serial_sequence('public.tag', 'id'), max(id)) FROM tag;"
psql -d linkguardian -c "SELECT pg_catalog.setval(pg_get_serial_sequence('public.toxic_link', 'id'), max(id)) FROM toxic_link;"
psql -d linkguardian -c "SELECT pg_catalog.setval(pg_get_serial_sequence('public.jhi_social_user_connection', 'id'), max(id)) FROM jhi_social_user_connection;"
psql -d linkguardian -c "SELECT pg_catalog.setval(pg_get_serial_sequence('public.jhi_persistent_audit_event', 'event_id'), max(event_id)) FROM jhi_persistent_audit_event;"
psql -d linkguardian -c "SELECT pg_catalog.setval(pg_get_serial_sequence('public.bookmark_batch', 'id'), max(id)) FROM bookmark_batch;"
psql -d linkguardian -c "SELECT pg_catalog.setval(pg_get_serial_sequence('public.bookmark_batch_item', 'id'), max(id)) FROM bookmark_batch_item;"
psql -d linkguardian -c "SELECT pg_catalog.setval(pg_get_serial_sequence('public.jhi_persistent_audit_event', 'event_id'), max(event_id)) FROM jhi_persistent_audit_event;"
