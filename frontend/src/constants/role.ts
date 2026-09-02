export const Role = {
  STUDENT: 0,
  TEACHER: 1,
  ADMIN: 2,
} as const

export type RoleValue = (typeof Role)[keyof typeof Role]
