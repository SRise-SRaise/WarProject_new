import type {
  HomeworkAdminItem,
  HomeworkSubmissionItem,
  HomeworkStudentItem
} from './types'

export const homeworkStudentFixtures: HomeworkStudentItem[] = [
  {
    id: 'hw-001',
    title: '需求分析作业一：角色旅程拆解',
    topicLabel: '需求分析专题',
    teacher: '周老师',
    status: 'pending',
    deadline: '周二 20:00',
    openTime: '已开放 3 天',
    summary: '围绕校园教学协同平台案例，输出角色旅程、主要场景和验收边界，重点训练需求表达的完整性。',
    tags: ['需求分析', '角色旅程', '案例复盘'],
    requirementSections: [
      { title: '提交目标', content: '从学生、教师、管理员三个视角拆解核心任务链路，并标出高频异常流。' },
      { title: '输出要求', content: '提交一份结构化分析文档和一张角色旅程草图，文档需说明验收口径。' },
      { title: '评分重点', content: '关注边界清晰度、场景覆盖率、表达结构和问题拆解能力。' }
    ],
    resources: [
      { name: '案例背景.pdf', size: '1.6 MB', kind: 'PDF' },
      { name: '提交模板.docx', size: '220 KB', kind: '文档' }
    ],
    submitTips: ['建议先阅读资料中的案例拆解材料。', '文档正文控制在 4-6 页，附图单独成页。', '提交后仍可在截止前再次更新内容。'],
    submission: {
      status: 'draft',
      updatedAt: '今天 09:10',
      content: '已完成角色清单和主链路梳理，待补异常流说明。',
      highlights: ['草稿已保存', '尚未正式提交']
    }
  },
  {
    id: 'hw-002',
    title: '前端组件拆分反思报告',
    topicLabel: '界面实现专题',
    teacher: '林老师',
    status: 'submitted',
    deadline: '周四 18:00',
    openTime: '已提交',
    summary: '结合课堂实验，整理壳层拆分、共享组件边界和状态管理归属的反思报告。',
    tags: ['Vue3', '组件拆分', '实验反思'],
    requirementSections: [
      { title: '反思重点', content: '说明壳层、模块视图和复用组件的边界是如何确定的。' },
      { title: '提交形式', content: '提交一份简短说明文档，可附关键截图和目录结构。' },
      { title: '注意事项', content: '不要求额外实现新功能，但需要说明结构决策的原因。' }
    ],
    resources: [
      { name: '课堂样例.zip', size: '3.1 MB', kind: '压缩包' }
    ],
    submitTips: ['重点说明设计取舍而不是重复描述页面样式。'],
    submission: {
      status: 'submitted',
      submittedAt: '昨天 19:08',
      updatedAt: '昨天 19:08',
      content: '已从布局、路由、共享组件三个层次整理结构反思。',
      fileName: 'frontend-structure-report.pdf',
      highlights: ['已提交', '等待教师批阅']
    }
  },
  {
    id: 'hw-003',
    title: '结构训练错题复盘说明',
    topicLabel: '结构训练专题',
    teacher: '陈老师',
    status: 'reviewed',
    deadline: '上周五 17:00',
    openTime: '已完成',
    summary: '对阶段测验中的高频失分点做错题复盘，重点说明错误原因和后续改进计划。',
    tags: ['错题复盘', '阶段测验'],
    requirementSections: [
      { title: '复盘要求', content: '至少挑选三道代表性题目，说明原始错误与改正思路。' },
      { title: '结果呈现', content: '需要给出后续复习安排，形成个人学习改进闭环。' }
    ],
    resources: [
      { name: '复盘讲评录播链接.txt', size: '2 KB', kind: '链接' }
    ],
    submitTips: ['建议结合最近结果页中的测验反馈一起查看。'],
    submission: {
      status: 'reviewed',
      submittedAt: '上周四 21:16',
      updatedAt: '上周五 15:20',
      content: '围绕栈和树结构题目完成了复盘总结。',
      fileName: 'review-note.docx',
      score: '90 分',
      teacherFeedback: '复盘结构清晰，后续可再补充时间复杂度对比。',
      reviewer: '陈老师',
      highlights: ['已批阅', '建议补充复杂度分析']
    }
  }
]

export const homeworkAdminFixtures: HomeworkAdminItem[] = [
  {
    id: 'hw-001',
    title: '需求分析作业一：角色旅程拆解',
    topicLabel: '需求分析专题',
    status: 'published',
    summary: '组织学生拆解角色旅程与验收边界，服务后续案例分析训练。',
    publishScope: '软工 2401 / 2402',
    deadline: '2026-04-16 20:00',
    updatedAt: '今天 08:30',
    submissionCount: 26,
    reviewedCount: 12,
    tags: ['需求分析', '角色旅程'],
    instructions: ['提交一份结构化文档和角色旅程图。', '允许在截止前反复更新。', '重点说明验收边界和异常流。'],
    resources: [{ name: '案例背景.pdf', size: '1.6 MB', kind: 'PDF' }]
  },
  {
    id: 'hw-002',
    title: '前端组件拆分反思报告',
    topicLabel: '界面实现专题',
    status: 'reviewing',
    summary: '围绕组件边界、共享层和状态归属撰写结构反思。',
    publishScope: '前端 2401',
    deadline: '2026-04-18 18:00',
    updatedAt: '昨天 19:10',
    submissionCount: 18,
    reviewedCount: 6,
    tags: ['Vue3', '结构反思'],
    instructions: ['允许附截图说明关键结构。', '重点解释为什么这样拆分。'],
    resources: [{ name: '课堂样例.zip', size: '3.1 MB', kind: '压缩包' }]
  },
  {
    id: 'hw-004',
    title: '课堂展示结构草案',
    topicLabel: '公共资料区',
    status: 'draft',
    summary: '为下周展示准备统一结构草案，尚未发布。',
    publishScope: '待发布',
    deadline: '2026-04-22 17:30',
    updatedAt: '今天 10:15',
    submissionCount: 0,
    reviewedCount: 0,
    tags: ['展示', '草案'],
    instructions: ['完成草案后再选择发布班级和截止时间。'],
    resources: [{ name: '展示模板.pptx', size: '1.8 MB', kind: '演示文稿' }]
  }
]

export const homeworkSubmissionFixtures: HomeworkSubmissionItem[] = [
  {
    id: 'sub-001',
    homeworkId: 'hw-001',
    studentName: '李明',
    className: '软工 2402',
    status: 'submitted',
    submittedAt: '今天 09:40',
    summary: '完成三类角色的主流程与异常流拆解。',
    answerPreview: '文档中已补充教师和学生视角的验收口径，异常流仍可继续细化。',
    attachments: [{ name: 'journey-analysis.pdf', size: '1.1 MB', kind: 'PDF' }]
  },
  {
    id: 'sub-002',
    homeworkId: 'hw-001',
    studentName: '张宁',
    className: '软工 2402',
    status: 'reviewed',
    submittedAt: '昨天 21:05',
    score: '87 分',
    summary: '结构完整，场景覆盖较好。',
    answerPreview: '已覆盖三类角色和关键异常流，表达比较清晰。',
    attachments: [{ name: 'role-journey.docx', size: '320 KB', kind: '文档' }],
    feedback: '场景覆盖完整，建议再加强验收口径描述。'
  },
  {
    id: 'sub-003',
    homeworkId: 'hw-002',
    studentName: '王若溪',
    className: '前端 2401',
    status: 'late',
    submittedAt: '今天 00:18',
    summary: '补交结构反思文档。',
    answerPreview: '重点围绕壳层拆分和 Pinia 状态归属做了说明。',
    attachments: [{ name: 'structure-review.pdf', size: '860 KB', kind: 'PDF' }]
  }
]
