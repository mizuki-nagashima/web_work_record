# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table ms_employee (
  regist_user_id                varchar(255) not null,
  update_user_id                varchar(255),
  employee_no                   varchar(255) not null,
  employee_name                 varchar(255) not null,
  employee_name_kana            varchar(255) not null,
  position_code                 varchar(255) not null,
  department_code               varchar(255),
  division_code                 varchar(255),
  employment_class              varchar(255) not null,
  breakdown_name1               varchar(255),
  breakdown_name2               varchar(255),
  breakdown_name3               varchar(255),
  breakdown_name4               varchar(255),
  authority_class               varchar(255) not null,
  retirement_date               varchar(255),
  regist_date                   datetime(6) not null,
  update_date                   datetime(6) not null
);

create table ms_general_code (
  regist_user_id                varchar(255) not null,
  update_user_id                varchar(255),
  code_type                     varchar(255) not null,
  target_year                   varchar(255) not null,
  code                          varchar(255) not null,
  code_name                     varchar(255),
  any_value1                    varchar(255),
  any_value2                    varchar(255),
  any_value3                    varchar(255),
  any_value4                    varchar(255),
  any_value5                    varchar(255),
  regist_date                   datetime(6) not null,
  update_date                   datetime(6) not null
);

create table ms_general_code_admin (
  regist_user_id                varchar(255) not null,
  update_user_id                varchar(255),
  code_type                     varchar(255) not null,
  code_type_name                varchar(255) not null,
  note                          varchar(255),
  any_value_note1               varchar(255),
  any_value_note2               varchar(255),
  any_value_note3               varchar(255),
  any_value_note4               varchar(255),
  any_value_note5               varchar(255),
  regist_date                   datetime(6) not null,
  update_date                   datetime(6) not null
);

create table ms_performance_manage (
  regist_user_id                varchar(255) not null,
  update_user_id                varchar(255),
  start_date                    varchar(255) not null,
  end_date                      varchar(255) not null,
  employee_no                   varchar(255) not null,
  business_code                 varchar(255),
  business_team_code            varchar(255),
  business_manage_auth_class    varchar(255),
  regist_date                   datetime(6) not null,
  update_date                   datetime(6) not null
);

create table tbl_login_info (
  regist_user_id                varchar(255) not null,
  update_user_id                varchar(255),
  employee_no                   varchar(255) not null,
  password                      varchar(255) not null,
  login_count                   integer not null,
  login_ng_count                integer not null,
  is_account_lock               varchar(255) not null,
  is_delete                     varchar(255) not null,
  regist_date                   datetime(6) not null,
  update_date                   datetime(6) not null
);

create table tbl_performance (
  regist_user_id                varchar(255) not null,
  update_user_id                varchar(255),
  employee_no                   varchar(255) not null,
  months_years                  varchar(255) not null,
  performance_date              varchar(255) not null,
  opening_time                  varchar(255),
  closing_time                  varchar(255),
  breakdown1                    double,
  breakdown2                    double,
  breakdown3                    double,
  breakdown4                    double,
  performance_time              double,
  deduction_night               double,
  deduction_other               double,
  holiday_class                 varchar(255),
  shift_class                   varchar(255),
  remarks                       varchar(255),
  performance_status            varchar(255) not null,
  approval_employee_no          varchar(255),
  approval_date                 datetime(6),
  regist_date                   datetime(6) not null,
  update_date                   datetime(6) not null
);

create table tbl_year_month_attribute (
  regist_user_id                varchar(255) not null,
  update_user_id                varchar(255),
  employee_no                   varchar(255) not null,
  months_years                  varchar(255) not null,
  department_code               varchar(255),
  division_code                 varchar(255),
  business_code                 varchar(255) not null,
  business_team_code            varchar(255) not null,
  breakdown_name1               varchar(255),
  breakdown_name2               varchar(255),
  breakdown_name3               varchar(255),
  breakdown_name4               varchar(255),
  months_years_status           varchar(255) not null,
  regist_date                   datetime(6) not null,
  update_date                   datetime(6) not null
);


# --- !Downs

drop table if exists ms_employee;

drop table if exists ms_general_code;

drop table if exists ms_general_code_admin;

drop table if exists ms_performance_manage;

drop table if exists tbl_login_info;

drop table if exists tbl_performance;

drop table if exists tbl_year_month_attribute;

