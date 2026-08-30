import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 5000
})

// 请求拦截器 自动携带token
service.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  console.log("拦截器拿到token：", token)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器：只做提示，不做页面跳转！跳转交给路由守卫
service.interceptors.response.use(res => {
  const result = res.data

  // 业务码401，仅清除token+弹窗，不跳转
  if (result.code === 401) {
    ElMessage.error('登录已失效，请重新登录')
    localStorage.removeItem('token')
    return Promise.reject(result)
  }

  // 其他业务错误提示
  if (result.code !== 200) {
    ElMessage.error(result.msg)
    return Promise.reject(result)
  }
  // 直接返回后端完整 {code,msg,data} 对象
  return result
}, err => {
  // HTTP状态401：只弹窗清token，彻底删除location.href跳转
  if (err.response?.status === 401) {
    ElMessage.error('登录已失效，请重新登录')
    localStorage.removeItem('token')
  }

  if (err.response?.data?.msg) {
    ElMessage.error(err.response.data.msg)
  } else {
    ElMessage.error('网络请求失败')
  }
  return Promise.reject(err)
})

// ============ 用户模块接口 ============
/**
 * 登录接口
 * @param {Object} data 登录表单 {username,password,userRole}
 */
export function login(data) {
  return service.post('/user/login', data)
}
/**
 * 注册接口
 * @param {Object} data 注册表单
 */
export function register(data) {
  return service.post('/user/register', data)
}

// ============ 唱片Record模块 ============
/**
 * 唱片分页查询
 * @param {Object} params {pageNum,pageSize,recordName}
 */
export function getRecordPage(params) {
  return service.get('/record/page', { params })
}
/**
 * 根据id查询单条唱片
 * @param {Number} id 唱片id
 */
export function getRecordById(id) {
  return service.get(`/record/${id}`)
}
/**
 * 新增唱片（卖家发布）
 * @param {Object} data 唱片表单
 */
export function addRecord(data) {
  return service.post('/record', data)
}
/**
 * 编辑唱片
 * @param {Object} data 完整唱片信息含id
 */
export function updateRecord(data) {
  return service.put('/record', data)
}
/**
 * 删除唱片
 * @param {Number} id 唱片id
 */
export function deleteRecord(id) {
  return service.delete(`/record/${id}`)
}
/**
 * 修改唱片上下架状态（复用编辑接口）
 * @param {Object} data 唱片+新status状态
 */
export function updateRecordStatus(data) {
  return service.put('/record', data)
}

// ============ 图片上传 ============
/**
 * 上传唱片封面图片
 * @param {File} file 图片文件对象
 */
export function uploadImg(file) {
  const formData = new FormData()
  formData.append('file', file)
  return service.post('/upload/img', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// ============ 订单Order模块 ============
/**
 * 买家创建订单下单
 * @param {Object} data 订单参数
 */
export function createOrder(data) {
  return service.post('/order/create', data)
}
/**
 * 买家查询自己的全部订单
 */
export function getBuyOrderList() {
  return service.get('/order/buy/list')
}
/**
 * 卖家查询自己收到的订单
 */
export function getSellOrderList() {
  return service.get('/order/sell/list')
}
/**
 * 买家取消订单
 * @param {Number} orderId 订单id
 */
export function cancelOrder(orderId) {
  return service.put(`/order/cancel/${orderId}`)
}
/**
 * 卖家发货
 * @param {Number} orderId 订单id
 */
export function sendOrder(orderId) {
  return service.put(`/order/send/${orderId}`)
}
/**
 * 买家确认收货
 * @param {Number} orderId 订单id
 */
export function confirmOrder(orderId) {
  return service.put(`/order/confirm/${orderId}`)
}

// ============ 咨询聊天Consult模块 ============
/**
 * 买家发起咨询消息
 * @param {Object} data 消息内容
 */
export function sendConsultMsg(data) {
  return service.post('/consult/send', data)
}
/**
 * 卖家回复咨询
 * @param {Object} data 回复消息
 */
export function replyConsultMsg(data) {
  return service.post('/consult/reply', data)
}
/**
 * 买家查看自己所有咨询对话
 * @param {Object} params 分页参数
 */
export function getBuyConsultList(params) {
  return service.get('/consult/buyer/list', { params })
}
/**
 * 卖家查看所有买家咨询对话
 * @param {Object} params 分页参数
 */
export function getSellConsultList(params) {
  return service.get('/consult/seller/list', { params })
}
/**
 * 查看单条咨询聊天详情
 * @param {Number} id 咨询会话id
 */
export function getConsultDetail(id) {
  return service.get(`/consult/${id}`)
}

// ========== 咨询聊天接口 ==========
// 获取唱片咨询列表
export const getConsultList = (params)=> service.get('/consult/list',{params})
// 买家新增提问
export const addConsult = (data)=> service.post('/consult',data)
// 卖家获取自己收到的咨询
export const getMyConsult = ()=> service.get('/consult/my')
// 卖家回复咨询
export const replyConsult = (data)=> service.put('/consult/reply',data)
