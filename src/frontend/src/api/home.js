import request from '../utils/request'

export const getStatistics = () => {
  return request.get('/home/statistics')
}
