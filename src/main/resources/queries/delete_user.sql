
DELETE FROM link_tag where links_id IN  (select id FROM link where user_id IN
(select id FROM jhi_user where email like 'alexis.paris@bnc.ca'));

DELETE FROM link where user_id IN
(select id FROM jhi_user where email like 'alexis.paris@bnc.ca');

DELETE FROM jhi_user_authority where user_id IN
(select id FROM jhi_user where email like 'alexis.paris@bnc.ca');

DELETE FROM jhi_persistent_token where user_id IN (
select id FROM jhi_user where email like 'alexis.paris@bnc.ca');

DELETE FROM bookmark_batch_item where bookmark_batch_id IN (
SELECT id FROM bookmark_batch where user_id IN (
select id FROM jhi_user where email like 'alexis.paris@bnc.ca')
);

DELETE FROM bookmark_batch where user_id IN (
select id FROM jhi_user where email like 'alexis.paris@bnc.ca');

DELETE FROM jhi_user where email like 'alexis.paris@bnc.ca';
