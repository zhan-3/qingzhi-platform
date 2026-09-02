<template>
  <div>
    <el-card>
      <div class="toolbar">
        <el-upload
          :auto-upload="false"
          :limit="1"
          accept=".xlsx,.xls"
          :on-change="handleFileChange"
          :show-file-list="false"
        >
          <el-button type="primary" plain>
            <el-icon><Upload /></el-icon>
            选择文件
          </el-button>
        </el-upload>
        <span class="file-hint" v-if="fileName">{{ fileName }}</span>
        <el-button type="primary" @click="handleImport" :loading="importing" :disabled="!fileRef">
          开始导入
        </el-button>
      </div>
      <el-table :data="users" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="账号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="phone" label="手机" width="130" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="department" label="院系" width="120" />
        <el-table-column prop="major" label="专业" width="120" />
        <el-table-column prop="role" label="角色" width="80">
          <template #default="{ row }">
            <el-tag :type="roleTag(row.role)" size="small">{{ roleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="70">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openEdit(row)">编辑</el-button>
            <el-button type="warning" size="small" @click="openResetPwd(row)">重置密码</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="page" v-model:page-size="pageSize"
          :total="total" :page-sizes="[5, 10, 20]" layout="total, sizes, prev, pager, next"
          @current-change="fetchUsers" @size-change="fetchUsers"
        />
      </div>
    </el-card>

    <el-dialog v-model="editVisible" title="编辑用户" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="姓名"><el-input v-model="editForm.name" /></el-form-item>
        <el-form-item label="手机"><el-input v-model="editForm.phone" /></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="editForm.email" /></el-form-item>
        <el-form-item label="院系"><el-input v-model="editForm.department" /></el-form-item>
        <el-form-item label="专业"><el-input v-model="editForm.major" /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="editForm.role" style="width: 100%">
            <el-option :value="0" label="学生" />
            <el-option :value="1" label="教师" />
            <el-option :value="2" label="管理员" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" style="width: 100%">
            <el-option :value="1" label="正常" />
            <el-option :value="0" label="禁用" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEdit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="pwdVisible" title="重置密码" width="400px">
      <el-form :model="pwdForm" label-width="80px">
        <el-form-item label="新密码">
          <el-input v-model="pwdForm.password" type="password" show-password placeholder="至少8位，包含数字和字母" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdVisible = false">取消</el-button>
        <el-button type="primary" @click="handleResetPwd">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="resultVisible" title="导入结果" width="500px">
      <div class="import-summary">
        <el-tag type="success" size="large" class="import-tag">成功 {{ importResult?.success }} 条</el-tag>
        <el-tag type="danger" size="large" class="import-tag">失败 {{ importResult?.fail }} 条</el-tag>
      </div>
      <div v-if="importResult?.errors?.length" class="import-errors">
        <el-alert
          v-for="(err, i) in importResult.errors"
          :key="i"
          :title="err"
          type="error"
          show-icon
          :closable="false"
          class="import-error-item"
        />
      </div>
      <el-empty v-else description="全部导入成功" :image-size="80" />
      <template #footer>
        <el-button type="primary" @click="resultVisible = false">确定</el-button>
      </template>
    </el-dialog>

    <ConfirmDeleteDialog
      v-model="deleteVisible"
      title="确认删除"
      message="确定要删除用户"
      :target-name="deleteTarget?.name ?? ''"
      :loading="deleting"
      @confirm="confirmDelete"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Upload } from '@element-plus/icons-vue'
import { getUserList, updateUser, deleteUser, resetPassword, batchImport } from '@/api/admin'
import ConfirmDeleteDialog from '@/components/ConfirmDeleteDialog.vue'

const users = ref<any[]>([])
const loading = ref(false)
const importing = ref(false)
const fileName = ref('')
const fileRef = ref<File | null>(null)
const importResult = ref<{ success: number; fail: number; errors: string[] } | null>(null)
const resultVisible = ref(false)

const roleLabel = (role: number) => ['学生', '教师', '管理员'][role] ?? '未知'
const roleTag = (role: number) => (['', '', 'danger'] as const)[role] ?? ''

const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchUsers = async () => {
  loading.value = true
  try {
    const { data } = await getUserList(page.value, pageSize.value)
    users.value = data.records
    total.value = data.total
  } catch {
    /* 拦截器已处理 */
  } finally {
    loading.value = false
  }
}

onMounted(fetchUsers)

const handleFileChange = (file: any) => {
  fileRef.value = file.raw
  fileName.value = file.name
}

const handleImport = async () => {
  if (!fileRef.value) {
    ElMessage({ message: '请先选择文件', type: 'warning' })
    return
  }
  importing.value = true
  try {
    const { data } = await batchImport(fileRef.value)
    importResult.value = data
    resultVisible.value = true
    fileName.value = ''
    fileRef.value = null
    fetchUsers()
  } catch {
    /* 拦截器已处理 */
  } finally {
    importing.value = false
  }
}

const editVisible = ref(false)
const editForm = reactive<any>({})
const editId = ref(0)

const openEdit = (row: any) => {
  editId.value = row.id
  Object.assign(editForm, {
    name: row.name,
    phone: row.phone,
    email: row.email,
    department: row.department,
    major: row.major,
    role: row.role,
    status: row.status,
  })
  editVisible.value = true
}

const handleEdit = async () => {
  try {
    await updateUser(editId.value, { ...editForm })
    ElMessage({ message: '修改成功', type: 'success' })
    editVisible.value = false
    fetchUsers()
  } catch {
    /* 拦截器已处理 */
  }
}

const pwdVisible = ref(false)
const pwdForm = reactive({ password: '' })
const pwdId = ref(0)

const openResetPwd = (row: any) => {
  pwdId.value = row.id
  pwdForm.password = ''
  pwdVisible.value = true
}

const handleResetPwd = async () => {
  try {
    await resetPassword(pwdId.value, pwdForm.password)
    ElMessage({ message: '密码重置成功', type: 'success' })
    pwdVisible.value = false
  } catch {
    /* 拦截器已处理 */
  }
}

const deleteVisible = ref(false)
const deleteTarget = ref<any>(null)
const deleting = ref(false)

const handleDelete = (row: any) => {
  deleteTarget.value = row
  deleteVisible.value = true
}

const confirmDelete = async () => {
  deleting.value = true
  try {
    await deleteUser(deleteTarget.value.id)
    ElMessage({ message: '删除成功', type: 'success' })
    deleteVisible.value = false
    fetchUsers()
  } catch {
    /* 拦截器已处理 */
  } finally {
    deleting.value = false
  }
}
</script>

<style scoped>
.toolbar {
  margin-bottom: 16px;
  display: flex;
  gap: 12px;
  align-items: center;
}

.file-hint {
  color: #999;
  font-size: 13px;
}

.pagination-bar {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.import-summary {
  text-align: center;
  margin-bottom: 16px;
}

.import-tag {
  margin-right: 16px;
}

.import-errors {
  max-height: 300px;
  overflow-y: auto;
}

.import-error-item {
  margin-bottom: 8px;
}

:deep(.el-dialog) {
  border-radius: 12px;
}

:deep(.el-dialog__header) {
  text-align: center;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.el-dialog__footer) {
  display: flex;
  justify-content: center;
  gap: 12px;
}
</style>
