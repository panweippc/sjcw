import request from '../utils/request'

export const getImportRecordList = (params) => {
  return request.get('/import/list', { params })
}

export const getImportRecordById = (id) => {
  return request.get(`/import/${id}`)
}

export const saveImportRecord = (data) => {
  return request.post('/import', data)
}

export const deleteImportRecord = (id) => {
  return request.delete(`/import/${id}`)
}
