import request from '@/utils/request'
import type { ApiResult, PageResult } from '@/types/api'

interface User {
  id: number
  username: string
  name: string
  phone?: string
  email?: string
  department?: string
  major?: string
  role: number
  status: number
}

interface ImportResult {
  success: number
  fail: number
  errors: string[]
}

/** 用户列表 */
export const getUserList = (page = 1, pageSize = 10) =>
  request.get<any, ApiResult<PageResult<User>>>(`/admin/users?page=${page}&pageSize=${pageSize}`)

/** 用户详情 */
export const getUserById = (id: number) =>
  request.get<any, ApiResult<User>>(`/admin/users/${id}`)

/** 修改用户 */
export const updateUser = (id: number, data: Partial<User>) =>
  request.put<any, ApiResult>(`/admin/users/${id}`, data)

/** 删除用户 */
export const deleteUser = (id: number) =>
  request.delete<any, ApiResult>(`/admin/users/${id}`)

/** 重置密码 */
export const resetPassword = (id: number, password: string) =>
  request.put<any, ApiResult>(`/admin/users/${id}/reset-password`, { password })

/** 批量导入 */
export const batchImport = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<any, ApiResult<ImportResult>>('/admin/users/import', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
