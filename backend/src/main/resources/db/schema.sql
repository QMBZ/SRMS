-- 主 schema.sql 文件
RUNSCRIPT FROM 'classpath:/db/schema/01_roles.sql';

RUNSCRIPT FROM 'classpath:/db/schema/02_colleges.sql';

RUNSCRIPT FROM 'classpath:/db/schema/03_majors.sql';

RUNSCRIPT FROM 'classpath:/db/schema/04_classes.sql';

RUNSCRIPT FROM 'classpath:/db/schema/05_users.sql';

RUNSCRIPT FROM 'classpath:/db/schema/06_admin_college.sql';

RUNSCRIPT FROM 'classpath:/db/schema/07_student_info.sql';
