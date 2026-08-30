<template>
  <div style="padding: 24px;">
    <h2>我的售卖订单管理</h2>

    <!-- 订单表格 -->
    <el-table
      :data="orderList"
      border
      v-loading="loading"
      style="margin-top: 16px; width: 100%;"
    >
      <el-table-column label="订单ID" prop="id" width="90" align="center" />
      <el-table-column label="唱片名称" prop="recordName" align="center" />
      <el-table-column label="购买数量" prop="buyNum" width="100" align="center" />
      <el-table-column label="订单总收入" prop="totalPrice" width="120" align="center" />
      <el-table-column label="买家收货地址" prop="address" min-width="200" align="center" />
      <el-table-column label="订单状态" width="130" align="center">
        <template #default="scope">
          <el-tag v-if="scope.row.orderStatus === 0" type="info">买家待付款</el-tag>
          <el-tag v-if="scope.row.orderStatus === 1" type="warning">待发货</el-tag>
          <el-tag v-if="scope.row.orderStatus === 2" type="primary">已发货</el-tag>
          <el-tag v-if="scope.row.orderStatus === 3" type="success">已完成</el-tag>
          <el-tag v-if="scope.row.orderStatus === -1" type="danger">订单已取消</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="下单时间" prop="createTime" width="180" align="center" />
      <el-table-column label="操作" width="110" align="center">
        <template #default="scope">
          <!-- 仅待发货订单显示发货按钮 -->
          <el-button
            v-if="scope.row.orderStatus === 1"
            size="small"
            type="success"
            @click="handleSendGoods(scope.row.id)"
          >
            发货
          </el-button>
          <span v-else style="color:#999">无操作</span>
        </template>
      </el-table-column>
    </el-table>

    <!-- 空数据提示 -->
    <el-empty v-if="orderList.length === 0 && !loading" description="暂无售卖订单" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
// 导入封装好的订单接口
import { getSellOrderList, sendOrder } from '@/api'

// 加载状态
const loading = ref(false)
// 订单列表
const orderList = ref([])

// 加载卖家所有售卖订单
const loadOrderList = async () => {
  loading.value = true
  try {
    const res = await getSellOrderList()
    // 后端返回 Result<List<Orders>>  data直接是数组
    orderList.value = res.data
  } catch (err) {
    ElMessage.error('加载订单列表失败')
    console.error(err)
  } finally {
    loading.value = false
  }
}

// 卖家发货操作
const handleSendGoods = (orderId) => {
  ElMessageBox.confirm(
    '确认对该订单执行发货操作？发货后买家可确认收货',
    '发货确认',
    { type: 'warning' }
  ).then(async () => {
    await sendOrder(orderId)
    ElMessage.success('发货操作完成，订单状态已更新')
    // 刷新列表
    loadOrderList()
  }).catch(() => {
    ElMessage.info('已取消发货')
  })
}

// 页面加载自动请求订单数据
onMounted(() => {
  loadOrderList()
})
</script>

<style scoped>
</style>