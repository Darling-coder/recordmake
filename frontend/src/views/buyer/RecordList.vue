<template>
  <div style="padding:24px">
    <h2>唱片商城</h2>
    <div style="margin:16px 0;display:flex;gap:10px">
      <el-input v-model="searchName" placeholder="搜索唱片" style="width:300px"></el-input>
      <el-button type="primary" @click="loadData">搜索</el-button>
      <el-button type="danger" @click="logout">退出登录</el-button>

    </div>

    <el-row :gutter="20">
      <el-col span="6" v-for="item in tableData" :key="item.id">
        <el-card shadow="hover">
          <img v-if="item.coverImg" :src="'http://localhost:8080' + item.coverImg" style="width:100%;height:200px;object-fit:cover"/>
          <div style="margin-top:10px">
            <h4>{{ item.recordName }}</h4>
            <p>歌手：{{ item.artist }}</p>
            <p style="color:red;font-size:18px">¥{{ item.price }}</p>
            <p>库存：{{ item.stock }}</p>
            <div style="display:flex;gap:6px;margin-top:10px">
              <el-button size="small" @click="goChat(item)">咨询卖家</el-button>
              <el-button size="small" type="primary" @click="openOrder(item)">立即下单</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-pagination
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      :total="total"
      layout="total,prev,pager,next"
      @current-change="loadData"
      @size-change="loadData"
      style="margin-top:20px;text-align:right"
    />


    <!-- 下单弹窗 -->
    <el-dialog v-model="orderDialog" title="确认下单" width="400px">
      <p>唱片：{{ curRecord.recordName }}</p>
      <p>单价：{{ curRecord.price }}</p>
      <el-form label-width="80px">
        <el-form-item label="购买数量">
          <el-input v-model="buyNum" type="number" min="1" :max="curRecord.stock"/>
        </el-form-item>
        <el-form-item label="收货地址">
          <el-input v-model="userAddress"/>
        </el-form-item>
        <!-- 修复这里！模板直接使用我们写好的totalPrice函数 -->
        <p>合计：<span style="color:red">{{ totalPrice() }}</span></p>
      </el-form>
      <template #footer>
        <el-button @click="orderDialog=false">取消</el-button>
        <el-button type="primary" @click="submitOrder">提交订单</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

import { getRecordPage, createOrder } from '@/api'
const router = useRouter()
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)
const tableData = ref([])
const searchName = ref('')
// 下单弹窗
const orderDialog = ref(false)
const curRecord = ref({})
const buyNum = ref(1)
const userAddress = ref(localStorage.getItem('userAddress') || '')
const loadData = async () => {
  const res = await getRecordPage({ pageNum: pageNum.value, pageSize: pageSize.value, recordName: searchName.value })
  const page = res.data
  tableData.value = page.records
  total.value = page.total
}
// 跳转咨询聊天
const goChat = (row) => {
  if(row.stock <= 0) return ElMessage.warning('该唱片暂无库存')
  router.push(`/chat/${row.sellerId}/${row.id}`)
}
// 打开下单弹窗
const openOrder = (row) => {
  if(row.stock <= 0) return ElMessage.warning('该唱片暂无库存，无法下单')
  curRecord.value = {...row}
  buyNum.value = 1
  orderDialog.value = true
}
const totalPrice = () => {
  const price = curRecord.value?.price
  const num = buyNum.value
  if (!price || !num) return "0.00"
  // 原生JS：转数字相乘，保留2位小数
  return (Number(price) * Number(num)).toFixed(2)
}

// 提交订单
const submitOrder = async () => {
  if (!userAddress.value) return ElMessage.error('请填写收货地址')
  if (buyNum.value <= 0) return ElMessage.error('购买数量至少为1')
  if (buyNum.value > curRecord.value.stock) return ElMessage.error('购买数量超出库存')
  try {
    await createOrder({
      recordId: curRecord.value.id,
      buyNum: buyNum.value,
      address: userAddress.value
    })
    ElMessage.success('下单成功，前往我的订单查看')
    orderDialog.value = false
    router.push('/myOrder')
  } catch(err) {
    console.error("下单失败", err)
  }
}
onMounted(() => loadData())

//退出登录
const logout = ()=>{
  //清空本地token
  localStorage.removeItem('token')
  //如果你存了角色，也一起清空
  localStorage.removeItem('userRole')
  //跳转到登录页
  router.push('/login')
  ElMessage.success("已退出登录")
}
</script>
