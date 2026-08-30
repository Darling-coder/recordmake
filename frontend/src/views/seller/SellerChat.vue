<template>
  <div style="padding:24px;max-width:900px;margin:0 auto;">
    <h2>收到的咨询</h2>
    <el-table :data="tableData" border>
      <el-table-column label="唱片ID" prop="recordId" width="100"/>
      <el-table-column label="买家提问" prop="question"/>
      <el-table-column label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.status===1?'success':'warning'">
            {{ scope.row.status===1?'已回复':'待回复' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button size="small" type="primary" @click="openReply(scope.row)">回复</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 回复弹窗 -->
    <el-dialog v-model="dialogVisible" title="回复咨询">
      <el-input v-model="replyContent" type="textarea" rows="4" placeholder="输入回复内容"/>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="doReply">确认回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref,onMounted } from 'vue'
import { getMyConsult, replyConsult } from '@/api'

const tableData = ref([])
const dialogVisible = ref(false)
const replyContent = ref('')
const currentId = ref(null)

const load = async ()=>{
  const res = await getMyConsult()
  tableData.value = res.data
}

const openReply = (row)=>{
  currentId.value = row.id
  replyContent.value = row.reply
  dialogVisible.value = true
}

const doReply = async ()=>{
  await replyConsult({id:currentId.value,reply:replyContent.value})
  dialogVisible.value = false
  load()
}

onMounted(()=>load())
</script>
