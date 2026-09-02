import request from '@/utils/request'
import type { ApiResult } from '@/types/api'

interface LoginParams {
  username: string
  password: string
}

interface RegisterParams {
  username: string
  password: string
  name?: string
  phone?: string
  email?: string
  department?: string
  major?: string
  role?: number
  status?: number
}

export const login = (data: LoginParams) =>
  request.post<any, ApiResult<string>>('/login', data)

export const register = (data: RegisterParams) =>
  request.post<any, ApiResult<null>>('/register', data)
