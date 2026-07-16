drop table line_version_workflow;
drop table line_version_snapshot;

drop sequence line_version_workflow_seq;
drop sequence line_version_snapshot_seq;

update line_version
set status = 'VALIDATED'
where status in ('IN_REVIEW', 'DRAFT');