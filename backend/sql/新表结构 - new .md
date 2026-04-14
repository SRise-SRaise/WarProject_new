本次修订在原 23 张表基础上，针对作业模块和考试模块增加了以下内容：

- **新增表**：`rel_exercise_class`（练习-班级关联表），支撑教师将作业分配给指定班级
- **新增表**：`res_exam_summary`（考试成绩汇总表），支撑考试成绩清单与统计分析
- **edu_exercise 新增字段**：`teacher_id`（创建教师）、`publish_status`（发布状态）
- **edu_exercise_item 新增字段**：`max_score`（题目分值）、`question_bank_id`（题库来源追溯）
- **res_exercise_record 新增字段**：`exercise_id`（冗余加速按作业维度查询成绩），新增联合唯一索引 `(student_id, item_id)`
- **edu_exam 新增字段**：`paper_id`（绑定试卷）
- **rel_paper_question 新增字段**：`question_order`（题目顺序）、`section_name`（题目分组）
- **edu_question_bank 新增字段**：`options_text`（选项）、`analysis`（解析）、`difficulty`（难度）、`creator_teacher_id`（创建教师）
- **res_exam_record 新增字段**：`exam_id`、`paper_id`、`paper_question_id`、`score`、`grading_status`、`comment`，用于支持按考试/试卷维度的批改与成绩分析

#### 1.1.1. **基本信息表**

| **序号** | **表名**              | **表注释**                                  | **引擎** | **字符集** | **记录数（参考原数据）** |
| -------- | --------------------- | ------------------------------------------- | -------- | ---------- | ------------------------ |
| 1        | auth_assistant        | 助教信息表 (原 t_assistant)                 | InnoDB   | utf8mb4    | 0                        |
| 2        | auth_class            | 班级信息表 (原 t_clazz)                     | InnoDB   | utf8mb4    | 3                        |
| 3        | edu_exercise          | 课内练习表 (原 t_ex3)                       | InnoDB   | utf8mb4    | 1                        |
| 4        | edu_exercise_item     | 练习题目表 (原 t_ex3_item)                  | InnoDB   | utf8mb4    | 2                        |
| 5        | rel_exercise_class    | 练习-班级关联表 (**新增**)                  | InnoDB   | utf8mb4    | —                        |
| 6        | edu_exam              | 考试信息表 (原 t_exam)                      | InnoDB   | utf8mb4    | 0                        |
| 7        | edu_experiment        | 实验项目表 (原 t_experiment)                | InnoDB   | utf8mb4    | 37                       |
| 8        | edu_experiment_item   | 实验子项目/评分项表 (原 t_experiment_item)  | InnoDB   | utf8mb4    | 143                      |
| 9        | edu_lecture           | 课件/讲座资料表 (原 t_lecture)              | InnoDB   | utf8mb4    | 0                        |
| 10       | edu_paper             | 试卷表 (原 t_paper)                         | InnoDB   | utf8mb4    | 0                        |
| 11       | rel_paper_question    | 试卷题目关联表 (原 t_paper_question)        | InnoDB   | utf8mb4    | 0                        |
| 12       | edu_question_bank     | 题目表/题库 (原 t_question)                 | InnoDB   | utf8mb4    | 0                        |
| 13       | edu_question_type     | 题目类型字典表 (原 t_question_type)         | InnoDB   | utf8mb4    | 7                        |
| 14       | res_score_summary     | 实验成绩汇总表 (原 t_score)                 | InnoDB   | utf8mb4    | 0                        |
| 15       | res_exam_summary      | 考试成绩汇总表 (**新增**)                   | InnoDB   | utf8mb4    | —                        |
| 16       | auth_student          | 学生信息表 (原 t_student)                   | InnoDB   | utf8mb4    | 2226                     |
| 17       | res_fill_blank_detail | 学生填空答案表 (原 t_student_answer)        | InnoDB   | utf8mb4    | 11811                    |
| 18       | res_exercise_record   | 学生练习答题记录表 (原 t_student_excercise) | InnoDB   | utf8mb4    | 0                        |
| 19       | res_experiment_result | 学生实验提交记录表 (原 t_student_item)      | InnoDB   | utf8mb4    | 25112                    |
| 20       | res_submission_log    | 学生实验提交日志表 (原 t_student_item_log)  | InnoDB   | utf8mb4    | 136670                   |
| 21       | sys_student_log       | 学生操作日志表 (原 t_student_log)           | InnoDB   | utf8mb4    | 352452                   |
| 22       | res_exam_record       | 学生试卷答题记录表 (原 t_student_question)  | InnoDB   | utf8mb4    | 0                        |
| 23       | sys_config            | 系统配置参数表 (原 t_sys_config)            | InnoDB   | utf8mb4    | 0                        |
| 24       | sys_admin_log         | 系统日志表 (原 t_sys_log)                   | InnoDB   | utf8mb4    | 231223                   |
| 25       | auth_teacher          | 教师信息表 (原 t_teacher)                   | InnoDB   | utf8mb4    | 2                        |

------

#### 1.1.2. **字段明细表**

##### **1.2.1 auth_assistant — 助教信息表**

| **字段名**        | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**                         |
| ----------------- | ------------ | -------- | ---------- | ----------------- | ------ | ------------------------------------------ |
| id                | BIGINT       | —        | 否         |                   | PK     | 助教主键ID（新增物理主键）                 |
| username          | VARCHAR      | 255      | 否         |                   | UK     | 助教登录账号（原 assistant_account）       |
| password_hash     | VARCHAR      | 255      | 是         | NULL              |        | 登录密码加密存储（原 assistant_password）  |
| bind_student_code | VARCHAR      | 8        | 是         | NULL              |        | 关联的学生学号（原 assistant_student_no）  |
| real_name         | VARCHAR      | 255      | 是         | NULL              |        | 助教真实姓名（原 assistant_student_name）  |
| class_code        | VARCHAR      | 6        | 是         | NULL              |        | 所属班级编号（原 assistant_student_clazz） |
| created_at        | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 创建时间（新增）                           |
| updated_at        | DATETIME     | —        | 否         | ON UPDATE CURRENT |        | 修改时间（新增）                           |

##### **1.2.2 auth_class — 班级信息表**

| **字段名**      | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**                          |
| --------------- | ------------ | -------- | ---------- | ----------------- | ------ | ------------------------------------------- |
| class_code      | VARCHAR      | 6        | 否         |                   | PK     | 班级编号（原 no）                           |
| headmaster_name | TEXT         | —        | 是         | NULL              |        | 班主任姓名/班级备注（原 memo）              |
| class_status    | INT          | —        | 是         | 0                 |        | 班级状态：0=正常, 1=已结课/归档（原 state） |
| created_at      | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 创建时间（新增）                            |
| updated_at      | DATETIME     | —        | 否         | ON UPDATE CURRENT |        | 修改时间（新增）                            |

##### **1.2.3 edu_exercise — 课内练习表**

| **字段名**      | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**                                |
| --------------- | ------------ | -------- | ---------- | ----------------- | ------ | -------------------------------------------------- |
| id              | BIGINT       | —        | 否         |                   | PK     | 练习主键ID（升级为 BIGINT）                        |
| sort_order      | INT          | —        | 是         | NULL              |        | 练习序号用于排序（原 no）                          |
| task_name       | VARCHAR      | 255      | 是         | NULL              |        | 练习名称（原 name）                                |
| teacher_id      | BIGINT       | —        | 是         | NULL              | FK     | 创建教师ID（**新增**，关联 auth_teacher.id）       |
| relate_exp_id   | BIGINT       | —        | 是         | NULL              | FK     | 关联实验ID（原 extype，升级为 BIGINT）             |
| interact_mode   | INT          | —        | 是         | NULL              |        | 交互类型：1=在线答题, 2=HTML（原 type）            |
| description     | VARCHAR      | 255      | 是         | NULL              |        | 练习描述                                            |
| publish_status  | INT          | —        | 是         | 0                 |        | 发布状态：0=草稿,1=已发布,2=已关闭（**新增**）      |
| start_time      | DATETIME     | —        | 是         | NULL              |        | 练习开放开始时间（原 begin_time）                   |
| end_time        | DATETIME     | —        | 是         | NULL              |        | 练习截止时间（原 end_time）                        |
| created_at      | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 创建时间（新增）                                    |
| updated_at      | DATETIME     | —        | 否         | ON UPDATE CURRENT |        | 修改时间（新增）                                    |

##### **1.2.4 edu_exercise_item — 练习题目表**

| **字段名**         | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**                                     |
| ------------------ | ------------ | -------- | ---------- | ----------------- | ------ | ------------------------------------------------------ |
| id                 | BIGINT       | —        | 否         |                   | PK     | 练习题目主键ID（原 excercise_item_id）                 |
| exercise_id        | BIGINT       | —        | 否         |                   | FK     | 所属练习ID（原 excercise_id）                           |
| question           | VARCHAR      | 255      | 是         | NULL              |        | 题目题干                                                |
| options_text       | VARCHAR      | 255      | 是         | NULL              |        | 选项内容（原 options，建议存JSON）                     |
| standard_answer    | VARCHAR      | 255      | 是         | NULL              |        | 正确答案（原 answer）                                  |
| question_type      | INT          | —        | 是         | NULL              |        | 题目类型：1=单选, 2=多选（原 type）                    |
| max_score          | TINYINT      | —        | 是         | NULL              |        | 该题满分分值（**新增**，支撑算分与成绩展示）            |
| question_bank_id   | BIGINT       | —        | 是         | NULL              | FK     | 来源题库ID（**新增**，为空表示手工创建；关联 edu_question_bank.id） |
| created_at         | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 创建时间（新增）                                        |
| updated_at         | DATETIME     | —        | 否         | ON UPDATE CURRENT |        | 修改时间（新增）                                        |

##### **1.2.5 rel_exercise_class — 练习-班级关联表（新增）**

> **业务说明**：设计文档明确要求"教师布置作业给指定班级"，原 `edu_exercise` 表缺少面向班级的分配机制。此表为练习与班级的多对多关联，支撑"布置作业"功能——教师创建练习后可指定开放给哪些班级，学生仅能看见自己所在班级被分配的练习。

| **字段名**  | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明**                                      |
| ----------- | ------------ | -------- | ---------- | ----------------- | ------ | ---------------------------------------------- |
| id          | BIGINT       | —        | 否         |                   | PK     | 关联主键ID                                     |
| exercise_id | BIGINT       | —        | 否         |                   | FK     | 练习ID，关联 edu_exercise.id                   |
| class_code  | VARCHAR      | 6        | 否         |                   | FK     | 班级编号，关联 auth_class.class_code            |
| created_at  | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 创建时间                                       |
| updated_at  | DATETIME     | —        | 否         | ON UPDATE CURRENT |        | 修改时间                                       |

> **唯一索引**：`uk_exercise_class (exercise_id, class_code)` — 同一练习不可重复分配给同一班级。

##### **1.2.6 edu_exam — 考试信息表**

> 用于描述一次具体的考试活动：绑定哪张试卷、开放时间、时长、是否发布等。

| **字段名**   | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**                                |
| ------------ | ------------ | -------- | ---------- | ----------------- | ------ | ------------------------------------------------- |
| id           | BIGINT       | —        | 否         |                   | PK     | 考试主键ID                                        |
| exam_name    | VARCHAR      | 30       | 否         |                   |        | 考试名称/描述（原 description）                   |
| paper_id     | BIGINT       | —        | 是         | NULL              | FK     | 本次考试使用的试卷ID，关联 `edu_paper.id`（**新增**） |
| duration_min | INT          | —        | 是         | NULL              |        | 考试时长：单位分钟（原 duration）                 |
| start_time   | DATETIME     | —        | 是         | NULL              |        | 考试开始时间（原 time）                           |
| is_published | BIT          | 1        | 是         | NULL              |        | 考试开放标记：0/1 可见（原 flag）                 |
| created_at   | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 创建时间（新增）                                  |
| updated_at   | DATETIME     | —        | 否         | ON UPDATE CURRENT |        | 修改时间（新增）                                  |

> 后续可以考虑增加“考试-班级关联表 `rel_exam_class`”，用于控制考试开放给哪些班级。

##### **1.2.7 edu_experiment — 实验项目表**

| **字段名**     | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**                             |
| -------------- | ------------ | -------- | ---------- | ----------------- | ------ | ---------------------------------------------- |
| id             | BIGINT       | —        | 否         |                   | PK     | 实验项目主键ID（原 experiment_id）             |
| sort_order     | INT          | —        | 否         |                   | IK     | 实验序号，用于展示排序（原 experiment_no）     |
| name           | VARCHAR      | 30       | 否         |                   |        | 实验名称（原 experiment_name）                 |
| category_id    | INT          | —        | 是         | NULL              |        | 实验类型，对应字典表（原 experiment_type）     |
| file_type      | VARCHAR      | 10       | 是         | NULL              |        | 指导书文件类型如pdf（原 instruction_type）     |
| requirement    | TEXT         | —        | 是         | NULL              |        | 实验要求/实验目的（原 experiment_requirement） |
| content_desc   | TEXT         | —        | 是         | NULL              |        | 实验内容描述（原 experiment_content）          |
| publish_status | INT          | —        | 是         | NULL              |        | 状态：0=未发布, 1=发布（原 state）             |
| created_at     | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 创建时间（新增）                               |
| updated_at     | DATETIME     | —        | 否         | ON UPDATE CURRENT |        | 修改时间（新增）                               |

##### **1.2.8 edu_experiment_item — 实验子项目/评分项表**

| **字段名**       | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**                            |
| ---------------- | ------------ | -------- | ---------- | ----------------- | ------ | --------------------------------------------- |
| id               | BIGINT       | —        | 否         |                   | PK     | 子项目主键ID（原 experiment_item_id）         |
| sort_order       | INT          | —        | 否         |                   | IK     | 实验内排序编号（原 experiment_item_no）       |
| item_name        | VARCHAR      | 100      | 否         |                   |        | 子项目名称如"报告"（原 experiment_item_name） |
| question_type    | INT          | —        | 否         |                   |        | 子项目类型：1-7（原 experiment_item_type）    |
| question_content | TEXT         | —        | 是         | NULL              |        | 题目具体要求（原 experiment_item_content）    |
| experiment_id    | BIGINT       | —        | 否         |                   | FK     | 所属实验ID                                    |
| standard_answer  | VARCHAR      | 255      | 是         | NULL              |        | 题目标准答案（原 experiment_item_answer）     |
| max_score        | TINYINT      | —        | 是         | NULL              |        | 满分分值（原 experiment_item_score）          |
| item_status      | INT          | —        | 是         | NULL              |        | 状态：0=未发布, 1=已发布（原 state）          |
| created_at       | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 创建时间（新增）                              |
| updated_at       | DATETIME     | —        | 否         | ON UPDATE CURRENT |        | 修改时间（新增）                              |

##### **1.2.9 edu_lecture — 课件/讲座资料表**

| **字段名**     | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**                     |
| -------------- | ------------ | -------- | ---------- | ----------------- | ------ | -------------------------------------- |
| id             | BIGINT       | —        | 否         |                   | PK     | 课件主键ID（原 lecture_id）            |
| lecture_name   | VARCHAR      | 50       | 是         | NULL              |        | 课件名称                               |
| category_id    | INT          | —        | 是         | NULL              |        | 课件分类（原 lecture_type）            |
| file_extension | VARCHAR      | 10       | 是         | NULL              |        | 文件格式如"mp4"（原 lecture_filetype） |
| created_at     | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 创建时间（新增）                       |
| updated_at     | DATETIME     | —        | 否         | ON UPDATE CURRENT |        | 修改时间（新增）                       |

##### **1.2.10 edu_paper — 试卷表**

> 存储由教师从题库中选题组卷后形成的“静态试卷”；一场考试通过 `edu_exam.paper_id` 绑定一张试卷。

| **字段名**      | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**          |
| --------------- | ------------ | -------- | ---------- | ----------------- | ------ | ---------------------------- |
| id              | BIGINT       | —        | 否         |                   | PK     | 试卷主键ID                   |
| paper_code      | INT          | —        | 否         |                   |        | 试卷序号（原 no）            |
| paper_name      | VARCHAR      | 255      | 是         | NULL              |        | 试卷名称（原 name）          |
| description     | VARCHAR      | 255      | 是         | NULL              |        | 试卷描述                     |
| generation_time | DATETIME     | —        | 是         | NULL              |        | 试卷生成业务时间（原 time） |
| created_at      | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 创建时间（新增）             |
| updated_at      | DATETIME     | —        | 否         | ON UPDATE CURRENT |        | 修改时间（新增）             |

##### **1.2.11 rel_paper_question — 试卷题目关联表**

> 描述一张试卷由哪些题目组成，以及每题在试卷中的顺序、分值和分组。

| **字段名**      | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**                                   |
| --------------- | ------------ | -------- | ---------- | ----------------- | ------ | ---------------------------------------------------- |
| id              | BIGINT       | —        | 否         |                   | PK     | 关联记录主键ID                                       |
| paper_id        | BIGINT       | —        | 是         | NULL              | FK     | 所属试卷ID                                           |
| question_id     | BIGINT       | —        | 是         | NULL              | FK     | 所属题目ID，关联 `edu_question_bank.id`             |
| score           | INT          | —        | 是         | NULL              |        | 该题在试卷中的分值                                   |
| question_order  | INT          | —        | 是         | NULL              |        | 该题在试卷中的顺序号（**新增**），用于显示题号顺序   |
| section_name    | VARCHAR      | 50       | 是         | NULL              |        | 题目分组名称，如“单选题”“简答题”（**新增**，可选） |
| created_at      | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 创建时间（新增）                                     |
| updated_at      | DATETIME     | —        | 否         | ON UPDATE CURRENT |        | 修改时间（新增）                                     |

##### **1.2.12 edu_question_bank — 题目表（题库）**

> 存储教师创建的标准化题目，用于练习与考试组卷。题目类型与 `edu_question_type` 对应，可配置选项、标准答案与解析等信息。

| **字段名**        | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**                                                          |
| ----------------- | ------------ | -------- | ---------- | ----------------- | ------ | --------------------------------------------------------------------------- |
| id                | BIGINT       | —        | 否         |                   | PK     | 题目主键ID                                                                  |
| question_content  | VARCHAR      | 255      | 是         | NULL              |        | 题目题干（原 question）                                                     |
| question_type     | INT          | —        | 是         | NULL              |        | 题目类型：1-7（原 type），与 `edu_question_type.type_id` 一一对应           |
| options_text      | VARCHAR      | 1000     | 是         | NULL              |        | 选项内容，主要用于单选/多选/判断题，建议存 JSON 或固定分隔符格式（**新增**） |
| standard_answer   | VARCHAR      | 255      | 是         | NULL              |        | 标准答案（原 answer）                                                       |
| analysis          | TEXT         | —        | 是         | NULL              |        | 题目解析说明（**新增**）                                                     |
| difficulty        | TINYINT      | —        | 是         | NULL              |        | 难度系数：1-5（**新增**）                                                    |
| creator_teacher_id| BIGINT       | —        | 是         | NULL              | FK     | 创建该题目的教师ID，关联 `auth_teacher.id`（**新增**）                      |
| created_at        | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 创建时间（新增）                                                            |
| updated_at        | DATETIME     | —        | 否         | ON UPDATE CURRENT |        | 修改时间（新增）                                                            |

> 说明：
> - 对于客观题（单选、多选、判断），`options_text` + `standard_answer` 即可实现自动判分。
> - 对于主观题（简答、编程、综合），可只维护 `question_content` + `analysis`，由教师人工批改打分。

##### **1.2.13 edu_question_type — 题目类型字典表**

| **字段名** | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**            |
| ---------- | ------------ | -------- | ---------- | ----------------- | ------ | ----------------------------- |
| type_id    | INT          | —        | 否         |                   | PK     | 类型编号（字典表保持INT即可） |
| type_name  | VARCHAR      | 10       | 是         | NULL              |        | 类型名称（如：单选、多选）    |
| created_at | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 创建时间（新增）              |

##### **1.2.14 res_score_summary — 实验成绩汇总表**

| **字段名**    | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**            |
| ------------- | ------------ | -------- | ---------- | ----------------- | ------ | ----------------------------- |
| id            | BIGINT       | —        | 否         |                   | PK     | 成绩记录主键ID（原 score_id） |
| student_id    | BIGINT       | —        | 是         | NULL              | FK     | 学生ID                        |
| experiment_id | BIGINT       | —        | 是         | NULL              | FK     | 实验ID                        |
| total_score   | INT          | —        | 是         | NULL              |        | 实验总成绩（原 score）        |
| created_at    | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 创建时间（新增）              |
| updated_at    | DATETIME     | —        | 否         | ON UPDATE CURRENT |        | 修改时间（新增）              |

##### **1.2.15 res_exam_summary — 考试成绩汇总表（新增）**

> 用于缓存每个学生在每场考试中的总成绩和客观/主观题得分，方便考试成绩列表和统计分析使用。数据可由 `res_exam_record` 统计汇总生成。

| **字段名**       | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明**                                             |
| ---------------- | ------------ | -------- | ---------- | ----------------- | ------ | ---------------------------------------------------- |
| id               | BIGINT       | —        | 否         |                   | PK     | 考试成绩汇总主键ID                                   |
| exam_id          | BIGINT       | —        | 否         |                   | FK     | 考试ID，关联 `edu_exam.id`                           |
| student_id       | BIGINT       | —        | 否         |                   | FK     | 学生ID，关联 `auth_student.id`                       |
| paper_id         | BIGINT       | —        | 是         | NULL              | FK     | 试卷ID，关联 `edu_paper.id`（冗余字段）              |
| total_score      | INT          | —        | 是         | NULL              |        | 本场考试总分                                         |
| objective_score  | INT          | —        | 是         | NULL              |        | 客观题总得分（系统自动判分部分）                     |
| subjective_score | INT          | —        | 是         | NULL              |        | 主观题总得分（教师批改部分）                         |
| created_at       | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 创建时间                                             |
| updated_at       | DATETIME     | —        | 否         | ON UPDATE CURRENT |        | 修改时间                                             |

> 唯一索引建议：`uk_exam_summary (exam_id, student_id)` —— 保证一场考试一个学生只有一条汇总记录。

##### **1.2.16 auth_student — 学生信息表**

| **字段名**       | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**                  |
| ---------------- | ------------ | -------- | ---------- | ----------------- | ------ | ----------------------------------- |
| id               | BIGINT       | —        | 否         |                   | PK     | 学生主键ID（原 student_id）         |
| student_code     | VARCHAR      | 8        | 否         |                   | UK     | 学号（原 student_no，唯一索引）     |
| student_name     | VARCHAR      | 20       | 否         |                   |        | 学生姓名                            |
| password_md5     | CHAR         | 32       | 否         |                   |        | 登录密码 MD5（原 student_password） |
| class_code       | VARCHAR      | 6        | 否         |                   | FK     | 所属班级编号（原 clazz_no）         |
| remark           | TEXT         | —        | 是         | NULL              |        | 备注说明（原 memo）                 |
| account_status   | INT          | —        | 是         | 0                 |        | 账号状态：0正常（原 state）         |
| login_fail_count | INT          | —        | 是         | 0                 |        | 连续登录失败次数（原 error）        |
| last_login_ip    | VARCHAR      | 20       | 是         | NULL              |        | 最近一次登录的IP（原 ip）           |
| created_at       | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 创建时间（新增）                    |
| updated_at       | DATETIME     | —        | 否         | ON UPDATE CURRENT |        | 修改时间（新增）                    |

##### **1.2.17 res_fill_blank_detail — 学生填空答案表**

| **字段名**     | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**                     |
| -------------- | ------------ | -------- | ---------- | ----------------- | ------ | -------------------------------------- |
| id             | BIGINT       | —        | 否         |                   | PK     | 答案记录主键ID                         |
| item_id        | BIGINT       | —        | 是         | NULL              | FK     | 所属实验子项目ID（升级 BIGINT）        |
| blank_index    | INT          | —        | 是         | NULL              |        | 填空题的空号（原 fill_no）             |
| answer_content | VARCHAR      | 255      | 是         | NULL              |        | 填写的文本答案（原 content）           |
| answer_hash    | CHAR         | 32       | 是         | NULL              |        | 答案的MD5哈希查重用（原 content_hash） |
| submit_count   | INT          | —        | 是         | NULL              |        | 答案被提交的次数（原 count）           |
| is_correct     | BIT          | 1        | 是         | NULL              |        | 是否被判定正确                         |
| created_at     | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 创建时间（新增）                       |
| updated_at     | DATETIME     | —        | 否         | ON UPDATE CURRENT |        | 修改时间（新增）                       |

##### **1.2.18 res_exercise_record — 学生练习答题记录表**

| **字段名**    | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**                                  |
| ------------- | ------------ | -------- | ---------- | ----------------- | ------ | ---------------------------------------------------- |
| id            | BIGINT       | —        | 否         |                   | PK     | 答题记录主键ID                                      |
| exercise_id   | BIGINT       | —        | 是         | NULL              | FK     | 所属练习ID（**新增**，冗余加速按作业维度查询成绩，关联 edu_exercise.id） |
| item_id       | BIGINT       | —        | 是         | NULL              | FK     | 练习题目ID                                          |
| student_id    | BIGINT       | —        | 是         | NULL              | FK     | 学生ID                                              |
| choice_answer | VARCHAR      | 30       | 是         | NULL              |        | 选择题/判断题答案（原 answer）                      |
| text_content  | TEXT         | —        | 是         | NULL              |        | 编程题/简答题内容（原 content）                     |
| score         | INT          | —        | 是         | NULL              |        | 该题得分                                            |
| submitted_at  | DATETIME     | —        | 是         | NULL              |        | 业务提交时间（原 fill_time）                        |
| created_at    | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 创建时间（新增）                                    |
| updated_at    | DATETIME     | —        | 否         | ON UPDATE CURRENT |        | 修改时间（新增）                                    |

> **唯一索引**：`uk_res_exe_record (student_id, item_id)` — 对同一学生的同一题目，仅保留一条答题记录，防止重复提交。

##### **1.2.19 res_experiment_result — 学生实验提交记录表**

| **字段名**     | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**                     |
| -------------- | ------------ | -------- | ---------- | ----------------- | ------ | -------------------------------------- |
| id             | BIGINT       | —        | 否         |                   | PK     | 实验提交记录主键（原 student_item_id） |
| student_id     | BIGINT       | —        | 否         |                   | FK     | 学生ID                                 |
| item_id        | BIGINT       | —        | 否         |                   | FK     | 实验子项目ID                           |
| sub_content    | TEXT         | —        | 否         |                   |        | 提交的内容或代码（原 content）         |
| score          | TINYINT      | —        | 是         | NULL              |        | 教师评分                               |
| submitted_at   | DATETIME     | —        | 否         |                   |        | 业务提交时间（原 fill_time）           |
| grading_status | INT          | —        | 是         | NULL              |        | 评分标记：0未评,1已评（原 score_flag） |
| created_at     | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 系统创建时间（新增）                   |
| updated_at     | DATETIME     | —        | 否         | ON UPDATE CURRENT |        | 系统最后修改/评分时间（新增）          |

##### **1.2.20 res_submission_log — 学生实验提交日志表**

| **字段名**       | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**                  |
| ---------------- | ------------ | -------- | ---------- | ----------------- | ------ | ----------------------------------- |
| id               | BIGINT       | —        | 否         |                   | PK     | 日志主键ID（原 log_id）             |
| result_id        | BIGINT       | —        | 是         | NULL              | FK     | 关联的提交记录ID（原 student_item） |
| content_snapshot | TEXT         | —        | 是         | NULL              |        | 提交内容的快照（原 content）        |
| snapshot_time    | DATETIME     | —        | 是         | NULL              |        | 此快照的生成时间（原 fill_time）    |
| created_at       | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 系统落库时间（新增）                |

##### **1.2.21 sys_student_log — 学生操作日志表**

| **字段名**    | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**           |
| ------------- | ------------ | -------- | ---------- | ----------------- | ------ | ---------------------------- |
| id            | BIGINT       | —        | 否         |                   | PK     | 日志主键ID                   |
| account       | VARCHAR      | 20       | 是         | NULL              |        | 操作用户账号                 |
| action_type   | INT          | —        | 是         | NULL              |        | 操作类型：1登录等（原 type） |
| action_detail | VARCHAR      | 255      | 是         | NULL              |        | 操作详情描述（原 info）      |
| op_time       | DATETIME     | —        | 是         | NULL              | IK     | 业务发生时间（原 time）      |
| client_ip     | VARCHAR      | 20       | 是         | NULL              |        | 客户端IP地址（原 ip）        |
| created_at    | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 数据行写入时间（新增）       |

##### **1.2.22 res_exam_record — 学生试卷答题记录表**

> 记录学生在一次考试中对试卷上每一道题的作答、得分及批改状态，是教师批改和成绩分析的基础数据。

| **字段名**        | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**                                                                 |
| ----------------- | ------------ | -------- | ---------- | ----------------- | ------ | ---------------------------------------------------------------------------------- |
| id                | BIGINT       | —        | 否         |                   | PK     | 答题记录主键ID                                                                     |
| exam_id           | BIGINT       | —        | 是         | NULL              | FK     | 考试ID，关联 `edu_exam.id`（**新增**）                                             |
| paper_id          | BIGINT       | —        | 是         | NULL              | FK     | 试卷ID，关联 `edu_paper.id`（**新增**，冗余便于查询）                              |
| paper_question_id | BIGINT       | —        | 是         | NULL              | FK     | 试卷题目关联ID，关联 `rel_paper_question.id`（**新增**，定位到试卷中的具体题目）   |
| student_id        | BIGINT       | —        | 是         | NULL              | FK     | 学生ID，关联 `auth_student.id`                                                     |
| question_id       | BIGINT       | —        | 是         | NULL              | FK     | 题目ID，关联 `edu_question_bank.id`，冗余便于回到题库（**可选冗余**）             |
| student_answer    | VARCHAR      | 255      | 是         | NULL              |        | 学生填写的答案（原 answer，可用于客观题或简短主观题；长答案可考虑改为 TEXT）      |
| score             | INT          | —        | 是         | NULL              |        | 本题得分（**新增**）                                                               |
| grading_status    | INT          | —        | 是         | 0                 |        | 批改状态：0=未批改, 1=系统自动判分, 2=教师已批改（**新增**）                       |
| comment           | VARCHAR      | 255      | 是         | NULL              |        | 教师评语/批注（**新增**）                                                          |
| created_at        | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 创建时间（新增）                                                                   |
| updated_at        | DATETIME     | —        | 否         | ON UPDATE CURRENT |        | 修改时间（新增）                                                                   |

> 索引建议：
> - 在 `(exam_id, student_id)` 上建联合索引，便于查询某场考试某个学生的所有作答。
> - 在 `paper_question_id` 上建索引，便于按某道题分析全班/全考场得分情况。

##### **1.2.23 sys_config — 系统配置参数表**

| **字段名**   | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**         |
| ------------ | ------------ | -------- | ---------- | ----------------- | ------ | -------------------------- |
| config_key   | VARCHAR      | 20       | 否         |                   | PK     | 配置参数名称键（原 param） |
| config_value | VARCHAR      | 100      | 是         | NULL              |        | 配置的具体值（原 value）   |
| created_at   | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 创建时间（新增）           |
| updated_at   | DATETIME     | —        | 否         | ON UPDATE CURRENT |        | 修改时间（新增）           |

##### **1.2.24 sys_admin_log — 系统日志表**

| **字段名**    | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**      |
| ------------- | ------------ | -------- | ---------- | ----------------- | ------ | ----------------------- |
| id            | BIGINT       | —        | 否         |                   | PK     | 日志主键ID              |
| account       | VARCHAR      | 20       | 是         | NULL              |        | 操作的教师/管理账号     |
| action_type   | INT          | —        | 是         | NULL              |        | 日志类型（原 type）     |
| action_detail | VARCHAR      | 255      | 是         | NULL              |        | 日志描述信息（原 info） |
| op_time       | DATETIME     | —        | 是         | NULL              | IK     | 业务发生时间（原 time） |
| client_ip     | VARCHAR      | 100      | 是         | NULL              |        | 客户端IP地址（原 ip）   |
| created_at    | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 系统落库时间（新增）    |

##### **1.2.25 auth_teacher — 教师信息表**

| **字段名**   | **数据类型** | **长度** | **允许空** | **默认值**        | **键** | **说明（还原后）**                      |
| ------------ | ------------ | -------- | ---------- | ----------------- | ------ | --------------------------------------- |
| id           | BIGINT       | —        | 否         |                   | PK     | 教师主键ID（原 teacher_id）             |
| username     | VARCHAR      | 6        | 否         |                   | UK     | 教师登录账号/工号（原 teacher_account） |
| password_md5 | CHAR         | 32       | 否         |                   |        | 登录密码（原 teacher_password）         |
| real_name    | VARCHAR      | 20       | 否         |                   |        | 教师真实姓名（原 teacher_name）         |
| created_at   | DATETIME     | —        | 否         | CURRENT_TIMESTAMP |        | 创建时间（新增）                        |
| updated_at   | DATETIME     | —        | 否         | ON UPDATE CURRENT |        | 修改时间（新增）                        |
