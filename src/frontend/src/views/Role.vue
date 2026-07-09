<template>
  <div class="role-container">
    <el-card>
      <div class="card-header">
        <h3>角色管理</h3>
        <el-button type="primary" @click="handleAdd">新增角色</el-button>
      </div>
      <el-table :data="tableData" border>
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="roleCode" label="角色编码" />
        <el-table-column prop="roleDesc" label="角色描述" />
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
              <el-button size="small" type="danger" @click="handleDelete(scope.row.roleId)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="800px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="角色描述" prop="roleDesc">
          <el-input v-model="form.roleDesc" type="textarea" placeholder="请输入角色描述" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.isEnabled" />
        </el-form-item>
        <el-form-item label="权限分配">
          <el-tree
            :data="menuTree"
            :props="{ label: 'menuName', children: 'children' }"
            node-key="menuId"
            ref="treeRef"
            show-checkbox
            default-expand-all
            :check-strictly="false"
            @check-change="handleCheckChange"
          >
            <template #default="{ node, data }">
              <span>{{ data.menuName }}</span>
              <span v-if="data.permissions && data.permissions.length > 0" style="margin-left: 8px;">
                <template v-for="perm in data.permissions" :key="perm.permissionId">
                  <el-checkbox 
                    :model-value="isPermissionChecked(perm.permissionId)" 
                    @change="(val) => togglePermission(perm.permissionId, val)"
                    style="margin-right: 8px;"
                  >
                    {{ perm.permissionName }}
                  </el-checkbox>
                </template>
              </span>
            </template>
          </el-tree>
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
import { getRoleList, saveRole, updateRole, deleteRole, getRolePermissions, saveRolePermissions } from '../api/role'
import { getMenuTree } from '../api/menu'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增角色')
const formRef = ref()
const treeRef = ref()
const menuTree = ref([])
const permissionIds = ref([])

const form = reactive({
  roleId: '',
  roleName: '',
  roleCode: '',
  roleDesc: '',
  isEnabled: true
})

const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

const loadData = () => {
  getRoleList().then((res) => {
    if (res.code === 200) {
      tableData.value = res.data
    }
  })
}

const loadMenuTree = () => {
  getMenuTree().then((res) => {
    if (res.code === 200) {
      menuTree.value = res.data
    }
  })
}

const loadRolePermissions = (roleId) => {
  getRolePermissions(roleId).then((res) => {
    if (res.code === 200) {
      permissionIds.value = res.data
      menuTree.value.forEach(menu => {
        if (menu.permissions) {
          menuPermissionMap.value[menu.menuId] = menu.permissions
            .filter(p => permissionIds.value.includes(p.permissionId))
            .map(p => p.permissionId)
        }
      })
    }
  })
}

const isPermissionChecked = (permissionId) => {
  return permissionIds.value.includes(permissionId)
}

const togglePermission = (permissionId, checked) => {
  const index = permissionIds.value.indexOf(permissionId)
  if (checked && index === -1) {
    permissionIds.value.push(permissionId)
  } else if (!checked && index > -1) {
    permissionIds.value.splice(index, 1)
  }
}

const handleCheckChange = (data, checked, indeterminate) => {
  if (data.permissions && data.permissions.length > 0) {
    const permIds = data.permissions.map(p => p.permissionId)
    if (checked) {
      permIds.forEach(id => {
        if (!permissionIds.value.includes(id)) {
          permissionIds.value.push(id)
        }
      })
    } else {
      permIds.forEach(id => {
        const index = permissionIds.value.indexOf(id)
        if (index > -1) {
          permissionIds.value.splice(index, 1)
        }
      })
    }
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增角色'
  Object.assign(form, { roleId: '', roleName: '', roleCode: '', roleDesc: '', isEnabled: true })
  permissionIds.value = []
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑角色'
  Object.assign(form, row)
  permissionIds.value = []
  loadRolePermissions(row.roleId)
  dialogVisible.value = true
}

const handleDelete = (id) => {
  ElMessage.confirm('确定要删除该角色吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteRole(id).then((res) => {
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
      if (form.roleId) {
        updateRole(form).then((res) => {
          if (res.code === 200) {
            saveRolePermissions(form.roleId, permissionIds.value).then(() => {
              ElMessage.success('修改成功')
              dialogVisible.value = false
              loadData()
            })
          }
        })
      } else {
        saveRole(form).then((res) => {
          if (res.code === 200) {
            saveRolePermissions(form.roleId, permissionIds.value).then(() => {
              ElMessage.success('新增成功')
              dialogVisible.value = false
              loadData()
            })
          }
        })
      }
    }
  })
}

onMounted(() => {
  loadData()
  loadMenuTree()
})
</script>

<style scoped>
.role-container {
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

.operation-buttons {
  display: flex;
  gap: 8px;
}
</style>
