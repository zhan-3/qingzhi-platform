import request from '@/utils/request'
import type { ApiResult } from '@/types/api'

interface UserProfile {
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

/** 获取个人信息 */
export const getProfile = () =>
  request.get<any, ApiResult<UserProfile>>('/user/profile')

/** 修改个人信息 */
export const updateProfile = (data: Partial<UserProfile>) =>
  request.put<any, ApiResult>('/user/profile', data)

/** 修改密码 */
export const changePassword = (oldPassword: string, newPassword: string) =>
  request.put<any, ApiResult>('/user/password', { oldPassword, newPassword })
