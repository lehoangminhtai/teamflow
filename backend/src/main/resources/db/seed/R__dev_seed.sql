insert into users (email,password_hash, full_name) 
values ('tai@teamflow.dev', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J9zqvJk6i7kJqUvJk6i7kJqUvJk6i7', 'Le Hoang Minh Tai'),
		('name@teamflow.dev', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J9zqvJk6i7kJqUvJk6i7kJqUvJk6i7', 'Nguyen Van Name'),
		('lai@teamflow.dev', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J9zqvJk6i7kJqUvJk6i7kJqUvJk6i7', 'Tran Nguyen Thu Lai')
on conflict do nothing;

insert into projects (name, description, owner_id)
select 'Website Redesign', 'Remake home and product page', u.id from users u where u.email ='tai@teamflow.dev'
and not exists (select 1 from projects where name = 'Website Redesign');

insert into projects (name, description, owner_id)
select 'Mobile app v2', 'Version 2 for app', u.id from users u where u.email = 'lai@teamflow.dev'
and not exists (select 1 from projects where name = 'Mobile app v2');

insert into project_members (project_id, user_id, role)
select p.id, u.id, v.role from projects p 
cross join lateral(
	values ('tai@teamflow.dev', 'OWNER'),
			('name@teamflow.dev', 'MANAGER'),
			('lai@teamflow.dev', 'MEMBER')
) as v(email, role) join users u on u.email = v.email
where p.name = 'Website Redesign'
on conflict on constraint uq_members_project_user do nothing;

insert into tasks (project_id, title, description, status, priority, assignee_id, created_by, due_date)
select p.id, v.title, v.des, v.status, v.priority, u.id, o.id, v.due_date::date from projects p
cross join lateral (
	values ('Fix login','button not response on safari','IN_PROGRESS', 'HIGH','tai@teamflow.dev','2026-10-05'),
			('Design Home Page','Draft for new home page','TODO','LOW','lai@teamflow.dev','2026-10-20'),
			('Write API doc', 'Add description for the endpoints', 'TODO', 'LOW', null, null),
			('Optimize image loading','compress image to 200kb','DONE', 'MEDIUM','tai@teamflow.dev', '2026-09-10')
) as v(title, des, status, priority, assignee_email, due_date)
left join users u on u.email = v.assignee_email
join users o on o.email='tai@teamflow.dev'
where p.name = 'Website Redesign'
and not exists (select 1 from tasks t where t.title = v.title);