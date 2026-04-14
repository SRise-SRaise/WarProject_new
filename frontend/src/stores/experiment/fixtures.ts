import type {
  ExperimentAdminItem,
  ExperimentResultItem,
  ExperimentStudentItem
} from './types'

export const experimentStudentFixtures: ExperimentStudentItem[] = [
  {
    id: 'exp-001',
    title: 'Vue 模块壳层拆分实验',
    topicLabel: '界面实现专题',
    teacher: '林老师',
    status: 'in_progress',
    schedule: '本周四 18:00 前完成',
    summary: '围绕双壳层、模块目录和共享组件拆分思路完成一次结构实验，并记录关键取舍。',
    objective: '训练学生在真实业务边界下拆分布局、路由与共享组件。',
    tags: ['Vue3', '模块拆分', '结构实验'],
    materials: [
      { name: '实验指导.docx', size: '860 KB', kind: '文档' },
      { name: '评分点清单.xlsx', size: '96 KB', kind: '表格' }
    ],
    steps: [
      { id: 'step-1', title: '建立模块边界', detail: '明确 views、components、stores 的职责边界。', deliverable: '一份模块划分说明' },
      { id: 'step-2', title: '拆分共享组件', detail: '将可复用的卡片、页头和状态组件从页面里抽离。', deliverable: '至少 2 个共享组件' },
      { id: 'step-3', title: '记录设计决策', detail: '说明为什么采用当前的布局和路由组织方式。', deliverable: '实验反思摘要' }
    ],
    work: {
      status: 'submitted',
      startedAt: '昨天 19:20',
      updatedAt: '今天 09:15',
      note: '已完成壳层拆分与共享组件抽离，正在补充设计决策说明。',
      reportName: 'module-shell-lab.pdf',
      highlights: ['已进入实验处理页', '等待教师反馈']
    }
  },
  {
    id: 'exp-002',
    title: '需求流程可视化实验',
    topicLabel: '需求分析专题',
    teacher: '周老师',
    status: 'pending',
    schedule: '下周二开放',
    summary: '通过流程图和角色节点标注整理教学平台需求流程，强调异常流表达。',
    objective: '训练学生用可视化方式呈现业务流和角色边界。',
    tags: ['需求可视化', '流程图'],
    materials: [{ name: '流程图模板.drawio', size: '42 KB', kind: '模板' }],
    steps: [
      { id: 'step-1', title: '识别角色', detail: '先确认参与者和关键操作节点。', deliverable: '角色清单' },
      { id: 'step-2', title: '绘制主流程', detail: '完成主流程与两个异常流的可视化表达。', deliverable: '一张流程图' }
    ],
    work: {
      status: 'pending',
      startedAt: '待开放',
      updatedAt: '待开放',
      note: '实验尚未开放。',
      highlights: ['未开始']
    }
  },
  {
    id: 'exp-003',
    title: '结构训练实验复盘',
    topicLabel: '结构训练专题',
    teacher: '陈老师',
    status: 'reviewed',
    schedule: '已结束',
    summary: '结合实验结果和测试输出，对关键结构实现过程做复盘。',
    objective: '训练学生归纳实验结果、问题定位和改进计划。',
    tags: ['实验复盘', '结构训练'],
    materials: [{ name: '实验结果样例.txt', size: '8 KB', kind: '文本' }],
    steps: [
      { id: 'step-1', title: '复盘测试输出', detail: '确认失败案例与边界条件。', deliverable: '测试记录' },
      { id: 'step-2', title: '总结修正策略', detail: '说明如何修正实现并预防类似错误。', deliverable: '复盘说明' }
    ],
    work: {
      status: 'reviewed',
      startedAt: '上周三 20:00',
      updatedAt: '上周五 16:10',
      note: '已完成实验复盘并提交总结。',
      reportName: 'experiment-retro.docx',
      score: '91 分',
      teacherFeedback: '复盘完整，建议继续补充复杂度测试对比。',
      highlights: ['已批阅', '可回看教师反馈']
    }
  }
]

export const experimentAdminFixtures: ExperimentAdminItem[] = [
  {
    id: 'exp-001',
    title: 'Vue 模块壳层拆分实验',
    topicLabel: '界面实现专题',
    status: 'running',
    summary: '围绕壳层、模块视图和共享组件组织一次结构实验。',
    schedule: '2026-04-18 18:00',
    scope: '前端 2401',
    updatedAt: '今天 08:50',
    itemCount: 3,
    resultCount: 15,
    tags: ['模块拆分', '结构实验']
  },
  {
    id: 'exp-002',
    title: '需求流程可视化实验',
    topicLabel: '需求分析专题',
    status: 'published',
    summary: '让学生用流程图方式表达角色链路和异常流。',
    schedule: '2026-04-22 20:00',
    scope: '软工 2401 / 2402',
    updatedAt: '昨天 17:20',
    itemCount: 2,
    resultCount: 0,
    tags: ['需求可视化']
  },
  {
    id: 'exp-004',
    title: '课堂协作演示草案',
    topicLabel: '公共资料区',
    status: 'draft',
    summary: '为下轮课堂协作实验准备草案内容和实验项。',
    schedule: '待发布',
    scope: '待设置',
    updatedAt: '今天 10:05',
    itemCount: 4,
    resultCount: 0,
    tags: ['草案']
  }
]

export const experimentResultFixtures: ExperimentResultItem[] = [
  {
    id: 'exp-result-001',
    experimentId: 'exp-001',
    studentName: '李明',
    className: '前端 2401',
    status: 'submitted',
    submittedAt: '今天 09:15',
    summary: '已完成共享组件抽离与壳层说明。',
    reportName: 'module-shell-lab.pdf'
  },
  {
    id: 'exp-result-002',
    experimentId: 'exp-001',
    studentName: '王若溪',
    className: '前端 2401',
    status: 'reviewed',
    submittedAt: '昨天 21:10',
    score: '88 分',
    summary: '实验完成度较高，共享层拆分比较稳。',
    reportName: 'structure-lab-notes.docx',
    feedback: '组件划分清晰，后续可再说明状态提升策略。'
  },
  {
    id: 'exp-result-003',
    experimentId: 'exp-003',
    studentName: '赵晨',
    className: '数构 2401',
    status: 'reviewed',
    submittedAt: '上周五 14:20',
    score: '91 分',
    summary: '实验复盘完整，重点问题定位准确。',
    reportName: 'experiment-retro.docx',
    feedback: '建议补充更多性能测试结论。'
  }
]
