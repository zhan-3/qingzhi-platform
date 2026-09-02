import request from '@/utils/request'
import type { ApiResult } from '@/types/api'

/** 收藏 */
export const addFavorite = (resourceId: number) =>
  request.post<any, ApiResult>(`/favorites/${resourceId}`)

/** 取消收藏 */
export const removeFavorite = (resourceId: number) =>
  request.delete<any, ApiResult>(`/favorites/${resourceId}`)

/** 收藏列表 */
export const getFavorites = () =>
  request.get<any, ApiResult<any[]>>('/favorites')
