<template>
  <div class="voucher-container">
    <div class="filter-bar" :class="{ 'sticky': isSticky }">
      <div class="filter-left">
        <el-select 
          v-model="filterForm.unitId" 
          placeholder="请选择单位" 
          style="width: 250px"
          @change="handleUnitChange"
          filterable
          remote
          :remote-method="searchUnits"
          loading-text="搜索中"
        >
          <el-option 
            v-for="unit in unitOptions" 
            :key="unit.unitId" 
            :label="unit.unitName + ' (' + unit.unitCode + ')'" 
            :value="unit.unitId" 
          />
        </el-select>
        
        <el-select 
          v-model="filterForm.year" 
          placeholder="请选择年度" 
          style="width: 150px"
          @change="handleYearChange"
        >
          <el-option 
            v-for="year in yearOptions" 
            :key="year" 
            :label="year + '年度'" 
            :value="year" 
          />
        </el-select>
      </div>
      <div class="filter-right">
        <el-button type="primary" @click="loadYearSummary">查询</el-button>
        <el-button @click="resetFilter">重置</el-button>
        <el-button @click="exportExcel" v-if="currentView === 'month'">导出Excel</el-button>
        <el-button type="success" @click="goToImport">导入凭证</el-button>
      </div>
    </div>

    <div v-if="currentView === 'year'" class="year-summary">
      <div class="summary-header">
        <h3>{{ filterForm.year }}年度凭证汇总</h3>
        <span class="summary-info">单位：{{ currentUnitName }}</span>
      </div>
      <div class="month-grid">
        <div 
          v-for="month in monthList" 
          :key="month.value"
          class="month-card"
          :class="{ 'no-data': !hasDataForMonth(month.value), 'has-data': hasDataForMonth(month.value) }"
          @click="handleMonthClick(month.value)"
        >
          <div class="month-name">{{ month.label }}</div>
          <div class="month-count">
            <span>{{ getMonthCount(month.value) }}</span>
            <span class="count-label">张凭证</span>
          </div>
          <div class="month-amount">
            <span class="debit">借 {{ formatAmount(getMonthDebit(month.value)) }}</span>
            <span class="credit">贷 {{ formatAmount(getMonthCredit(month.value)) }}</span>
          </div>
          <div v-if="getMonthLastUpdate(month.value)" class="month-update">
            更新于 {{ formatDate(getMonthLastUpdate(month.value)) }}
          </div>
          <div v-if="!hasDataForMonth(month.value)" class="no-data-text">暂无凭证</div>
        </div>
      </div>
    </div>

    <div v-else class="month-detail">
      <div class="breadcrumb-bar">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item @click.native="backToYearView">凭证管理</el-breadcrumb-item>
          <el-breadcrumb-item @click.native="backToYearView">{{ filterForm.year }}年度</el-breadcrumb-item>
          <el-breadcrumb-item>{{ filterForm.month }}月</el-breadcrumb-item>
        </el-breadcrumb>
        <el-button type="primary" @click="backToYearView" style="margin-left: auto">返回年度视图</el-button>
      </div>
      
      <div class="detail-header">
        <h3>{{ filterForm.year }}年{{ filterForm.month }}月凭证明细</h3>
        <span class="detail-info">共 {{ pagination.total }} 条记录</span>
      </div>
      
      <el-table :data="detailList" border stripe>
        <el-table-column prop="voucherNo" label="凭证字号" width="120" />
        <el-table-column prop="voucherDate" label="凭证日期" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.voucherDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="summary" label="摘要" min-width="200" />
        <el-table-column prop="debitAmount" label="借方金额" width="140">
          <template #default="scope">
            {{ formatAmount(scope.row.debitAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="creditAmount" label="贷方金额" width="140">
          <template #default="scope">
            {{ formatAmount(scope.row.creditAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="creator" label="制单人" width="100" />
        <el-table-column prop="status" label="审核状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300">
          <template #default="scope">
            <div class="operation-btns">
              <el-button size="small" @click="handleView(scope.row)">查看</el-button>
              <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button size="small" type="success" @click="handlePrint(scope.row)">打印</el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row.voucherId)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        :current-page="pagination.pageNum"
        :page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
        style="margin-top: 20px; text-align: right"
      />
    </div>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="1100px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <div class="form-row">
          <el-form-item label="凭证字号" prop="voucherNo">
            <el-input v-model="form.voucherNo" placeholder="请输入凭证字号" style="width: 150px" />
          </el-form-item>
          <el-form-item label="凭证日期" prop="voucherDate">
            <el-date-picker 
              v-model="form.voucherDate" 
              type="date" 
              placeholder="请选择凭证日期"
              style="width: 170px"
            />
          </el-form-item>
          <el-form-item label="制单人">
            <el-input v-model="form.creator" placeholder="请输入制单人" style="width: 120px" />
          </el-form-item>
          <el-form-item label="审核人">
            <el-input v-model="form.auditor" placeholder="请输入审核人" style="width: 120px" />
          </el-form-item>
          <el-form-item label="记账人">
            <el-input v-model="form.bookkeeper" placeholder="请输入记账人" style="width: 120px" />
          </el-form-item>
          <el-form-item label="出纳">
            <el-input v-model="form.cashier" placeholder="请输入出纳" style="width: 120px" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="form.status" placeholder="请选择状态" style="width: 120px">
              <el-option label="草稿" value="DRAFT" />
              <el-option label="已审核" value="APPROVED" />
              <el-option label="已记账" value="POSTED" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" placeholder="请输入摘要" style="width: 100%" />
        </el-form-item>
        
        <el-form-item label="分录明细">
          <div class="entry-table-container">
            <table class="entry-table">
              <thead>
                <tr>
                  <th rowspan="2">序号</th>
                  <th rowspan="2">摘要</th>
                  <th colspan="4">财务会计</th>
                  <th colspan="4">预算会计</th>
                  <th rowspan="2">操作</th>
                </tr>
                <tr>
                  <th>科目代码</th>
                  <th>科目名称</th>
                  <th>借方</th>
                  <th>贷方</th>
                  <th>科目代码</th>
                  <th>科目名称</th>
                  <th>借方</th>
                  <th>贷方</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(entry, index) in form.entries" :key="index">
                  <td>{{ index + 1 }}</td>
                  <td><el-input v-model="entry.summary" placeholder="摘要" size="small" /></td>
                  <td><el-input v-model="entry.faAccountCode" placeholder="科目代码" size="small" /></td>
                  <td><el-input v-model="entry.faAccountName" placeholder="科目名称" size="small" /></td>
                  <td><el-input v-model.number="entry.faDebitAmount" type="number" placeholder="借方" size="small" style="width: 90px" /></td>
                  <td><el-input v-model.number="entry.faCreditAmount" type="number" placeholder="贷方" size="small" style="width: 90px" /></td>
                  <td><el-input v-model="entry.baAccountCode" placeholder="科目代码" size="small" /></td>
                  <td><el-input v-model="entry.baAccountName" placeholder="科目名称" size="small" /></td>
                  <td><el-input v-model.number="entry.baDebitAmount" type="number" placeholder="借方" size="small" style="width: 90px" /></td>
                  <td><el-input v-model.number="entry.baCreditAmount" type="number" placeholder="贷方" size="small" style="width: 90px" /></td>
                  <td><el-button size="small" type="danger" @click="removeEntry(index)">删除</el-button></td>
                </tr>
              </tbody>
              <tfoot>
                <tr>
                  <td colspan="2" class="total-label">合计金额</td>
                  <td colspan="2"></td>
                  <td class="total-value">{{ formatAmount(formFaDebit) }}</td>
                  <td class="total-value">{{ formatAmount(formFaCredit) }}</td>
                  <td colspan="2"></td>
                  <td class="total-value">{{ formatAmount(formBaDebit) }}</td>
                  <td class="total-value">{{ formatAmount(formBaCredit) }}</td>
                  <td><el-button size="small" type="primary" @click="addEntry">添加分录</el-button></td>
                </tr>
              </tfoot>
            </table>
            <div v-if="form.entries.length === 0" class="no-entry-hint">
              <el-button type="primary" @click="addEntry">添加分录</el-button>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog title="查看凭证" v-model="viewDialogVisible" width="1000px" :close-on-click-modal="false">
      <div class="voucher-paper">
        <div class="voucher-header">
          <div class="voucher-title">记账凭证</div>
          <div class="voucher-info">
            <span class="info-item">凭证日期：{{ formatDate(viewVoucher.voucherDate) }}</span>
            <span class="info-item">凭证字号：{{ viewVoucher.voucherNo }}</span>
          </div>
        </div>
        
        <div class="voucher-summary">
          <span class="summary-label">摘要：</span>
          <span class="summary-content">{{ viewVoucher.summary || '-' }}</span>
        </div>
        
        <table class="voucher-table">
          <thead>
            <tr>
              <th rowspan="2" class="col-no">序号</th>
              <th rowspan="2" class="col-summary">摘要</th>
              <th colspan="4" class="col-section">财务会计</th>
              <th colspan="4" class="col-section">预算会计</th>
            </tr>
            <tr>
              <th class="col-code">科目代码</th>
              <th class="col-name">科目名称</th>
              <th class="col-debit">借方金额</th>
              <th class="col-credit">贷方金额</th>
              <th class="col-code">科目代码</th>
              <th class="col-name">科目名称</th>
              <th class="col-debit">借方金额</th>
              <th class="col-credit">贷方金额</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(entry, index) in viewVoucher.entries" :key="entry.entryId || index">
              <td class="col-no">{{ entry.entryNo || index + 1 }}</td>
              <td class="col-summary">{{ entry.summary || '-' }}</td>
              <td class="col-code">{{ entry.faAccountCode || '-' }}</td>
              <td class="col-name">{{ entry.faAccountName || '-' }}</td>
              <td class="col-debit">{{ formatAmount(entry.faDebitAmount) }}</td>
              <td class="col-credit">{{ formatAmount(entry.faCreditAmount) }}</td>
              <td class="col-code">{{ entry.baAccountCode || '-' }}</td>
              <td class="col-name">{{ entry.baAccountName || '-' }}</td>
              <td class="col-debit">{{ formatAmount(entry.baDebitAmount) }}</td>
              <td class="col-credit">{{ formatAmount(entry.baCreditAmount) }}</td>
            </tr>
            <tr v-if="(!viewVoucher.entries || viewVoucher.entries.length === 0)">
              <td colspan="10" class="empty-row">暂无分录数据</td>
            </tr>
          </tbody>
          <tfoot>
            <tr class="total-row">
              <td colspan="2" class="total-label">合计金额</td>
              <td colspan="2"></td>
              <td class="total-debit">{{ formatAmount(viewVoucher.entries?.reduce((sum, e) => sum + (e.faDebitAmount || 0), 0) || 0) }}</td>
              <td class="total-credit">{{ formatAmount(viewVoucher.entries?.reduce((sum, e) => sum + (e.faCreditAmount || 0), 0) || 0) }}</td>
              <td colspan="2"></td>
              <td class="total-debit">{{ formatAmount(viewVoucher.entries?.reduce((sum, e) => sum + (e.baDebitAmount || 0), 0) || 0) }}</td>
              <td class="total-credit">{{ formatAmount(viewVoucher.entries?.reduce((sum, e) => sum + (e.baCreditAmount || 0), 0) || 0) }}</td>
            </tr>
          </tfoot>
        </table>
        
        <div class="voucher-footer">
          <div class="footer-left">
            <span>制单：{{ viewVoucher.creator || '-' }}</span>
            <span>审核：{{ viewVoucher.auditor || '-' }}</span>
            <span>记账：{{ viewVoucher.bookkeeper || '-' }}</span>
            <span>出纳：{{ viewVoucher.cashier || '-' }}</span>
          </div>
          <div class="footer-right">
            <span>状态：<el-tag :type="getStatusType(viewVoucher.status)">{{ getStatusLabel(viewVoucher.status) }}</el-tag></span>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
        <el-button type="success" @click="showPrintOptions">打印</el-button>
      </template>
    </el-dialog>

    <el-dialog title="打印设置" v-model="printDialogVisible" width="450px">
      <el-form :model="printOptions" label-width="100px">
        <el-form-item label="纸张大小">
          <el-select v-model="printOptions.paperSize" placeholder="请选择纸张大小">
            <el-option label="A4" value="A4" />
            <el-option label="凭证纸(240×140)" value="voucher" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="打印方向">
          <el-select v-model="printOptions.orientation" placeholder="请选择打印方向">
            <el-option label="纵向" value="portrait" />
            <el-option label="横向" value="landscape" />
          </el-select>
        </el-form-item>
        <el-form-item label="边距(mm)">
          <div class="margin-options">
            <el-input-number v-model="printOptions.marginTop" :min="0" :max="50" :step="1" style="width: 80px" />
            <el-input-number v-model="printOptions.marginBottom" :min="0" :max="50" :step="1" style="width: 80px" />
            <el-input-number v-model="printOptions.marginLeft" :min="0" :max="50" :step="1" style="width: 80px" />
            <el-input-number v-model="printOptions.marginRight" :min="0" :max="50" :step="1" style="width: 80px" />
          </div>
        </el-form-item>
        <el-form-item label="打印份数">
          <el-input-number v-model="printOptions.copies" :min="1" :max="10" :step="1" style="width: 120px" />
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="printOptions.showHeader">打印表头</el-checkbox>
          <el-checkbox v-model="printOptions.showFooter" style="margin-left: 20px">打印表尾</el-checkbox>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="printDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="executePrint">开始打印</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { getYearSummary, getMonthDetail, getAvailableYears, getVoucherById, getEntriesByVoucherNo, saveVoucher, updateVoucher, saveVoucherWithEntries, updateVoucherWithEntries, deleteVoucher } from '../api/voucher'
import { getUnitList } from '../api/unit'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as XLSX from 'xlsx'

const router = useRouter()

const currentView = ref('year')
const isSticky = ref(false)
const filterForm = reactive({
  unitId: '',
  year: new Date().getFullYear(),
  month: null
})

const yearOptions = ref([])
const unitOptions = ref([])
const yearSummaryData = ref([])
const detailList = ref([])
const pagination = reactive({
  pageNum: 1,
  pageSize: 20,
  total: 0
})

const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('新增凭证')
const formRef = ref()
const form = reactive({
  voucherId: '',
  voucherNo: '',
  voucherDate: null,
  summary: '',
  creator: '',
  auditor: '',
  bookkeeper: '',
  cashier: '',
  status: 'DRAFT',
  entries: []
})

const viewVoucher = reactive({
  voucherId: '',
  voucherNo: '',
  voucherDate: null,
  summary: '',
  debitAmount: 0,
  creditAmount: 0,
  creator: '',
  auditor: '',
  bookkeeper: '',
  cashier: '',
  status: 'DRAFT',
  entries: []
})

const printDialogVisible = ref(false)
const printOptions = reactive({
  paperSize: 'A4',
  orientation: 'portrait',
  marginTop: 10,
  marginBottom: 10,
  marginLeft: 15,
  marginRight: 15,
  copies: 1,
  showHeader: true,
  showFooter: true
})

const rules = {
  voucherNo: [{ required: true, message: '请输入凭证字号', trigger: 'blur' }],
  voucherDate: [{ required: true, message: '请选择凭证日期', trigger: 'change' }]
}

const addEntry = () => {
  form.entries.push({
    faAccountCode: '',
    faAccountName: '',
    faDebitAmount: 0,
    faCreditAmount: 0,
    baAccountCode: '',
    baAccountName: '',
    baDebitAmount: 0,
    baCreditAmount: 0,
    summary: ''
  })
}

const removeEntry = (index) => {
  form.entries.splice(index, 1)
}

const monthList = [
  { value: 1, label: '1月' },
  { value: 2, label: '2月' },
  { value: 3, label: '3月' },
  { value: 4, label: '4月' },
  { value: 5, label: '5月' },
  { value: 6, label: '6月' },
  { value: 7, label: '7月' },
  { value: 8, label: '8月' },
  { value: 9, label: '9月' },
  { value: 10, label: '10月' },
  { value: 11, label: '11月' },
  { value: 12, label: '12月' }
]

const loadUnits = async () => {
  try {
    const res = await getUnitList({})
    if (res.code === 200) {
      unitOptions.value = res.data
      if (unitOptions.value.length > 0) {
        filterForm.unitId = unitOptions.value[0].unitId
        loadAvailableYears()
        await loadYearSummary()
      }
    }
  } catch (e) {
    console.error('加载单位列表失败', e)
  }
}

const searchUnits = async (keyword) => {
  try {
    const res = await getUnitList({ unitName: keyword })
    if (res.code === 200) {
      unitOptions.value = res.data
    }
  } catch (e) {
    console.error('搜索单位失败', e)
  }
}

const loadAvailableYears = () => {
  const currentYear = new Date().getFullYear()
  const years = []
  for (let i = 0; i <= 30; i++) {
    years.push(currentYear - i)
  }
  yearOptions.value = years
}

const loadYearSummary = async () => {
  if (!filterForm.unitId || !filterForm.year) return
  try {
    const res = await getYearSummary(filterForm.unitId, filterForm.year)
    if (res.code === 200) {
      yearSummaryData.value = res.data
    }
  } catch (e) {
    console.error('加载年度汇总失败', e)
  }
}

const loadMonthDetail = async () => {
  if (!filterForm.unitId || !filterForm.year || !filterForm.month) return
  try {
    const res = await getMonthDetail(filterForm.unitId, filterForm.year, filterForm.month, pagination.pageNum, pagination.pageSize)
    if (res.code === 200) {
      detailList.value = res.data.list
      pagination.total = res.data.total
    }
  } catch (e) {
    console.error('加载月度明细失败', e)
  }
}

const handleUnitChange = async () => {
  filterForm.year = new Date().getFullYear()
  filterForm.month = null
  currentView.value = 'year'
  await loadAvailableYears()
  await loadYearSummary()
}

const handleYearChange = async () => {
  filterForm.month = null
  currentView.value = 'year'
  await loadYearSummary()
}

const handleMonthClick = (month) => {
  if (!hasDataForMonth(month)) {
    ElMessageBox.confirm('当月暂无凭证，是否去新建？', '提示', {
      confirmButtonText: '新建',
      cancelButtonText: '取消',
      type: 'info'
    }).then(() => {
      handleAdd()
    }).catch(() => {})
    return
  }
  filterForm.month = month
  currentView.value = 'month'
  pagination.pageNum = 1
  loadMonthDetail()
}

const goToImport = () => {
  router.push('/import')
}

const backToYearView = () => {
  currentView.value = 'year'
  filterForm.month = null
  pagination.pageNum = 1
}

const hasDataForMonth = (month) => {
  return yearSummaryData.value.some(item => item.month === month)
}

const getMonthCount = (month) => {
  const item = yearSummaryData.value.find(item => item.month === month)
  return item ? item.totalCount : 0
}

const getMonthDebit = (month) => {
  const item = yearSummaryData.value.find(item => item.month === month)
  return item ? item.totalDebit : 0
}

const getMonthCredit = (month) => {
  const item = yearSummaryData.value.find(item => item.month === month)
  return item ? item.totalCredit : 0
}

const getMonthLastUpdate = (month) => {
  const item = yearSummaryData.value.find(item => item.month === month)
  return item ? item.lastUpdateTime : null
}

const currentUnitName = computed(() => {
  const unit = unitOptions.value.find(u => u.unitId === filterForm.unitId)
  return unit ? unit.unitName : ''
})

const formFaDebit = computed(() => {
  return form.entries.reduce((sum, entry) => sum + (entry.faDebitAmount || 0), 0)
})

const formFaCredit = computed(() => {
  return form.entries.reduce((sum, entry) => sum + (entry.faCreditAmount || 0), 0)
})

const formBaDebit = computed(() => {
  return form.entries.reduce((sum, entry) => sum + (entry.baDebitAmount || 0), 0)
})

const formBaCredit = computed(() => {
  return form.entries.reduce((sum, entry) => sum + (entry.baCreditAmount || 0), 0)
})

const handleView = async (row) => {
  try {
    const res = await getEntriesByVoucherNo(row.voucherNo)
    if (res.code === 200 && res.data && res.data.length > 0) {
      const entries = res.data
      Object.assign(viewVoucher, {
        voucherId: entries[0].voucherId,
        voucherNo: entries[0].voucherNo,
        voucherDate: entries[0].voucherDate,
        summary: entries[0].summary,
        creator: entries[0].creator,
        auditor: entries[0].auditor,
        bookkeeper: entries[0].bookkeeper,
        cashier: entries[0].cashier,
        status: entries[0].status,
        debitAmount: entries.reduce((sum, e) => sum + (e.faDebitAmount || 0) + (e.baDebitAmount || 0), 0),
        creditAmount: entries.reduce((sum, e) => sum + (e.faCreditAmount || 0) + (e.baCreditAmount || 0), 0),
        entries: entries.map(e => ({
          entryNo: e.entryNo,
          faAccountCode: e.faAccountCode,
          faAccountName: e.faAccountName,
          faDebitAmount: e.faDebitAmount,
          faCreditAmount: e.faCreditAmount,
          baAccountCode: e.baAccountCode,
          baAccountName: e.baAccountName,
          baDebitAmount: e.baDebitAmount,
          baCreditAmount: e.baCreditAmount,
          summary: e.summary
        }))
      })
      viewDialogVisible.value = true
    }
  } catch (e) {
    console.error('获取凭证详情失败', e)
    ElMessage.error('获取凭证详情失败')
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增凭证'
  Object.assign(form, {
    voucherId: '',
    voucherNo: '',
    voucherDate: new Date(),
    summary: '',
    creator: '',
    auditor: '',
    bookkeeper: '',
    cashier: '',
    status: 'DRAFT',
    entries: []
  })
  dialogVisible.value = true
}

const handlePrint = async (row) => {
  try {
    const res = await getEntriesByVoucherNo(row.voucherNo)
    if (res.code === 200 && res.data && res.data.length > 0) {
      const entries = res.data
      Object.assign(viewVoucher, {
        voucherId: entries[0].voucherId,
        voucherNo: entries[0].voucherNo,
        voucherDate: entries[0].voucherDate,
        summary: entries[0].summary,
        creator: entries[0].creator,
        auditor: entries[0].auditor,
        bookkeeper: entries[0].bookkeeper,
        cashier: entries[0].cashier,
        status: entries[0].status,
        debitAmount: entries.reduce((sum, e) => sum + (e.faDebitAmount || 0) + (e.baDebitAmount || 0), 0),
        creditAmount: entries.reduce((sum, e) => sum + (e.faCreditAmount || 0) + (e.baCreditAmount || 0), 0),
        entries: entries.map(e => ({
          entryNo: e.entryNo,
          faAccountCode: e.faAccountCode,
          faAccountName: e.faAccountName,
          faDebitAmount: e.faDebitAmount,
          faCreditAmount: e.faCreditAmount,
          baAccountCode: e.baAccountCode,
          baAccountName: e.baAccountName,
          baDebitAmount: e.baDebitAmount,
          baCreditAmount: e.baCreditAmount,
          summary: e.summary
        }))
      })
      showPrintOptions()
    }
  } catch (e) {
    console.error('获取凭证详情失败', e)
    ElMessage.error('获取凭证详情失败')
  }
}

const handleEdit = async (row) => {
  try {
    const res = await getEntriesByVoucherNo(row.voucherNo)
    if (res.code === 200 && res.data && res.data.length > 0) {
      const entries = res.data
      Object.assign(form, {
        voucherId: entries[0].voucherId,
        voucherNo: entries[0].voucherNo,
        voucherDate: entries[0].voucherDate,
        summary: entries[0].summary,
        creator: entries[0].creator,
        auditor: entries[0].auditor,
        bookkeeper: entries[0].bookkeeper,
        cashier: entries[0].cashier,
        status: entries[0].status,
        entries: entries.map(e => ({
          faAccountCode: e.faAccountCode,
          faAccountName: e.faAccountName,
          faDebitAmount: e.faDebitAmount,
          faCreditAmount: e.faCreditAmount,
          baAccountCode: e.baAccountCode,
          baAccountName: e.baAccountName,
          baDebitAmount: e.baDebitAmount,
          baCreditAmount: e.baCreditAmount,
          summary: e.summary
        }))
      })
      dialogTitle.value = '编辑凭证'
      dialogVisible.value = true
    }
  } catch (e) {
    console.error('获取凭证详情失败', e)
    ElMessage.error('获取凭证详情失败')
  }
}

const showPrintOptions = () => {
  printDialogVisible.value = true
}

const executePrint = () => {
  printDialogVisible.value = false
  
  const printContent = document.createElement('div')
  printContent.innerHTML = `
    <style>
      @page {
        size: ${printOptions.paperSize === 'A4' ? 'A4' : printOptions.paperSize === 'voucher' ? '240mm 140mm' : 'auto'};
        margin: ${printOptions.marginTop}mm ${printOptions.marginRight}mm ${printOptions.marginBottom}mm ${printOptions.marginLeft}mm;
        orientation: ${printOptions.orientation};
      }
      body { font-family: 'SimSun', 'Microsoft YaHei', sans-serif; }
      .voucher-paper { border: 1px solid #ddd; padding: 15px; }
      .voucher-header { text-align: center; margin-bottom: 12px; padding-bottom: 8px; border-bottom: 2px solid #333; }
      .voucher-title { font-size: 18px; font-weight: bold; margin-bottom: 6px; }
      .voucher-info { display: flex; justify-content: center; gap: 25px; font-size: 13px; }
      .voucher-summary { padding: 6px 0; margin-bottom: 10px; font-size: 13px; }
      .summary-label { font-weight: bold; margin-right: 6px; }
      .voucher-table { width: 100%; border-collapse: collapse; font-size: 12px; }
      .voucher-table th, .voucher-table td { border: 1px solid #ddd; padding: 6px; text-align: center; }
      .voucher-table th { background: #f5f5f5; font-weight: bold; }
      .col-no { width: 50px; }
      .col-summary { width: 120px; text-align: left; }
      .col-code { width: 80px; }
      .col-name { width: 120px; }
      .col-debit, .col-credit { width: 100px; }
      .total-row { background: #f9f9f9; }
      .total-row td { font-weight: bold; }
      .total-label { text-align: right; }
      .voucher-footer { display: flex; justify-content: space-between; margin-top: 12px; padding-top: 8px; border-top: 1px solid #ddd; font-size: 12px; }
      .footer-left { display: flex; gap: 20px; }
      @media print {
        body * { visibility: hidden; }
        .print-container, .print-container * { visibility: visible; }
        .print-container { position: absolute; left: 0; top: 0; }
      }
    </style>
    <div class="voucher-paper">
      ${printOptions.showHeader ? `
      <div class="voucher-header">
        <div class="voucher-title">记账凭证</div>
        <div class="voucher-info">
          <span>凭证日期：${formatDate(viewVoucher.voucherDate)}</span>
          <span>凭证字号：${viewVoucher.voucherNo}</span>
        </div>
      </div>
      ` : ''}
      <div class="voucher-summary">
        <span class="summary-label">摘要：</span>
        <span>${viewVoucher.summary || '-'}</span>
      </div>
      <table class="voucher-table">
        <thead>
          <tr>
            <th rowspan="2" class="col-no">序号</th>
            <th rowspan="2" class="col-summary">摘要</th>
            <th colspan="4">财务会计</th>
            <th colspan="4">预算会计</th>
          </tr>
          <tr>
            <th class="col-code">科目代码</th>
            <th class="col-name">科目名称</th>
            <th class="col-debit">借方金额</th>
            <th class="col-credit">贷方金额</th>
            <th class="col-code">科目代码</th>
            <th class="col-name">科目名称</th>
            <th class="col-debit">借方金额</th>
            <th class="col-credit">贷方金额</th>
          </tr>
        </thead>
        <tbody>
          ${viewVoucher.entries && viewVoucher.entries.length > 0 ? viewVoucher.entries.map((entry, index) => `
          <tr>
            <td class="col-no">${entry.entryNo || index + 1}</td>
            <td class="col-summary">${entry.summary || '-'}</td>
            <td class="col-code">${entry.faAccountCode || '-'}</td>
            <td class="col-name">${entry.faAccountName || '-'}</td>
            <td class="col-debit">${formatAmount(entry.faDebitAmount)}</td>
            <td class="col-credit">${formatAmount(entry.faCreditAmount)}</td>
            <td class="col-code">${entry.baAccountCode || '-'}</td>
            <td class="col-name">${entry.baAccountName || '-'}</td>
            <td class="col-debit">${formatAmount(entry.baDebitAmount)}</td>
            <td class="col-credit">${formatAmount(entry.baCreditAmount)}</td>
          </tr>
          `).join('') : '<tr><td colspan="10" style="text-align:center;color:#999;">暂无分录数据</td></tr>'}
        </tbody>
        <tfoot>
          <tr class="total-row">
            <td colspan="2" class="total-label">合计金额</td>
            <td colspan="2"></td>
            <td class="col-debit">${formatAmount(viewVoucher.entries?.reduce((sum, e) => sum + (e.faDebitAmount || 0), 0) || 0)}</td>
            <td class="col-credit">${formatAmount(viewVoucher.entries?.reduce((sum, e) => sum + (e.faCreditAmount || 0), 0) || 0)}</td>
            <td colspan="2"></td>
            <td class="col-debit">${formatAmount(viewVoucher.entries?.reduce((sum, e) => sum + (e.baDebitAmount || 0), 0) || 0)}</td>
            <td class="col-credit">${formatAmount(viewVoucher.entries?.reduce((sum, e) => sum + (e.baCreditAmount || 0), 0) || 0)}</td>
          </tr>
        </tfoot>
      </table>
      ${printOptions.showFooter ? `
      <div class="voucher-footer">
        <div class="footer-left">
          <span>制单：${viewVoucher.creator || '-'}</span>
          <span>审核：${viewVoucher.auditor || '-'}</span>
          <span>记账：${viewVoucher.bookkeeper || '-'}</span>
          <span>出纳：${viewVoucher.cashier || '-'}</span>
        </div>
        <div>状态：${getStatusLabel(viewVoucher.status)}</div>
      </div>
      ` : ''}
    </div>
  `
  
  const printWindow = window.open('', '_blank')
  printWindow.document.write(`<div class="print-container">${printContent.innerHTML}</div>`)
  printWindow.document.close()
  printWindow.print()
  
  setTimeout(() => {
    printWindow.close()
  }, 1000)
}

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return Number(amount).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

const getStatusLabel = (status) => {
  const map = {
    DRAFT: '草稿',
    APPROVED: '已审核',
    POSTED: '已记账'
  }
  return map[status] || status
}

const getStatusType = (status) => {
  const map = {
    DRAFT: 'info',
    APPROVED: 'success',
    POSTED: 'warning'
  }
  return map[status] || 'info'
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该凭证吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    const res = await deleteVoucher(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      if (currentView.value === 'month') {
        loadMonthDetail()
      } else {
        loadYearSummary()
      }
    }
  })
}

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      if (form.entries.length === 0) {
        ElMessage.warning('请至少添加一条分录')
        return
      }
      
      const debitTotal = form.entries.reduce((sum, e) => sum + (e.faDebitAmount || 0) + (e.baDebitAmount || 0), 0)
      const creditTotal = form.entries.reduce((sum, e) => sum + (e.faCreditAmount || 0) + (e.baCreditAmount || 0), 0)
      
      if (Math.abs(debitTotal - creditTotal) > 0.01) {
        ElMessage.error(`借贷不平衡！借方合计：${debitTotal.toFixed(2)}，贷方合计：${creditTotal.toFixed(2)}`)
        return
      }
      
      const data = { 
        ...form, 
        unitId: filterForm.unitId,
        voucherDate: form.voucherDate ? form.voucherDate.toISOString().split('T')[0] : null
      }
      
      if (form.voucherId) {
        const res = await updateVoucherWithEntries(data)
        if (res.code === 200) {
          ElMessage.success('修改成功')
          dialogVisible.value = false
          loadMonthDetail()
        }
      } else {
        const res = await saveVoucherWithEntries(data)
        if (res.code === 200) {
          ElMessage.success('新增成功')
          dialogVisible.value = false
          if (currentView.value === 'month') {
            loadMonthDetail()
          } else {
            loadYearSummary()
          }
        }
      }
    }
  })
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  loadMonthDetail()
}

const handlePageChange = (page) => {
  pagination.pageNum = page
  loadMonthDetail()
}

const resetFilter = () => {
  filterForm.year = new Date().getFullYear()
  filterForm.month = null
  currentView.value = 'year'
  loadYearSummary()
}

const exportExcel = async () => {
  try {
    ElMessage.info('正在导出，请稍候...')
    
    const res = await getMonthDetail(filterForm.unitId, filterForm.year, filterForm.month, 1, 9999)
    if (res.code === 200 && res.data && res.data.list) {
      const data = res.data.list
      const exportData = data.map(item => ({
        '凭证字号': item.voucherNo || '',
        '凭证日期': formatDate(item.voucherDate) || '',
        '摘要': item.summary || '',
        '借方金额': formatAmount(item.debitAmount) || '0.00',
        '贷方金额': formatAmount(item.creditAmount) || '0.00',
        '制单人': item.creator || '',
        '审核状态': getStatusLabel(item.status) || ''
      }))
      
      const worksheet = XLSX.utils.json_to_sheet(exportData)
      const workbook = XLSX.utils.book_new()
      XLSX.utils.book_append_sheet(workbook, worksheet, '凭证明细')
      
      const fileName = `${filterForm.year}年${filterForm.month}月凭证明细.xlsx`
      XLSX.writeFile(workbook, fileName)
      
      ElMessage.success('导出成功')
    } else {
      ElMessage.error('导出失败，暂无数据')
    }
  } catch (e) {
    console.error('导出失败', e)
    ElMessage.error('导出失败')
  }
}

const handleScroll = () => {
  const filterBar = document.querySelector('.filter-bar')
  if (filterBar) {
    isSticky.value = window.scrollY > filterBar.offsetTop
  }
}

onMounted(() => {
  loadUnits()
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.voucher-container {
  padding: 20px;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  margin-bottom: 20px;
  z-index: 100;
}

.filter-bar.sticky {
  position: fixed;
  top: 0;
  left: 200px;
  right: 20px;
}

.filter-left {
  display: flex;
  gap: 16px;
}

.filter-right {
  display: flex;
  gap: 12px;
}

.year-summary {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.summary-header, .detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.summary-header h3, .detail-header h3 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.summary-info, .detail-info {
  color: #909399;
  font-size: 14px;
}

.month-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.month-card {
  padding: 20px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e4e7ed;
}

.month-card.has-data:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border-color: #409eff;
}

.month-card.no-data {
  background: #fafafa;
  border-color: #ebeef5;
  cursor: not-allowed;
}

.month-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.month-count {
  margin-bottom: 8px;
}

.month-count span:first-child {
  font-size: 24px;
  font-weight: 700;
  color: #409eff;
}

.month-count .count-label {
  font-size: 12px;
  color: #909399;
  margin-left: 4px;
}

.month-amount {
  display: flex;
  gap: 12px;
  font-size: 12px;
  margin-bottom: 8px;
}

.month-amount .debit {
  color: #67c23a;
}

.month-amount .credit {
  color: #f56c6c;
}

.month-update {
  font-size: 11px;
  color: #c0c4cc;
}

.no-data-text {
  font-size: 12px;
  color: #c0c4cc;
  text-align: center;
  margin-top: 10px;
}

.month-detail {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.breadcrumb-bar {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.breadcrumb-bar .el-breadcrumb {
  flex: 1;
}

.breadcrumb-bar .el-breadcrumb__item a {
  cursor: pointer;
}

.operation-btns {
  display: flex;
  gap: 4px;
}

.operation-btns .el-button {
  flex-shrink: 0;
}

.voucher-paper {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.voucher-header {
  text-align: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #333;
}

.voucher-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.voucher-info {
  display: flex;
  justify-content: center;
  gap: 30px;
  font-size: 14px;
  color: #333;
}

.info-item {
  display: flex;
  align-items: center;
}

.voucher-summary {
  padding: 8px 0;
  margin-bottom: 12px;
  font-size: 14px;
}

.summary-label {
  font-weight: bold;
  margin-right: 8px;
}

.summary-content {
  color: #333;
}

.voucher-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.voucher-table th,
.voucher-table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: center;
}

.voucher-table th {
  background: #f5f5f5;
  font-weight: bold;
  color: #333;
}

.voucher-table td {
  color: #333;
}

.col-no {
  width: 60px;
}

.col-code {
  width: 100px;
}

.col-name {
  width: 150px;
}

.col-summary {
  width: 200px;
  text-align: left;
}

.col-debit,
.col-credit {
  width: 120px;
}

.empty-row {
  color: #909399;
}

.total-row {
  background: #f9f9f9;
}

.total-row td {
  font-weight: bold;
}

.total-label {
  text-align: right;
}

.total-debit,
.total-credit {
  font-size: 14px;
  color: #333;
}

.voucher-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #ddd;
  font-size: 13px;
}

.footer-left {
  display: flex;
  gap: 30px;
}

.footer-left span {
  display: flex;
  align-items: center;
}

.footer-right {
  display: flex;
  align-items: center;
}

.margin-options {
  display: flex;
  gap: 12px;
  align-items: center;
}

.margin-options .el-input-number {
  margin-right: 4px;
}

.form-row {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.entry-table-container {
  max-height: 400px;
  overflow-y: auto;
}

.entry-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.entry-table th,
.entry-table td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: center;
}

.entry-table th {
  background: #f5f5f5;
  font-weight: bold;
  position: sticky;
  top: 0;
  z-index: 1;
}

.entry-table td {
  padding: 6px;
}

.entry-table .total-label {
  text-align: right;
  font-weight: bold;
}

.entry-table .total-value {
  font-weight: bold;
  color: #333;
}

.no-entry-hint {
  text-align: center;
  padding: 20px;
}
</style>