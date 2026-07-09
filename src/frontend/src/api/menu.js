import request from '../utils/request'

export const getMenuTree = () => {
  return request.get('/menu/tree')
}

export const getPermissions = () => {
  return request.get('/menu/permissions')
}

export const getPermissionsByMenuId = (menuId) => {
  return request.get(`/menu/permissions/${menuId}`)
}
