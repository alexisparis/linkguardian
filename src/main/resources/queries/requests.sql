
# drop spring-batch tables
DROP TABLE batch_job_execution CASCADE;
DROP TABLE batch_job_execution_context CASCADE;
DROP TABLE batch_job_execution_params CASCADE;
DROP TABLE batch_job_instance CASCADE;
DROP TABLE batch_step_execution CASCADE;
DROP TABLE batch_step_execution_context CASCADE;

DROP TABLE bookmark_batch CASCADE;
DROP TABLE bookmark_batch_item CASCADE;
DROP TABLE databasechangelog CASCADE;
DROP TABLE databasechangeloglock CASCADE;
DROP TABLE jhi_authority CASCADE;
DROP TABLE jhi_persistent_audit_event CASCADE;
DROP TABLE jhi_persistent_audit_evt_data CASCADE;
DROP TABLE jhi_persistent_token CASCADE;
DROP TABLE jhi_social_user_connection CASCADE;
DROP TABLE jhi_user CASCADE;
DROP TABLE jhi_user_authority CASCADE;
DROP TABLE link CASCADE;
DROP TABLE link_tag CASCADE;
DROP TABLE tag CASCADE;
DROP TABLE toxic_link CASCADE;

# view repartition of items per batch
select bookmark_batch_id, status, COUNT(*) from bookmark_batch_item
group by bookmark_batch_id, status

# change status of item
UPDATE bookmark_batch_item set STATUS = 'NOT_IMPORTED'

UPDATE bookmark_batch_item set STATUS = 'CREATED'


# delete links
delete from link where user_id = 1000 and creation_date > '2018-01-30 00:00:00.0'
