import request from '../utils/request'

export const getTemplateList = (params) => {
  return request.get('/template/list', { params })
}

export const getTemplateById = (id) => {
  return request.get(`/template/${id}`)
}

export const saveTemplate = (data) => {
  return request.post('/template', data, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const updateTemplate = (data) => {
  return request.put('/template', data, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const deleteTemplate = (id) => {
  return request.delete(`/template/${id}`)
}
