/**
 * 用户上下文工厂（Factory Method 模式）
 *
 * <p>集中管理当前用户信息的获取逻辑，消除多个文件中重复的 session 解析代码。
 * 所有需要获取当前用户信息的地方统一调用此类，不再各自实现。</p>
 *
 * <p>支持多级回退策略：
 * <ol>
 *   <li>优先从 eduhub.auth.session 获取（主登录态）</li>
 *   <li>备用从 user 获取（次登录态）</li>
 *   <li>返回默认值（开发/测试场景）</li>
 * </ol>
 * </p>
 */
export interface UserContext {
  /** 用户 ID（优先取 id，备用 userId） */
  id: number
  /** 学号/账号 */
  account: string
  /** 用户姓名 */
  name: string
  /** 班级编号 */
  classCode: string
  /** 班级名称（部分 session 有） */
  className?: string
  /** 角色（teacher / student） */
  role?: string
}

/** 工厂单例 */
class UserContextFactoryImpl {
  private cached: UserContext | null = null

  /**
   * 获取当前用户完整上下文（带缓存）。
   * 仅在导航切换页面时清除缓存。
   */
  getCurrent(): UserContext {
    if (this.cached) return this.cached
    this.cached = this.resolve()
    return this.cached
  }

  /**
   * 获取当前用户 ID（数字）。
   * 用于 API 请求参数。
   */
  getUserId(): number {
    return this.getCurrent().id
  }

  /**
   * 获取当前用户 ID（字符串）。
   * 用于 URL 路径参数。
   */
  getUserIdStr(): string {
    return String(this.getUserId())
  }

  /**
   * 获取学生学号，无则返回空字符串。
   */
  getAccount(): string {
    return this.getCurrent().account
  }

  /**
   * 获取学生姓名，无则返回 '未知用户'。
   */
  getName(): string {
    return this.getCurrent().name || '未知用户'
  }

  /**
   * 获取班级编号，无则返回空字符串。
   */
  getClassCode(): string {
    return this.getCurrent().classCode
  }

  /**
   * 检查是否为教师角色。
   */
  isTeacher(): boolean {
    return this.getCurrent().role === 'teacher'
  }

  /**
   * 检查是否为学生角色。
   */
  isStudent(): boolean {
    return this.getCurrent().role === 'student'
  }

  /**
   * 清除用户缓存（页面切换时调用）。
   */
  clearCache(): void {
    this.cached = null
  }

  // ==================== 内部解析逻辑 ====================

  private resolve(): UserContext {
    const fromSession = this.parseSession()
    if (fromSession) return fromSession

    const fromUser = this.parseUser()
    if (fromUser) return fromUser

    return this.defaultUser()
  }

  private parseSession(): UserContext | null {
    try {
      const raw = localStorage.getItem('eduhub.auth.session')
      if (!raw) return null
      const s = JSON.parse(raw)
      if (!s || (!s.id && !s.userId)) return null
      return {
        id: Number(s.id || s.userId || 1),
        account: String(s.account || s.studentNo || ''),
        name: String(s.name || s.studentName || ''),
        classCode: String(s.classCode || s.clazzNo || ''),
        className: s.className || s.clazzName || undefined,
        role: s.role || (s.userRole === 'admin' ? 'teacher' : 'student')
      }
    } catch {
      return null
    }
  }

  private parseUser(): UserContext | null {
    try {
      const raw = localStorage.getItem('user')
      if (!raw) return null
      const u = JSON.parse(raw)
      if (!u || !u.id) return null
      return {
        id: Number(u.id),
        account: String(u.studentCode || u.account || ''),
        name: String(u.studentName || u.name || ''),
        classCode: String(u.classCode || ''),
        className: undefined,
        role: undefined
      }
    } catch {
      return null
    }
  }

  private defaultUser(): UserContext {
    return {
      id: 1,
      account: '2023001234',
      name: '张三',
      classCode: 'CS2301',
      className: undefined,
      role: 'student'
    }
  }
}

/** 导出工厂单例 */
export const userContextFactory = new UserContextFactoryImpl()

/** 兼容旧代码的别名（方便迁移） */
export const getCurrentUser = () => userContextFactory.getCurrent()
export const getLoginUser = () => userContextFactory.getCurrent()
