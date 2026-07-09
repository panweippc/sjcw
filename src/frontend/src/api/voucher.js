import request from '../utils/request'

export const getYearSummary = (unitId, year) => {
  return request.get('/voucher/year-summary', { params: { unitId, year } })
}

export const getMonthDetail = (unitId, year, month, pageNum, pageSize) => {
  return request.get('/voucher/month-detail', { params: { unitId, year, month, pageNum, pageSize } })
}

export const getAvailableYears = (unitId) => {
  return request.get('/voucher/available-years', { params: { unitId } })
}

export const getVoucherById = (id) => {
  return request.get(`/voucher/${id}`)
}

export const getEntriesByVoucherNo = (voucherNo) => {
  return request.get(`/voucher/entries/${voucherNo}`)
}

export const saveVoucher = (data) => {
  return request.post('/voucher', data)
}

export const updateVoucher = (data) => {
  return request.put('/voucher', data)
}

export const saveVoucherWithEntries = (data) => {
  return request.post('/voucher/with-entries', data)
}

export const updateVoucherWithEntries = (data) => {
  return request.put('/voucher/with-entries', data)
}

export const deleteVoucher = (id) => {
  return request.delete(`/voucher/${id}`)
}