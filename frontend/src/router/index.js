import { createRouter, createWebHistory } from 'vue-router'
const routes = [
  {
    path: '/',
    redirect: '/recordList'
  },
  {
    path: '/login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/recordList',
    component: () => import('../views/buyer/RecordList.vue')
  },
  {
    path: '/myOrder',
    component: () => import('../views/buyer/MyOrder.vue')
  },
  {
    path: '/chat/:sellerId/:recordId',
    component: () => import('../views/buyer/Chat.vue')
  },
  {
    path: '/myRecord',
    component: () => import('../views/seller/MyRecord.vue')
  },
  {
    path: '/sellerOrder',
    component: () => import('../views/seller/SellerOrder.vue')
  },
  {
    path: '/sellerChat',
    component: () => import('../views/seller/SellerChat.vue')
  }
]
const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('userRole')
  const whiteList = ['/login', '/register']
  const buyerPages = ['/recordList', '/myOrder']
  const sellerPages = ['/myRecord', '/sellerOrder', '/sellerChat']

  // 1、无token，只能访问白名单
  if (!token) {
    localStorage.removeItem('userRole')
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next('/login')
    }
    return
  }

  //2、有token访问登录注册，跳对应首页
  if (whiteList.includes(to.path)) {
    if(userRole === 'seller'){
      next('/myRecord')
    }else{
      next('/recordList')
    }
    return
  }

  //3、校验角色权限
  if(userRole === 'buyer'){
    //买家不能进卖家页面
    if(sellerPages.includes(to.path)){
      next('/recordList')
      return
    }
  }else if(userRole === 'seller'){
    //卖家不能进买家页面，去掉chat拦截！！！！！！！！！！！！！！！！！
    if(buyerPages.includes(to.path)){
      next('/myRecord')
      return
    }
  }else{
    localStorage.clear()
    next('/login')
    return
  }

  // ✅全部校验通过，正常放行，只执行一次next
  next()
})

export default router

