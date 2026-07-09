import request from '../utils/request'

export const login = (data) => {
  return request.post('/auth/login', data)
}

export const getInfo = () => {
  return request.get('/auth/getInfo')
}

export const logout = () => {
  return request.post('/auth/logout')
}
