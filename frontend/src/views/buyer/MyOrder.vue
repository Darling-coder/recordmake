<template>
  <div style="padding:24px">
    <h2>我的购买订单</h2>
    <el-table :data="orderList" border style="margin-top:16px">
      <el-table-column label="订单ID" prop="id" width="80"/>
      <el-table-column label="唱片名称" prop="recordName"/>
      <el-table-column label="购买数量" prop="buyNum"/>
      <el-table-column label="总价" prop="totalPrice"/>
      <el-table-column label="收货地址" prop="address"/>
      <el-table-column label="订单状态">
        <template #default="scope">
          <span v-if="scope.row.orderStatus===0">待付款</span>
          <span v-if="scope.row.orderStatus===1">待发货</span>
          <span v-if="scope.row.orderStatus===2">已发货</span>
          <span v-if="scope.row.orderStatus===3">已完成</span>
          <span v-if="scope.row.orderStatus===-1">已取消</span>
        </template>
      </el-table-column>
      <el-table-column label="下单时间" prop="createTime"/>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button v-if="scope.row.orderStatus===0" size="small" type="danger" @click="handleCancelOrder(scope.row.id)">取消订单</el-button>
          <el-button v-if="scope.row.orderStatus===2" size="small" type="success" @click="handleConfirmOrder(scope.row.id)">确认收货</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getBuyOrderList, cancelOrder, confirmOrder } from '@/api'

const orderList = ref([])

const loadList = async () => {
  const res = await getBuyOrderList()
  orderList.value = res.data
}

// 取消订单
const HandleCancelOrder = async (id) => {
  await cancelOrder(id)
  ElMessage.success('订单已取消')
  loadList()
}

// 确认收货
const HandleConfirmOrder = async (id) => {
  await confirmOrder(id)
  ElMessage.success('确认收货完成')
  loadList()
}

onMounted(loadList)
</script>