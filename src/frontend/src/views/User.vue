<template>
  <div class="user-container">
    <el-card>
      <div class="card-header">
        <h3>用户管理</h3>
        <el-button type="primary" @click="handleAdd">新增用户</el-button>
      </div>
      <div class="search-bar">
        <el-input v-model="searchForm.loginName" placeholder="用户名" style="width: 200px" />
        <el-input v-model="searchForm.realName" placeholder="姓名" style="width: 200px" />
        <el-button type="primary" @click="loadData">搜索</el-button>
      </div>
      <el-table :data="tableData" border>
        <el-table-column prop="loginName" label="用户名" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="email" label="邮箱" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="unitName" label="所属单位" />
        <el-table-column prop="isEnabled" label="状态">
          <template #default="scope">
            <el-switch v-model="scope.row.isEnabled" />
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" />
        <el-table-column label="操作" width="260">
          <template #default="scope">
            <div class="operation-buttons">
              <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(scope.row.userId)">删除</el-button>
              <el-button size="small" @click="handleResetPassword(scope.row.userId)">重置密码</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="loginName">
          <el-input v-model="form.loginName" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="所属单位">
          <el-select v-model="form.unitId" placeholder="请选择单位">
            <el-option v-for="unit in unitOptions" :key="unit.unitId" :label="unit.unitName" :value="unit.unitId" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属角色">
          <el-select v-model="form.roleIds" multiple placeholder="请选择角色">
            <el-option v-for="role in roleOptions" :key="role.roleId" :label="role.roleName" :value="role.roleId" />
          </el-select>
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
import { getUserList, saveUser, updateUser, deleteUser, resetPassword } from '../api/user'
import { getUnitList } from '../api/unit'
import { getRoleList } from '../api/role'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const unitOptions = ref([])
const roleOptions = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const formRef = ref()
const searchForm = reactive({ loginName: '', realName: '' })
const form = reactive({
  userId: '',
  loginName: '',
  password: '',
  realName: '',
  email: '',
  phone: '',
  unitId: '',
  isEnabled: true,
  roleIds: []
})

const rules = {
  loginName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

const loadData = () => {
  getUserList(searchForm).then((res) => {
    if (res.code === 200) {
      tableData.value = res.data
    }
  })
}

const loadUnitOptions = () => {
  getUnitList().then((res) => {
    if (res.code === 200) {
      unitOptions.value = res.data
    }
  })
}

const loadRoleOptions = () => {
  getRoleList().then((res) => {
    if (res.code === 200) {
      roleOptions.value = res.data
    }
  })
}

const handleAdd = () => {
  dialogTitle.value = '新增用户'
  Object.assign(form, { userId: '', loginName: '', password: '', realName: '', email: '', phone: '', unitId: '', isEnabled: true, roleIds: [] })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑用户'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (id) => {
  ElMessage.confirm('确定要删除该用户吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteUser(id).then((res) => {
      if (res.code === 200) {
        ElMessage.success('删除成功')
        loadData()
      }
    })
  })
}

const handleResetPassword = (userId) => {
  ElMessage.prompt('请输入新密码', '重置密码', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(({ value }) => {
    resetPassword({ userId, password: value }).then((res) => {
      if (res.code === 200) {
        ElMessage.success('密码重置成功')
      }
    })
  })
}

const handleSubmit = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      if (form.userId) {
        updateUser(form).then((res) => {
          if (res.code === 200) {
            ElMessage.success('修改成功')
            dialogVisible.value = false
            loadData()
          }
        })
      } else {
        saveUser(form).then((res) => {
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

onMounted(() => {
  loadData()
  loadUnitOptions()
  loadRoleOptions()
})
</script>

<style scoped>
.user-container {
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
