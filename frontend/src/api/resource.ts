import request from '@/utils/request'
import type { ApiResult, PageResult } from '@/types/api'

interface Resource {
  id: number
  title: string
  description?: string
  course?: string
  fileId: number
  userId: number
  status: number
  rejectReason?: string
  createdAt: string
  updatedAt: string
}

interface ResourceDTO {
  title: string
  description?: string
  course?: string
  fileId: number
}

/** 发布资源 */
export const publish = (data: ResourceDTO) =>
  request.post<any, ApiResult<Resource>>('/resources', data)

/** 修改资源 */
export const updateResource = (id: number, data: ResourceDTO) =>
  request.put<any, ApiResult<Resource>>(`/resources/${id}`, data)

/** 删除资源 */
export const deleteResource = (id: number) =>
  request.delete<any, ApiResult>(`/resources/${id}`)

/** 资源详情 */
export const getResourceById = (id: number) =>
  request.get<any, ApiResult<Resource>>(`/resources/${id}`)

/** 资源列表 */
export const getResourceList = (params?: { start?: string; end?: string; status?: number; page?: number; pageSize?: number }) =>
  request.get<any, ApiResult<PageResult<Resource>>>('/resources', { params })

/** 获取受 JWT 保护的预览文件 */
export const getFilePreview = (fileId: number, signal?: AbortSignal) =>
  request.get<Blob>(`/files/${fileId}/preview`, {
    responseType: 'blob',
    signal,
  })

/** 管理员 - 查看所有资源 */
export const adminGetAll = (params?: { start?: string; end?: string; status?: number; page?: number; pageSize?: number }) =>
  request.get<any, ApiResult<PageResult<Resource>>>('/admin/resources', { params })

/** 管理员 - 审核资源 */
export const auditResource = (id: number, status: number, reason?: string) =>
  request.put<any, ApiResult>(`/admin/resources/${id}/audit`, { status, reason })

/** 管理员 - 删除资源 */
export const adminDeleteResource = (id: number) =>
  request.delete<any, ApiResult>(`/admin/resources/${id}`)
