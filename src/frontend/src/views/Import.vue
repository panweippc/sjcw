<template>
  <div class="import-container">
    <el-card>
      <div class="card-header">
        <h3>批量导入</h3>
      </div>
      <div class="import-content">
        <div class="upload-area">
          <el-form :model="form" label-width="80px">
            <el-form-item label="选择单位">
              <el-select v-model="form.unitId" placeholder="请选择单位" style="width: 250px">
                <el-option v-for="unit in unitOptions" :key="unit.unitId" :label="unit.unitName" :value="unit.unitId" />
              </el-select>
            </el-form-item>
          </el-form>
          <el-upload
            class="upload-demo"
            drag
            :action="uploadUrl"
            :data="uploadData"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
          >
            <el-icon class="upload-icon"><Upload /></el-icon>
            <div class="upload-text">将文件拖到此处，或<em>点击上传</em></div>
            <div class="upload-hint">支持 Excel (.xlsx, .xls) 文件格式</div>
          </el-upload>
          <el-button type="primary" style="margin-top: 20px;" @click="downloadTemplate">
            <el-icon><Download /></el-icon>
            下载导入模板
          </el-button>
          <div class="template-hint">
            <p>模板字段说明：</p>
            <p>凭证字号 | 凭证日期 | 摘要 | 制单人 | 审核人 | 记账人 | 出纳 | 状态 | 分录序号 | 财务会计科目代码 | 财务会计科目名称 | 财务会计借方金额 | 财务会计贷方金额 | 预算会计科目代码 | 预算会计科目名称 | 预算会计借方金额 | 预算会计贷方金额</p>
          </div>
        </div>
        <div class="import-list">
          <h4>导入记录</h4>
          <el-table :data="tableData" border>
            <el-table-column prop="fileName" label="文件名" />
            <el-table-column prop="filePath" label="文件路径" />
            <el-table-column prop="importType" label="导入类型" />
            <el-table-column prop="totalCount" label="导入条数" />
            <el-table-column prop="successCount" label="成功条数" />
            <el-table-column prop="failCount" label="失败条数" />
            <el-table-column prop="status" label="状态">
              <template #default="scope">
                <el-tag :type="scope.row.status === 'success' ? 'success' : 'danger'">
                  {{ scope.row.status === 'success' ? '成功' : '失败' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdTime" label="导入时间" />
            <el-table-column label="操作">
              <template #default="scope">
                <el-button size="small" @click="handleViewLog(scope.row)">查看日志</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Upload, Download } from '@element-plus/icons-vue'
import { getImportRecordList } from '../api/importRecord'
import { getUnitList } from '../api/unit'
import { ElMessage } from 'element-plus'
import * as XLSX from 'xlsx'

const tableData = ref([])
const unitOptions = ref([])
const form = reactive({
  unitId: ''
})

const uploadUrl = '/api/import/voucher'

const uploadData = computed(() => ({
  unitId: form.unitId
}))

const loadData = () => {
  getImportRecordList().then((res) => {
    if (res.code === 200) {
      tableData.value = res.data
    }
  })
}

const loadUnits = () => {
  getUnitList({}).then((res) => {
    if (res.code === 200) {
      unitOptions.value = res.data
      if (unitOptions.value.length > 0) {
        form.unitId = unitOptions.value[0].unitId
      }
    }
  })
}

const handleUploadSuccess = () => {
  ElMessage.success('导入成功')
  loadData()
}

const handleUploadError = (err) => {
  ElMessage.error('导入失败：' + (err.message || '未知错误'))
}

const beforeUpload = (file) => {
  if (!form.unitId) {
    ElMessage.error('请先选择单位')
    return false
  }
  const isExcel = file.type === 'application/vnd.ms-excel' || 
                  file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isExcel) {
    ElMessage.error('只能上传 Excel 文件')
  }
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过 10MB')
  }
  return isExcel && isLt10M
}

const downloadTemplate = () => {
  const templateData = [
    ['凭证字号', '凭证日期', '摘要', '制单人', '审核人', '记账人', '出纳', '状态', '分录序号', '财务会计科目代码', '财务会计科目名称', '财务会计借方金额', '财务会计贷方金额', '预算会计科目代码', '预算会计科目名称', '预算会计借方金额', '预算会计贷方金额'],
    ['记-001', '2026-01-01', '财政发放工资补贴', '张三', '李四', '王五', '赵六', 'DRAFT', 1, '220101', '应付职工薪酬-基本工资', 165000, 0, '72010101', '事业支出-基本支出-人员经费', 165000, 0],
    ['记-001', '2026-01-01', '财政发放工资补贴', '张三', '李四', '王五', '赵六', 'DRAFT', 2, '4001', '财政拨款收入', 0, 165000, '600101', '财政拨款预算收入', 0, 165000]
  ]
  
  const worksheet = XLSX.utils.aoa_to_sheet(templateData)
  const workbook = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(workbook, worksheet, '凭证导入模板')
  XLSX.writeFile(workbook, '凭证导入模板.xlsx')
}

const handleViewLog = (row) => {
  ElMessage.info(`查看 ${row.fileName} 的导入日志`)
}

onMounted(() => {
  loadData()
  loadUnits()
})
</script>

<style scoped>
.import-container {
  padding: 20px;
}

.card-header {
  margin-bottom: 20px;
}

.card-header h3 {
  margin: 0;
}

.import-content {
  display: flex;
  gap: 40px;
}

.upload-area {
  width: 400px;
  text-align: left;
}

.template-hint {
  margin-top: 20px;
  padding: 12px;
  background: #f5f5f5;
  border-radius: 4px;
  font-size: 12px;
  color: #666;
}

.template-hint p {
  margin: 4px 0;
}

.upload-icon {
  font-size: 48px;
  color: #1890ff;
}

.upload-text {
  margin-top: 16px;
  font-size: 16px;
}

.upload-hint {
  margin-top: 8px;
  color: #999;
}

.import-list {
  flex: 1;
}

.import-list h4 {
  margin-bottom: 16px;
}
</style>
