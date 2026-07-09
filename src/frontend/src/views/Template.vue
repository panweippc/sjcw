<template>
  <div class="template-container">
    <el-card>
      <div class="card-header">
        <h3>模板管理</h3>
        <el-button type="primary" @click="handleAdd">新增模板</el-button>
      </div>
      <div class="search-bar">
        <el-input v-model="searchForm.templateName" placeholder="模板名称" style="width: 200px" />
        <el-button type="primary" @click="loadData">搜索</el-button>
      </div>
      <el-table :data="tableData" border>
        <el-table-column prop="templateName" label="模板名称" />
        <el-table-column prop="templateCode" label="模板编码" />
        <el-table-column prop="templateType" label="模板类型" />
        <el-table-column prop="description" label="模板描述" />
        <el-table-column prop="attachment" label="附件">
          <template #default="scope">
            <el-image 
              v-if="scope.row.attachment && isImage(scope.row.attachment)"
              :src="scope.row.attachment" 
              style="width: 50px; height: 50px;" 
              fit="cover"
              preview-src-list="[scope.row.attachment]"
            />
            <span v-else-if="scope.row.attachment">已上传</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="isEnabled" label="状态">
          <template #default="scope">
            <el-switch v-model="scope.row.isEnabled" />
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <div class="operation-buttons">
              <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row.templateId)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="form.templateName" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="模板编码" prop="templateCode">
          <el-input v-model="form.templateCode" placeholder="请输入模板编码" />
        </el-form-item>
        <el-form-item label="模板类型">
          <el-select v-model="form.templateType" placeholder="请选择模板类型">
            <el-option label="凭证模板" value="voucher" />
            <el-option label="报表模板" value="report" />
            <el-option label="其他模板" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="模板描述">
          <el-input v-model="form.description" type="textarea" placeholder="请输入模板描述" />
        </el-form-item>
        <el-form-item label="附件上传">
          <el-upload
            class="upload-demo"
            drag
            :action="uploadUrl"
            :show-file-list="true"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            :file-list="fileList"
            :limit="1"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">拖拽文件到此处，或<em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip">支持图片、PDF等格式，单个文件最大50MB</div>
            </template>
          </el-upload>
          <div v-if="form.attachment" class="attachment-preview">
            <el-image 
              v-if="isImage(form.attachment)"
              :src="form.attachment" 
              style="width: 200px; height: 200px;" 
              fit="cover"
            />
            <div v-else class="attachment-info">
              <el-icon><Document /></el-icon>
              <span>{{ getFileName(form.attachment) }}</span>
            </div>
            <el-button size="small" type="danger" @click="removeAttachment">删除附件</el-button>
          </div>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.isEnabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getTemplateList, saveTemplate, updateTemplate, deleteTemplate } from '../api/template'
import { ElMessage } from 'element-plus'
import { UploadFilled, Document } from '@element-plus/icons-vue'

const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增模板')
const formRef = ref()
const searchForm = reactive({ templateName: '' })
const fileList = ref([])
const uploadUrl = '/api/upload'

const form = reactive({
  templateId: '',
  templateName: '',
  templateCode: '',
  templateType: '',
  description: '',
  attachment: '',
  isEnabled: true
})

const rules = {
  templateName: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  templateCode: [{ required: true, message: '请输入模板编码', trigger: 'blur' }]
}

const loadData = () => {
  getTemplateList(searchForm).then((res) => {
    if (res.code === 200) {
      tableData.value = res.data
    }
  })
}

const handleAdd = () => {
  dialogTitle.value = '新增模板'
  Object.assign(form, { templateId: '', templateName: '', templateCode: '', templateType: '', description: '', attachment: '', isEnabled: true })
  fileList.value = []
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑模板'
  Object.assign(form, row)
  fileList.value = []
  dialogVisible.value = true
}

const handleDelete = (id) => {
  ElMessage.confirm('确定要删除该模板吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteTemplate(id).then((res) => {
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadData()
      }
    })
  })
}

const handleSubmit = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      const formData = new FormData()
      formData.append('templateName', form.templateName)
      formData.append('templateCode', form.templateCode)
      if (form.templateType) {
        formData.append('templateType', form.templateType)
      }
      if (form.description) {
        formData.append('description', form.description)
      }
      formData.append('isEnabled', form.isEnabled)
      
      if (form.templateId) {
        formData.append('templateId', form.templateId)
        updateTemplate(formData).then((res) => {
          if (res.code === 200) {
            ElMessage.success('修改成功')
            dialogVisible.value = false
            loadData()
          }
        })
      } else {
        saveTemplate(formData).then((res) => {
          if (res.code === 200) {
            ElMessage.success('新增成功')
            dialogVisible.value = false
            loadData()
          }
        })
      }
    }
  })
}

const handleUploadSuccess = (res) => {
  if (res.code === 200) {
    form.attachment = res.data.url
    ElMessage.success('文件上传成功')
  } else {
    ElMessage.error(res.message || '文件上传失败')
  }
}

const handleUploadError = () => {
  ElMessage.error('文件上传失败')
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isPdf = file.type === 'application/pdf'
  const isDoc = file.type.startsWith('application/vnd.openxmlformats-officedocument') || file.type.startsWith('application/msword')
  
  if (!isImage && !isPdf && !isDoc) {
    ElMessage.error('只能上传图片、PDF或Word文件')
    return false
  }
  
  const isLt50M = file.size / 1024 / 1024 < 50
  if (!isLt50M) {
    ElMessage.error('文件大小不能超过50MB')
    return false
  }
  
  return true
}

const isImage = (url) => {
  const ext = url.toLowerCase().substring(url.lastIndexOf('.'))
  return ['.jpg', '.jpeg', '.png', '.gif', '.bmp', '.svg', '.webp'].includes(ext)
}

const getFileName = (url) => {
  return url.substring(url.lastIndexOf('/') + 1)
}

const removeAttachment = () => {
  form.attachment = ''
  fileList.value = []
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.template-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-header h3 {
  margin: 0;
}

.search-bar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.operation-buttons {
  display: flex;
  gap: 8px;
}
</style>
