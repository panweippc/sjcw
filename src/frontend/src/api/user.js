import request from '../utils/request'

export const getUserList = (params) => {
  return request.get('/user/list', { params })
}

export const getUserById = (id) => {
  return request.get(`/user/${id}`)
}

export const saveUser = (data) => {
  return request.post('/user', data)
}

export const updateUser = (data) => {
  return request.put('/user', data)
}

export const deleteUser = (id) => {
  return request.delete(`/user/${id}`)
}

export const resetPassword = (data) => {
  return request.put('/user/resetPassword', data)
}
