/** 后端统一响应格式 */
export interface ApiResult<T = unknown> {
  code: number
  msg: string
  data: T
}

/** 分页结果 */
export interface PageResult<T> {
  records: T[]
  total: number
  page: number
  pageSize: number
}
