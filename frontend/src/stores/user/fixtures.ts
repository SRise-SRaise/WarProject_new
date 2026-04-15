import type {
    ClassRecord,
    NotificationPreference,
    RegisteredAccount,
    StudentRosterItem,
    TeacherRosterItem,
    UserRole,
    UserSession
} from './types'

const studentPreferences: NotificationPreference[] = [
    { key: 'notice-material', label: '资料更新提醒', enabled: true },
    { key: 'notice-deadline', label: '截止时间提醒', enabled: true },
    { key: 'notice-feedback', label: '反馈结果提醒', enabled: true }
]

const teacherPreferences: NotificationPreference[] = [
    { key: 'notice-class', label: '班级风险提醒', enabled: true },
    { key: 'notice-material', label: '资料发布提醒', enabled: true },
    { key: 'notice-collab', label: '教研协同提醒', enabled: false }
]

export const sampleLoginAccounts = [
    {
        role: 'student' as const,
        label: '学生体验账号',
        account: 'student',
        password: '123456'
    },
    {
        role: 'teacher' as const,
        label: '教师体验账号',
        account: 'teacher',
        password: '123456'
    }
]

export const defaultUserAccounts: RegisteredAccount[] = [
    {
        id: 'student-001',
        account: 'student',
        password: '123456',
        role: 'student',
        name: '李明'
    },
    {
        id: 'student-002',
        account: '2024001',
        password: 'learn2024',
        role: 'student',
        name: '张宁'
    },
    {
        id: 'teacher-001',
        account: 'teacher',
        password: '123456',
        role: 'teacher',
        name: '周老师'
    },
    {
        id: 'teacher-002',
        account: 'teacher01',
        password: 'teach2024',
        role: 'teacher',
        name: '林老师'
    }
]

function buildAvatar(name: string): string {
    return name.slice(0, 1).toUpperCase()
}

export function buildSessionFromAccount(account: RegisteredAccount): UserSession {
    if (account.role === 'teacher') {
        return {
            id: account.id,
            account: account.account,
            name: account.name,
            role: 'teacher',
            title: '教学负责人 / 协同教师',
            department: '信息工程学院',
            email: `${account.account}@campus.edu.cn`,
            phone: '13800002233',
            location: '教学楼 B309 办公室',
            signature: '把复杂教学流程收束成清晰、可执行的协作界面。',
            avatar: buildAvatar(account.name),
            lastLogin: '今天 08:40',
            focusAreas: ['班级组织', '资料', '教学反馈'],
            preferences: teacherPreferences.map((item) => ({ ...item }))
        }
    }

    return {
        id: account.id,
        account: account.account,
        name: account.name,
        role: 'student',
        title: '软件工程 2024 级学生',
        department: '信息工程学院',
        major: '软件工程',
        className: '软工 2402',
        email: `${account.account}@campus.edu.cn`,
        phone: '13800001122',
        location: '南校区图书馆二层',
        signature: '先理解问题，再动手实现，把每一次提交都变成长期资产。',
        avatar: buildAvatar(account.name),
        lastLogin: '今天 09:05',
        focusAreas: ['资料', '实验准备', '阶段复盘'],
        preferences: studentPreferences.map((item) => ({ ...item }))
    }
}

export const studentRosterFixture: StudentRosterItem[] = [
    {
        id: 'stu-001',
        name: '李明',
        account: '2024001',
        className: '软工 2402',
        major: '软件工程',
        progress: 92,
        pendingTasks: 1,
        status: 'active',
        email: '2024001@campus.edu.cn',
        lastActive: '今天 09:05'
    },
    {
        id: 'stu-002',
        name: '张宁',
        account: '2024007',
        className: '软工 2402',
        major: '软件工程',
        progress: 81,
        pendingTasks: 2,
        status: 'attention',
        email: '2024007@campus.edu.cn',
        lastActive: '昨天 20:15'
    },
    {
        id: 'stu-003',
        name: '陈一帆',
        account: '2024012',
        className: '软工 2401',
        major: '软件工程',
        progress: 95,
        pendingTasks: 0,
        status: 'active',
        email: '2024012@campus.edu.cn',
        lastActive: '今天 08:46'
    },
    {
        id: 'stu-004',
        name: '王若溪',
        account: '2024018',
        className: '前端 2401',
        major: '前端工程',
        progress: 74,
        pendingTasks: 3,
        status: 'attention',
        email: '2024018@campus.edu.cn',
        lastActive: '昨天 16:30'
    },
    {
        id: 'stu-005',
        name: '赵晨',
        account: '2024026',
        className: '软工 2401',
        major: '软件工程',
        progress: 63,
        pendingTasks: 4,
        status: 'inactive',
        email: '2024026@campus.edu.cn',
        lastActive: '3 天前'
    }
]

export const teacherRosterFixture: TeacherRosterItem[] = [
    {
        id: 'teacher-001',
        name: '周老师',
        account: 'teacher01',
        topicLabel: '需求分析专题',
        classCoverage: 3,
        openTasks: 8,
        status: 'active',
        contact: 'teacher01@campus.edu.cn'
    },
    {
        id: 'teacher-002',
        name: '林老师',
        account: 'teacher02',
        topicLabel: '界面实现专题',
        classCoverage: 2,
        openTasks: 5,
        status: 'support',
        contact: 'teacher02@campus.edu.cn'
    },
    {
        id: 'teacher-003',
        name: '陈老师',
        account: 'teacher03',
        topicLabel: '结构训练专题',
        classCoverage: 2,
        openTasks: 4,
        status: 'leave',
        contact: 'teacher03@campus.edu.cn'
    }
]

export const classFixtures: ClassRecord[] = [
    {
        id: 'class-2401',
        name: '软工 2401',
        major: '软件工程',
        term: '2026 春',
        advisor: '周老师',
        studentCount: 32,
        completionRate: 88,
        focus: '阶段测验复盘',
        monitor: '陈一帆'
    },
    {
        id: 'class-2402',
        name: '软工 2402',
        major: '软件工程',
        term: '2026 春',
        advisor: '周老师',
        studentCount: 34,
        completionRate: 84,
        focus: '需求分析作业跟进',
        monitor: '李明'
    },
    {
        id: 'class-fe2401',
        name: '前端 2401',
        major: '前端工程',
        term: '2026 春',
        advisor: '林老师',
        studentCount: 28,
        completionRate: 79,
        focus: '实验准备度提升',
        monitor: '王若溪'
    },
    {
        id: 'class-ds2401',
        name: '数构 2401',
        major: '结构训练方向',
        term: '2026 春',
        advisor: '陈老师',
        studentCount: 30,
        completionRate: 86,
        focus: '错题复盘跟进',
        monitor: '赵晨'
    }
]

export function buildDisplayName(account: string, role: UserRole): string {
    const suffix = account.slice(-4)
    return role === 'teacher' ? `教师 ${suffix}` : `同学 ${suffix}`
}
