import type {
  MaterialItem,
  PublicSnapshot,
  StudentOverviewData,
  TeacherDashboardData
} from './types'

export const publicSnapshotFixture: PublicSnapshot = {
  heroMetrics: [
    {
      label: '首波页面覆盖',
      value: '9 个',
      description: '公共入口、学习概览、资料详情与教师管理页已经成型。',
      tone: 'primary'
    },
    {
      label: '资料样本',
      value: '5 份',
      description: '以真实学习节奏组织的讲义、录播、实验指导和案例材料。',
      tone: 'accent'
    },
    {
      label: '角色分流',
      value: '学生 / 教师',
      description: '登录后按身份自动进入学习侧或管理侧入口。',
      tone: 'success'
    },
    {
      label: '导航来源',
      value: '100% 路由元数据',
      description: '后台菜单、标题与分组统一由路由定义驱动。',
      tone: 'warning'
    }
  ],
  highlights: [
    {
      title: '学生学习闭环',
      description: '围绕学习概览、资料和个人中心组织首页到详情的连续体验。',
      tags: ['学习概览', '资料', '个人中心']
    },
    {
      title: '教师工作台',
      description: '从待办任务、班级状态到协同入口，优先承载教学分发与组织管理。',
      tags: ['工作台', '待处理队列', '班级健康度']
    },
    {
      title: '可持续扩展骨架',
      description: '第一波只实现 common 与 user 模块业务页，为 homework、exam、experiment 预留后续接入空间。',
      tags: ['模块化', '双壳层', '生成 API 复用']
    }
  ],
  timeline: [
    {
      title: '第一阶段：壳层与关键页面',
      description: '先完成公共首页、登录注册、学习概览、资料详情与教师工作台。'
    },
    {
      title: '第二阶段：流程型模块接入',
      description: '在既有路由骨架上继续接 homework、exam、experiment 业务链路。'
    },
    {
      title: '第三阶段：真实接口联调',
      description: '用后端新暴露的业务接口替换当前 fixture repository，保留页面结构不动。'
    }
  ],
  notices: [
    {
      title: '账号分流说明',
      detail: '教师账号将优先进入 /admin 工作台；学生账号进入 /learning。',
      time: '今日更新'
    },
    {
      title: '资料页策略',
      detail: '当前资料页采用 fixture repository 驱动，结构已按后续真实接口形态预留。',
      time: '本周重点'
    }
  ]
}

export const studentOverviewFixture: StudentOverviewData = {
  headline: '今日学习概览',
  summary: '把待完成任务、最近反馈和资源入口集中在一个页面里，帮助学生快速判断今天该做什么。',
  metrics: [
    {
      label: '待完成事项',
      value: '3 项',
      description: '含一次作业提醒、一次实验准备与一次资料阅读。',
      trend: '截至周五',
      tone: 'warning'
    },
    {
      label: '本周资料更新',
      value: '5 份',
      description: '新增学习导引、案例分析和实验说明。',
      trend: '持续同步',
      tone: 'primary'
    },
    {
      label: '最近成绩表现',
      value: '88 / 100',
      description: '最近一次阶段测验成绩与教师反馈摘要。',
      trend: '较上周 +6',
      tone: 'success'
    },
    {
      label: '学习完成率',
      value: '92%',
      description: '以已读资料、已完成任务和登录活跃度综合估算。',
      trend: '稳定提升',
      tone: 'accent'
    }
  ],
  tasks: [
    {
      id: 'task-homework-01',
      moduleLabel: '作业',
      title: '软件工程需求分析作业',
      topicLabel: '需求分析专题',
      dueLabel: '截止于周二 20:00',
      status: 'urgent',
      progress: '待提交',
      available: true,
      path: '/homework'
    },
    {
      id: 'task-material-01',
      moduleLabel: '资料',
      title: '阅读学习导引与评分说明',
      topicLabel: '公共资料区',
      dueLabel: '建议今日完成',
      status: 'processing',
      progress: '可立即查看',
      available: true,
      path: '/materials/material-001'
    },
    {
      id: 'task-experiment-01',
      moduleLabel: '实验',
      title: 'Vue 组件拆分实验预习',
      topicLabel: '界面实现专题',
      dueLabel: '开放时间：周四 18:00',
      status: 'processing',
      progress: '可进入实验',
      available: true,
      path: '/experiments'
    },
    {
      id: 'task-exam-01',
      moduleLabel: '考试',
      title: '软件工程阶段测验确认',
      topicLabel: '需求分析专题',
      dueLabel: '今晚 19:30 开始',
      status: 'urgent',
      progress: '待参加',
      available: true,
      path: '/exams'
    }
  ],
  results: [
    {
      id: 'result-01',
      title: '第一周课堂测验',
      moduleLabel: '考试',
      score: '88 分',
      feedback: '基础概念掌握稳定，建议继续补充实验题复盘。',
      updatedAt: '昨天 18:30'
    },
    {
      id: 'result-02',
      title: '资料阅读确认',
      moduleLabel: '资料',
      score: '已完成',
      feedback: '已阅读系统导引和资料分类说明，可继续进入专题资料。',
      updatedAt: '今天 09:10'
    }
  ],
  notices: [
    {
      title: '密码安全提醒',
      detail: '首次登录后建议完善个人资料并修改初始密码。',
      time: '系统提醒'
    },
    {
      title: '资料更新提醒',
      detail: '《前端组件拆分实验指导》已加入本周学习包，可在资料页查看。',
      time: '30 分钟前'
    }
  ],
  quickLinks: [
    {
      title: '资料',
      description: '查看学习导引、讲义和实验资料。',
      badge: '已开放',
      available: true,
      path: '/materials'
    },
    {
      title: '作业模块',
      description: '查看作业列表、详情、提交页和结果反馈。',
      badge: '已开放',
      available: true,
      path: '/homework'
    },
    {
      title: '实验模块',
      description: '查看实验详情、处理步骤和结果反馈。',
      badge: '已开放',
      available: true,
      path: '/experiments'
    },
    {
      title: '个人中心',
      description: '更新联系方式、通知偏好和学习标签。',
      badge: '已开放',
      available: true,
      path: '/profile'
    },
    {
      title: '考试模块',
      description: '查看考试确认页、答题页和结果页。',
      badge: '已开放',
      available: true,
      path: '/exams'
    }
  ]
}

export const materialFixtures: MaterialItem[] = [
  {
    id: 'material-001',
    title: '学习导引与评分说明',
    topicLabel: '公共资料区',
    type: '讲义',
    level: '基础',
    summary: '帮助学生快速理解学习目标、作业安排、实验节奏以及成绩构成，适合作为学期初统一入口材料。',
    teacher: '周老师',
    updatedAt: '2026-04-10 09:30',
    audience: '全体学生',
    tags: ['使用说明', '评分规则', '开学必读'],
    downloads: 318,
    duration: '15 分钟阅读',
    coverNote: '建议新同学在进入作业与实验模块前先完整阅读这份导读。',
    sections: [
      {
        heading: '学习结构',
        content: '当前学习安排由基础讲授、案例研讨、实验训练和阶段测验四部分组成，所有任务最终汇入学习概览页。'
      },
      {
        heading: '成绩构成',
        content: '平时作业、实验完成度、课堂互动和阶段测验共同构成最终成绩，资料阅读记录仅用于学习过程反馈。'
      },
      {
        heading: '建议路径',
        content: '优先阅读本材料，再根据学习概览中的提醒进入资料列表或后续开放的流程型模块。'
      }
    ],
    attachments: [
      { name: '学习导引.pdf', size: '1.2 MB', kind: 'PDF' },
      { name: '评分结构说明.png', size: '420 KB', kind: '图片' }
    ],
    availability: 'open'
  },
  {
    id: 'material-002',
    title: '软件工程需求分析案例拆解',
    topicLabel: '需求分析专题',
    type: '案例',
    level: '专题',
    summary: '通过校内系统迭代案例拆解需求文档、角色边界与评审标准，适合作为教师讲解后的复盘材料。',
    teacher: '王老师',
    updatedAt: '2026-04-11 14:20',
    audience: '软工 2401 / 2402',
    tags: ['需求分析', '案例复盘', '课堂延伸'],
    downloads: 186,
    duration: '25 分钟阅读',
    coverNote: '建议结合课堂笔记一同阅读，重点查看角色边界与验收标准。',
    sections: [
      {
        heading: '案例背景',
        content: '案例以校内资源平台升级为背景，强调需求变更、角色冲突与验收口径统一。'
      },
      {
        heading: '分析方法',
        content: '从业务目标、角色旅程、数据边界和异常流四个维度拆解案例，并提炼常见写作模板。'
      },
      {
        heading: '课堂衔接',
        content: '完成阅读后可回到作业模块，按照相同结构撰写个人需求分析作业。'
      }
    ],
    attachments: [
      { name: '需求分析案例.pdf', size: '2.4 MB', kind: 'PDF' }
    ],
    availability: 'limited'
  },
  {
    id: 'material-003',
    title: 'Vue 组件拆分实验指导',
    topicLabel: '界面实现专题',
    type: '实验指导',
    level: '进阶',
    summary: '围绕页面壳层、模块视图和复用组件拆分思路给出实验前准备、提交要求与评分点。',
    teacher: '林老师',
    updatedAt: '2026-04-09 18:00',
    audience: '前端方向学生',
    tags: ['Vue3', '组件拆分', '实验准备'],
    downloads: 224,
    duration: '18 分钟阅读',
    coverNote: '建议先阅读学习导引，再进入本实验指导理解提交物要求。',
    sections: [
      {
        heading: '实验目标',
        content: '拆分布局、路由与模块页面，确保共享组件与模块边界清晰，适合后续真实接口接入。'
      },
      {
        heading: '提交规范',
        content: '要求提交页面截图、关键组件说明以及一次构建通过的验证结果。'
      },
      {
        heading: '评分重点',
        content: '重点考察布局一致性、模块落位、复用组件设计和可维护性。'
      }
    ],
    attachments: [
      { name: '实验指导.docx', size: '860 KB', kind: '文档' },
      { name: '评分点清单.xlsx', size: '96 KB', kind: '表格' }
    ],
    availability: 'open'
  },
  {
    id: 'material-004',
    title: '阶段测验复盘讲评录播',
    topicLabel: '结构训练专题',
    type: '录播',
    level: '进阶',
    summary: '整理阶段测验中失分较集中的知识点，配合教师讲评录播帮助学生快速完成错题复盘。',
    teacher: '陈老师',
    updatedAt: '2026-04-08 20:15',
    audience: '结构训练方向学生',
    tags: ['录播', '错题复盘', '测验讲评'],
    downloads: 142,
    duration: '32 分钟观看',
    coverNote: '适合在查看最近成绩后直接进入对应复盘内容。',
    sections: [
      {
        heading: '重点知识点',
        content: '重点梳理栈、队列与树结构题目中的高频错误，并给出规范答题示例。'
      },
      {
        heading: '观看建议',
        content: '建议先对照测验反馈定位自己的薄弱点，再按章节观看录播。'
      },
      {
        heading: '后续动作',
        content: '完成复盘后可在个人中心记录关注点，便于下次学习概览继续提醒。'
      }
    ],
    attachments: [
      { name: '讲评录播链接.txt', size: '2 KB', kind: '链接' }
    ],
    availability: 'open'
  },
  {
    id: 'material-005',
    title: '课堂展示模板与汇报清单',
    topicLabel: '公共资料区',
    type: '讲义',
    level: '基础',
    summary: '为学生课堂展示准备统一模板、时间控制建议和汇报清单，减少展示前重复沟通。',
    teacher: '教研组',
    updatedAt: '2026-04-07 11:00',
    audience: '全体学生',
    tags: ['汇报模板', '课堂展示', '公共资源'],
    downloads: 267,
    duration: '10 分钟阅读',
    coverNote: '适合在课堂展示前快速确认结构与材料完整度。',
    sections: [
      {
        heading: '模板结构',
        content: '模板包含背景、目标、方案、结果与反思五部分，适合大多数阶段汇报。'
      },
      {
        heading: '时间控制',
        content: '建议 5 到 8 分钟完成核心表达，额外预留 2 分钟用于教师提问。'
      },
      {
        heading: '检查清单',
        content: '重点检查图示清晰度、结论完整度以及现场演示环境准备情况。'
      }
    ],
    attachments: [
      { name: '课堂展示模板.pptx', size: '1.8 MB', kind: '演示文稿' }
    ],
    availability: 'open'
  }
]

export const teacherDashboardFixture: TeacherDashboardData = {
  headline: '教学工作台',
  summary: '把班级组织、学生风险与近期教学动作集中在一个入口里，帮助教师更快做分发和跟进。',
  metrics: [
    {
      label: '待处理事项',
      value: '24 项',
      description: '包含资料维护、学生跟进与班级协同提醒。',
      trend: '今日重点',
      tone: 'warning'
    },
    {
      label: '活跃班级',
      value: '6 个',
      description: '本周至少有一次资料阅读或课堂互动记录的班级。',
      trend: '较上周 +1',
      tone: 'success'
    },
    {
      label: '资料更新',
      value: '5 份',
      description: '已发布到学生资料页，可持续扩展到作业和实验模块。',
      trend: '本周累计',
      tone: 'primary'
    },
    {
      label: '重点关注学生',
      value: '8 人',
      description: '根据登录活跃度、资料阅读与测验表现综合识别。',
      trend: '需要跟进',
      tone: 'accent'
    }
  ],
  queue: [
    {
      id: 'queue-01',
      title: '跟进需求分析作业提交',
      meta: '作业管理 · 查看最新提交并完成批改分发',
      status: 'urgent',
      path: '/admin/homework/submissions/hw-001',
      linkLabel: '查看提交记录'
    },
    {
      id: 'queue-02',
      title: '核对软工 2402 班学生名单',
      meta: '用户管理 · 近期新增 3 名学生待确认分班',
      status: 'urgent',
      path: '/admin/users',
      linkLabel: '查看名册'
    },
    {
      id: 'queue-03',
      title: '处理模块拆分实验结果',
      meta: '实验管理 · 已有学生提交实验结果待处理',
      status: 'processing',
      path: '/admin/experiments/results/exp-001',
      linkLabel: '处理实验结果'
    }
  ],
  updates: [
    {
      title: '资料中心更新完成',
      detail: '《Vue 组件拆分实验指导》已同步到学生资料页，下载量较昨日增长明显。',
      time: '今天 10:20'
    },
    {
      title: '班级风险提醒',
      detail: '软工 2401 班有 2 名学生连续三天未登录，建议在用户管理页查看明细。',
      time: '今天 08:45'
    },
    {
      title: '教研协同提醒',
      detail: '下周将开放第二波流程模块，当前页面结构可直接承接后续作业与实验接入。',
      time: '本周通知'
    }
  ],
  schedule: [
    {
      id: 'schedule-01',
      time: '周二 14:00',
      title: '需求分析专题讲解',
      detail: '讲解需求分析案例并发布阅读材料。'
    },
    {
      id: 'schedule-02',
      time: '周四 18:00',
      title: '前端实验预热说明',
      detail: '同步实验指导和提交要求。'
    },
    {
      id: 'schedule-03',
      time: '周五 16:30',
      title: '班级展示排期确认',
      detail: '核对班级展示顺序与课堂材料准备。'
    }
  ],
  quickLinks: [
    {
      title: '用户管理',
      description: '查看学生名单、协同教师与重点关注对象。',
      badge: '已开放',
      available: true,
      path: '/admin/users'
    },
    {
      title: '班级管理',
      description: '查看班级结构、班长信息与完课状态。',
      badge: '已开放',
      available: true,
      path: '/admin/classes'
    },
    {
      title: '个人设置',
      description: '维护教师联系信息、办公地点和通知偏好。',
      badge: '已开放',
      available: true,
      path: '/admin/profile'
    },
    {
      title: '作业管理',
      description: '进入作业列表、发布页、提交记录与批改页。',
      badge: '已开放',
      available: true,
      path: '/admin/homework'
    },
    {
      title: '实验管理',
      description: '进入实验列表、实验项管理和结果处理页。',
      badge: '已开放',
      available: true,
      path: '/admin/experiments'
    }
  ]
}
