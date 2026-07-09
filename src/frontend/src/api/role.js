import request from '../utils/request'

export const getRoleList = (params) => {
  return request.get('/role/list', { params })
}

export const getRoleById = (id) => {
  return request.get(`/role/${id}`)
}

export const saveRole = (data) => {
  return request.post('/role', data)
}

export const updateRole = (data) => {
  return request.put('/role', data)
}

export const deleteRole = (id) => {
  return request.delete(`/role/${id}`)
}

export const getRolePermissions = (roleId) => {
  return request.get(`/role/${roleId}/permissions`)
}

export const saveRolePermissions = (roleId, permissionIds) => {
  return request.put(`/role/${roleId}/permissions`, { permissionIds })
}
