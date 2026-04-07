import type { Router } from 'vue-router'

export function setupRouterGuards(router: Router): void {
  router.beforeEach(async (to, _from, next) => {
    // 设置页面标题
    if (to.meta?.title) {
      document.title = `${to.meta.title} - 通用系统模板`
    } else {
      document.title = '通用系统模板'
    }

    next()
  })

  router.afterEach((_to, _from) => {
    // console.log(`路由跳转: ${from.path} -> ${to.path}`)
  })

  router.onError((error) => {
    console.error('路由错误:', error)
  })
}
