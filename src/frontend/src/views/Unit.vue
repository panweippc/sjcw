<template>
  <div class="unit-container">
    <el-card>
      <div class="card-header">
        <h3>单位管理</h3>
        <el-button type="primary" @click="handleAdd">+新增</el-button>
      </div>
      <div class="unit-content">
        <div class="tree-panel">
          <el-input 
            v-model="searchKeyword" 
            placeholder="搜索单位名称" 
            prefix-icon="Search"
            @input="handleSearch"
            clearable
          />
          <el-tree
            :data="treeData"
            :props="{ label: 'unitName', children: 'children' }"
            node-key="unitId"
            ref="treeRef"
            @node-click="handleNodeClick"
            default-expand-all
          >
            <template #default="{ node, data }">
              <span>{{ data.unitName }}</span>
            </template>
          </el-tree>
        </div>
        <div class="table-panel">
          <el-table :data="tableData" border>
            <el-table-column type="expand" width="40">
              <template #default="scope">
                <el-table :data="scope.row.children || []" border>
                  <el-table-column prop="unitName" label="子单位名称" />
                  <el-table-column prop="unitCode" label="单位编码" />
                  <el-table-column prop="contactPerson" label="联系人" />
                  <el-table-column prop="phone" label="联系电话" />
                  <el-table-column prop="address" label="单位地址" />
                  <el-table-column label="操作" width="200">
                    <template #default="childScope">
                      <div class="operation-buttons">
                        <el-button size="small" @click="handleView(childScope.row)">查看</el-button>
                        <el-button size="small" type="warning" @click="handleEdit(childScope.row)">编辑</el-button>
                        <el-button size="small" type="danger" @click="handleDelete(childScope.row.unitId)">删除</el-button>
                      </div>
                    </template>
                  </el-table-column>
                </el-table>
              </template>
            </el-table-column>
            <el-table-column prop="unitName" label="单位名称" />
            <el-table-column prop="unitCode" label="单位编码" />
            <el-table-column prop="contactPerson" label="联系人" />
            <el-table-column prop="phone" label="联系电话" />
            <el-table-column prop="address" label="单位地址" show-overflow-tooltip />
            <el-table-column label="操作" width="260">
              <template #default="scope">
                <div class="operation-buttons">
                  <el-button size="small" type="primary" @click="handleView(scope.row)">查看</el-button>
                  <el-button size="small" type="warning" @click="handleEdit(scope.row)">编辑</el-button>
                  <el-button size="small" type="info" @click="handleAddChild(scope.row)">添加子级</el-button>
                  <el-button size="small" type="danger" @click="handleDelete(scope.row.unitId)">删除</el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="单位名称" prop="unitName">
          <el-input v-model="form.unitName" placeholder="请输入单位名称" />
        </el-form-item>
        <el-form-item label="单位编码" prop="unitCode">
          <el-input v-model="form.unitCode" placeholder="请输入单位编码" />
        </el-form-item>
        <el-form-item label="上级单位">
          <el-select v-model="form.parentId" placeholder="请选择上级单位">
            <el-option label="无" value="" />
            <el-option v-for="unit in parentOptions" :key="unit.unitId" :label="unit.unitName" :value="unit.unitId" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="form.contactPerson" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="单位地址">
          <el-input v-model="form.address" type="textarea" placeholder="请输入单位地址" />
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

    <el-dialog title="查看单位" v-model="viewDialogVisible" width="600px" :close-on-click-modal="false">
      <el-form :model="viewForm" label-width="100px" :disabled="true">
        <el-form-item label="单位名称">
          <el-input v-model="viewForm.unitName" disabled />
        </el-form-item>
        <el-form-item label="单位编码">
          <el-input v-model="viewForm.unitCode" disabled />
        </el-form-item>
        <el-form-item label="上级单位">
          <el-input v-model="viewForm.parentName" disabled />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="viewForm.contactPerson" disabled />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="viewForm.phone" disabled />
        </el-form-item>
        <el-form-item label="单位地址">
          <el-input v-model="viewForm.address" type="textarea" disabled />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="viewForm.isEnabled" :disabled="true" />
        </el-form-item>
        <el-form-item label="创建时间">
          <el-input v-model="viewForm.createdTime" disabled />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getUnitTree, saveUnit, updateUnit, deleteUnit } from '../api/unit'
import { ElMessage, ElMessageBox } from 'element-plus'

const treeData = ref([])
const tableData = ref([])
const treeRef = ref()
const formRef = ref()
const parentOptions = ref([])
const searchKeyword = ref('')
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('新增单位')

const form = reactive({
  unitId: '',
  unitName: '',
  unitCode: '',
  parentId: '',
  contactPerson: '',
  phone: '',
  address: '',
  isEnabled: true
})

const viewForm = reactive({
  unitId: '',
  unitName: '',
  unitCode: '',
  parentName: '',
  contactPerson: '',
  phone: '',
  address: '',
  isEnabled: true,
  createdTime: ''
})

const rules = {
  unitName: [{ required: true, message: '请输入单位名称', trigger: 'blur' }],
  unitCode: [{ required: true, message: '请输入单位编码', trigger: 'blur' }]
}

const loadData = () => {
  getUnitTree().then((res) => {
    if (res.code === 200) {
      treeData.value = res.data
      flattenUnits(res.data)
      convertTreeToTable(res.data)
    }
  })
}

const flattenUnits = (units) => {
  const options = []
  const flatten = (list, level = 0) => {
    list.forEach((unit) => {
      options.push({ ...unit, unitName: '　'.repeat(level) + unit.unitName })
      if (unit.children && unit.children.length > 0) {
        flatten(unit.children, level + 1)
      }
    })
  }
  flatten(units)
  parentOptions.value = options
}

const convertTreeToTable = (units) => {
  const result = []
  const flatten = (list) => {
    list.forEach((unit) => {
      result.push(unit)
      if (unit.children && unit.children.length > 0) {
        flatten(unit.children)
      }
    })
  }
  flatten(units)
  tableData.value = result
}

const handleSearch = () => {
  if (!searchKeyword.value) {
    loadData()
    return
  }
  const keyword = searchKeyword.value.toLowerCase()
  const filterTree = (units) => {
    return units.filter(unit => {
      const match = unit.unitName.toLowerCase().includes(keyword)
      if (unit.children) {
        unit.children = filterTree(unit.children)
      }
      return match || (unit.children && unit.children.length > 0)
    })
  }
  treeData.value = filterTree(JSON.parse(JSON.stringify(treeData.value)))
}

const handleNodeClick = (data) => {
  tableData.value = [data]
  if (data.children && data.children.length > 0) {
    const children = []
    const collect = (list) => {
      list.forEach(item => {
        children.push(item)
        if (item.children) collect(item.children)
      })
    }
    collect(data.children)
    tableData.value = [data, ...children]
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增单位'
  Object.assign(form, { unitId: '', unitName: '', unitCode: '', parentId: '', contactPerson: '', phone: '', address: '', isEnabled: true })
  dialogVisible.value = true
}

const handleAddChild = (row) => {
  dialogTitle.value = '新增子单位'
  Object.assign(form, { unitId: '', unitName: '', unitCode: '', parentId: row.unitId, contactPerson: '', phone: '', address: '', isEnabled: true })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑单位'
  const { children, ...unitData } = row
  Object.assign(form, unitData)
  if (!form.parentId) {
    form.parentId = ''
  }
  dialogVisible.value = true
}

const handleView = (row) => {
  Object.assign(viewForm, row)
  viewForm.parentName = getParentName(row.parentId)
  viewForm.createdTime = row.createdTime ? new Date(row.createdTime).toLocaleString() : ''
  viewDialogVisible.value = true
}

const getParentName = (parentId) => {
  if (!parentId) return '无'
  const find = (units) => {
    for (const unit of units) {
      if (unit.unitId === parentId) return unit.unitName
      if (unit.children) {
        const found = find(unit.children)
        if (found) return found
      }
    }
    return '无'
  }
  return find(treeData.value) || '无'
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该单位吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteUnit(id).then((res) => {
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadData()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    }).catch((error) => {
      ElMessage.error(error.response?.data?.message || '删除失败')
    })
  })
}

const handleSubmit = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      if (form.unitId) {
        updateUnit(form).then((res) => {
          if (res.code === 200) {
            ElMessage.success('修改成功')
            dialogVisible.value = false
            loadData()
          } else {
            ElMessage.error(res.message || '修改失败')
          }
        }).catch((error) => {
          ElMessage.error(error.response?.data?.message || '修改失败')
        })
      } else {
        saveUnit(form).then((res) => {
          if (res.code === 200) {
            ElMessage.success('新增成功')
            dialogVisible.value = false
            loadData()
          } else {
            ElMessage.error(res.message || '新增失败')
          }
        }).catch((error) => {
          ElMessage.error(error.response?.data?.message || '新增失败')
        })
      }
    }
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.unit-container {
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

.unit-content {
  display: flex;
  gap: 20px;
  height: calc(100vh - 180px);
}

.tree-panel {
  width: 320px;
  border-right: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
}

.tree-panel :deep(.el-input) {
  margin-bottom: 12px;
}

.tree-panel :deep(.el-tree) {
  flex: 1;
  overflow-y: auto;
}

.table-panel {
  flex: 1;
  overflow-y: auto;
}

.operation-buttons {
  display: flex;
  gap: 8px;
}
</style>