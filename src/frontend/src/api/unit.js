import request from '../utils/request'

export const getUnitTree = () => {
  return request.get('/unit/tree')
}

export const getUnitList = (params) => {
  return request.get('/unit/list', { params })
}

export const getUnitById = (id) => {
  return request.get(`/unit/${id}`)
}

export const saveUnit = (data) => {
  return request.post('/unit', data)
}

export const updateUnit = (data) => {
  return request.put('/unit', data)
}

export const deleteUnit = (id) => {
  return request.delete(`/unit/${id}`)
}
