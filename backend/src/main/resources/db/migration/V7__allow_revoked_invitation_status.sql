alter table project_invitations
drop constraint ck_invitations_status;

alter table project_invitations
add constraint ck_invitations_status check (
    status in (
        'PENDING',
        'ACCEPTED',
        'REVOKED',
        'EXPIRED'
    )
);