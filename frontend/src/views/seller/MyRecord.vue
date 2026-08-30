<template>
  <div style="padding: 24px;">
    <div style="display: flex; justify-content: space-between; margin-bottom: 16px;">
      <h2>我发布的唱片管理</h2>
      <el-button type="primary" @click="openDialog">新增唱片</el-button>
      <el-button type="danger" @click="logout">退出登录</el-button>
    </div>
    <el-table :data="tableData" border v-loading="loading">
      <el-table-column label="ID" prop="id" width="80" />
      <el-table-column label="封面" width="100">
        <template #default="scope">
          <img v-if="scope.row.coverImg" :src="'http://localhost:8080' + scope.row.coverImg" style="width:60px;height:60px;object-fit:cover"/>
          <span v-else>无图</span>
        </template>
      </el-table-column>
      <el-table-column label="唱片名称" prop="recordName" />
      <el-table-column label="歌手" prop="artist" />
      <el-table-column label="售价" prop="price" />
      <el-table-column label="库存" prop="stock" />
      <el-table-column label="上架状态">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 1" type="success">已上架</el-tag>
          <el-tag v-else type="danger">已下架</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" />
      <el-table-column label="操作" width="300">
        <template #default="scope">
          <el-button size="small" @click="openDialog(scope.row)">编辑</el-button>
          <el-button
            size="small"
            :type="scope.row.status === 1 ? 'warning' : 'success'"
            @click="changeStatus(scope.row)"
          >
            {{ scope.row.status === 1 ? '下架' : '上架' }}
          </el-button>
          <el-button size="small" type="danger" @click="delRecord(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadData"
      @current-change="loadData"
      style="margin-top:16px;text-align:right"
    />
    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" title="唱片信息" width="600px">
      <el-form ref="formRef" :model="form" label-width="90px" :rules="dialogRules">
        <el-form-item label="唱片名称" prop="recordName">
          <el-input v-model="form.recordName"></el-input>
        </el-form-item>
        <el-form-item label="歌手" prop="artist">
          <el-input v-model="form.artist"></el-input>
        </el-form-item>
        <el-form-item label="售价" prop="price">
          <el-input v-model.number="form.price" ></el-input>
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input v-model.number="form.stock" ></el-input>
        </el-form-item>
        <el-form-item label="封面图片">
          <el-upload
            :auto-upload="true"
            :on-success="uploadSuccess"
            action="http://localhost:8080/upload/img"
          >
            <el-button type="primary">点击上传图片</el-button>
          </el-upload>
          <div v-if="form.coverImg" style="margin-top:8px">
            <img :src="'http://localhost:8080' + form.coverImg" style="width:120px;height:120px;object-fit:cover"/>
          </div>
        </el-form-item>

      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
// 改回你项目真实存在的 getRecordPage，不再用不存在的getMyRecordPage
import { getRecordPage, addRecord, updateRecord, deleteRecord } from '@/api'
import { useRouter } from 'vue-router'
const router = useRouter()


// 弹窗表单校验规则对象
const dialogRules = {
  recordName: [{ required: true, message: '请输入唱片名称', trigger: 'blur' }],
  artist: [{ required: true, message: '请输入歌手', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur', type: 'number' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur', type: 'number' }]
}

// 分页变量
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])
const loading = ref(false)

// 弹窗表单
const dialogVisible = ref(false)
const formRef = ref(null)
const form = ref({
  id: null,
  recordName: '',
  artist: '',
  price: 0,
  stock: 0,
  coverImg: '',
  status: 1
})

// 加载列表
const loadData = async () => {
  console.log('🚀 loadData执行')
  loading.value = true
  try {
    console.log("👉准备发起getRecordPage请求")
    const res = await getRecordPage({ pageNum: pageNum.value, pageSize: pageSize.value })
    console.log("✅接口返回 res：", res)
    const pageInfo = res.data
    tableData.value = pageInfo.records
    total.value = pageInfo.total
  } catch (err) {
    console.error('❌列表加载失败：', err)
  } finally {
    loading.value = false
  }
}


// 打开新增/编辑弹窗
const openDialog = (row) => {
  if(formRef.value){
    formRef.value.clearValidate()
  }
  if (row) {
    form.value = { ...row }
  } else {
    form.value = {
      id: null,
      recordName: '',
      artist: '',
      price: 0,
      stock: 0,
      coverImg: '',
      status: 1
    }
  }
  dialogVisible.value = true
}

// 图片上传成功回调
const uploadSuccess = (res) => {
  if (res.code === 200) {
    form.value.coverImg = res.data
    ElMessage.success('图片上传成功')
  }
}

// 提交表单保存
const submitForm = async () => {
  try {
    await formRef.value.validate()
    if (form.value.id) {
      await updateRecord(form.value)
      ElMessage.success('编辑唱片成功')
    } else {
      await addRecord(form.value)
      ElMessage.success('发布唱片成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (err) {
    // 如果是表单校验失败，不打印错误弹窗，element已经显示红色提示
    console.error('保存失败', err)
  }
}


// 上下架状态切换
const changeStatus = async (row) => {
  const newStatus = row.status === 1 ? 0 : 1
  try {
    await updateRecord({ ...row, status: newStatus })
    ElMessage.success('状态修改成功')
    loadData()
  } catch (err) {
    console.error(err)
  }
}

// 删除唱片
const delRecord = (id) => {
  ElMessageBox.confirm('确定删除该唱片？删除后无法恢复', '警告', { type: 'warning' })
  .then(async () => {
    await deleteRecord(id)
    ElMessage.success('删除成功')
    loadData()
  })
}

onMounted(() => {
  console.log('🔥 onMounted触发')
  loadData()
})

//退出登录
const logout = ()=>{
  localStorage.removeItem('token')
  localStorage.removeItem('userRole')
  router.push('/login')
  ElMessage.success("已退出登录")
}
</script>

<style scoped>
</style>
