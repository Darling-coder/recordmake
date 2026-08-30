<template>
  <div style="padding: 24px; max-width: 850px; margin: 0 auto;">
    <h2>咨询对话</h2>

    <!-- 聊天气泡容器 -->
    <div
      ref="scrollRef"
      style="border:1px solid #e5e7eb; border-radius:8px; padding:16px; height:450px; overflow-y:auto; background:#fafafa;margin-bottom:16px;"
    >
      <div v-for="item in consultList" :key="item.id">
        <!-- 买家提问：靠右蓝色气泡 -->
        <div style="display:flex; justify-content:flex-end; margin-bottom:14px;">
          <div style="max-width:70%;">
            <div
              style="background:#409eff;color:#fff;padding:10px 14px;border-radius:12px 12px 2px 12px;"
            >{{ item.question }}</div>
            <div style="text-align:right;font-size:12px;color:#888;margin-top:4px;">
              {{ formatTime(item.createTime) }}
            </div>
          </div>
        </div>

        <!-- 卖家回复：靠左灰色气泡，有回复才渲染 -->
        <div v-if="item.reply" style="display:flex; justify-content:flex-start;margin-bottom:14px;">
          <div style="max-width:70%;">
            <div
              style="background:#e4e7ed;color:#333;padding:10px 14px;border-radius:12px 12px 12px 2px;"
            >{{ item.reply }}</div>
            <div style="font-size:12px;color:#888;margin-top:4px;">
              {{ formatTime(item.replyTime) }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 输入框区域 -->
    <div style="display:flex; gap:10px;">
      <el-input
        v-model="question"
        placeholder="输入咨询问题..."
        @keyup.enter="submitQuestion"
      />
      <el-button type="primary" @click="submitQuestion">发送</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { getConsultList, addConsult } from '@/api'

const route = useRoute()
const scrollRef = ref(null)
const sellerId = route.params.sellerId
const recordId = route.params.recordId

const consultList = ref([])
const question = ref('')

// 加载咨询记录
const loadData = async () => {
  const res = await getConsultList({ recordId })
  consultList.value = res.data
  // 滚动到底部
  await nextTick()
  scrollRef.value.scrollTop = scrollRef.value.scrollHeight
}

// 提交提问
const submitQuestion = async () => {
  if (!question.value.trim()) return
  await addConsult({
    recordId,
    sellerId,
    question: question.value.trim()
  })
  question.value = ''
  loadData()
}

// 简单时间格式化
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  return new Date(timeStr).toLocaleString()
}

onMounted(() => {
  loadData()
})
</script>
