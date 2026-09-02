<template>
  <div>
    <div class="toolbar">
      <el-button type="primary" @click="openPublish"><el-icon><Plus /></el-icon>发布资源</el-button>
    </div>

    <el-card>
      <el-table :data="resources" stripe v-loading="loading">
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column prop="course" label="课程" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布时间" width="170" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="openPreview(row)">预览</el-button>
            <el-button type="success" size="small" link @click="handleFavorite(row)">
              {{ row.favorited ? '★' : '☆' }}
            </el-button>
            <el-button v-if="row.userId === userId" type="primary" size="small" link @click="openEdit(row)">编辑</el-button>
            <el-button v-if="row.userId === userId" type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑资源' : '发布资源'" width="560px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="课程"><el-input v-model="form.course" /></el-form-item>
        <el-form-item label="文件">
          <el-upload
            :auto-upload="false" :limit="1" :on-change="handleFileChange"
            :show-file-list="false"
            accept=".pdf,.ppt,.pptx,.doc,.docx,.jpg,.jpeg,.png,.gif,.webp"
          >
            <el-button type="primary" plain :loading="uploading"><el-icon><Upload /></el-icon>选择文件</el-button>
          </el-upload>
          <span v-if="uploadFile" class="file-name">{{ uploadFile.name }}</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">{{ editingId ? '保存' : '发布' }}</el-button>
      </template>
    </el-dialog>

    <ConfirmDeleteDialog
      v-model="deleteVisible"
      title="确认删除"
      message="确定要删除资源"
      :target-name="deleteTarget?.title ?? ''"
      :loading="deleting"
      @confirm="confirmDelete"
    />

    <el-dialog
      v-model="previewVisible"
      :title="previewTitle"
      width="80%"
      top="5vh"
      destroy-on-close
      @closed="cleanupPreview"
    >
      <div v-loading="previewLoading" class="preview-content" element-loading-text="正在加载预览…">
        <img
          v-if="previewKind === 'image' && previewUrl"
          :src="previewUrl"
          :alt="previewTitle"
          class="preview-image"
        />
        <iframe
          v-else-if="previewKind === 'document' && previewUrl"
          :src="previewUrl"
          :title="`${previewTitle} 文件预览`"
          class="preview-iframe"
        />
        <el-empty v-else-if="previewError" :description="previewError">
          <el-button type="primary" plain @click="retryPreview">重新加载</el-button>
        </el-empty>
        <el-empty
          v-else-if="previewKind === 'unsupported'"
          description="当前仅支持 PDF 和图片在线预览；PPT、Word 请下载后查看"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onBeforeUnmount, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { Plus, Upload } from '@element-plus/icons-vue'
import { publish, updateResource, deleteResource, getResourceList, getFilePreview } from '@/api/resource'
import { addFavorite, removeFavorite } from '@/api/favorite'
import request from '@/utils/request'
import ConfirmDeleteDialog from '@/components/ConfirmDeleteDialog.vue'

const userId = Number(localStorage.getItem('userId'))

const resources = ref<any[]>([])
const loading = ref(false)

const statusType = (s: number) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[s] ?? 'info')
const statusLabel = (s: number) => ({ 0: '待审核', 1: '已通过', 2: '已拒绝' }[s] ?? '未知')

const fetchResources = async () => {
  loading.value = true
  try {
    const { data } = await getResourceList()
    resources.value = data.records
  } catch {
    /* 拦截器已处理 */
  } finally {
    loading.value = false
  }
}

onMounted(fetchResources)

const dialogVisible = ref(false)
const editingId = ref(0)
const form = reactive({ title: '', description: '', course: '' })
const uploadFile = ref<File | null>(null)
const uploading = ref(false)
const submitting = ref(false)

const openPublish = () => {
  editingId.value = 0
  Object.assign(form, { title: '', description: '', course: '' })
  uploadFile.value = null
  dialogVisible.value = true
}

const openEdit = (row: any) => {
  editingId.value = row.id
  form.title = row.title
  form.description = row.description || ''
  form.course = row.course || ''
  uploadFile.value = null
  dialogVisible.value = true
}

const handleFileChange = (file: any) => {
  uploadFile.value = file.raw
}

const handleSubmit = async () => {
  if (!form.title) {
    ElMessage({ message: '标题不能为空', type: 'warning' })
    return
  }
  if (!editingId.value && !uploadFile.value) {
    ElMessage({ message: '请选择文件', type: 'warning' })
    return
  }

  submitting.value = true
  try {
    let fileId: number | undefined
    if (uploadFile.value) {
      uploading.value = true
      const formData = new FormData()
      formData.append('file', uploadFile.value)
      const { data: fileData } = await request.post('/files/upload', formData)
      fileId = fileData.id
      uploading.value = false
    }

    const dto: any = { title: form.title, description: form.description, course: form.course }
    if (fileId) dto.fileId = fileId
    if (editingId.value) {
      await updateResource(editingId.value, dto)
      ElMessage({ message: '修改成功', type: 'success' })
    } else {
      await publish(dto)
      ElMessage({ message: '发布成功', type: 'success' })
    }
    dialogVisible.value = false
    fetchResources()
  } catch {
    /* 拦截器已处理 */
  } finally {
    submitting.value = false
    uploading.value = false
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
    await deleteResource(deleteTarget.value.id)
    deleteVisible.value = false
    fetchResources()
  } catch {
    /* 拦截器已处理 */
  } finally {
    deleting.value = false
  }
}

const previewVisible = ref(false)
const previewTitle = ref('')
const previewUrl = ref('')
const previewKind = ref<'document' | 'image' | 'unsupported'>('unsupported')
const previewLoading = ref(false)
const previewError = ref('')
const previewTarget = ref<any>(null)
let previewController: AbortController | null = null

const revokePreviewUrl = () => {
  if (previewUrl.value) {
    URL.revokeObjectURL(previewUrl.value)
    previewUrl.value = ''
  }
}

const cleanupPreview = () => {
  previewController?.abort()
  previewController = null
  previewLoading.value = false
  revokePreviewUrl()
}

const detectPreviewKind = (fileName: string) => {
  const extension = fileName.split('.').pop()?.toLowerCase()
  if (extension === 'pdf') return 'document' as const
  if (['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(extension ?? '')) return 'image' as const
  return 'unsupported' as const
}

const loadPreview = async (row: any) => {
  cleanupPreview()
  previewError.value = ''
  previewKind.value = detectPreviewKind(row.fileName || '')
  if (previewKind.value === 'unsupported') return

  previewLoading.value = true
  const controller = new AbortController()
  previewController = controller
  try {
    const response = await getFilePreview(row.fileId, controller.signal)
    const blob = response.data
    const actualKind = blob.type.startsWith('image/')
      ? 'image'
      : blob.type === 'application/pdf'
        ? 'document'
        : 'unsupported'

    if (actualKind === 'unsupported') {
      previewKind.value = actualKind
      previewError.value = '服务器返回的文件类型暂不支持在线预览'
      return
    }

    previewKind.value = actualKind
    previewUrl.value = URL.createObjectURL(blob)
  } catch (error) {
    if (!axios.isCancel(error)) {
      previewError.value = '文件预览加载失败，请检查网络后重试'
    }
  } finally {
    if (previewController === controller) {
      previewLoading.value = false
      previewController = null
    }
  }
}

const openPreview = (row: any) => {
  previewTarget.value = row
  previewTitle.value = row.title
  previewVisible.value = true
  void loadPreview(row)
}

const retryPreview = () => {
  if (previewTarget.value) void loadPreview(previewTarget.value)
}

onBeforeUnmount(cleanupPreview)

const handleFavorite = async (row: any) => {
  try {
    if (row.favorited) {
      await removeFavorite(row.id)
      row.favorited = false
    } else {
      await addFavorite(row.id)
      row.favorited = true
    }
  } catch {
    /* 拦截器已处理 */
  }
}
</script>

<style scoped>
.toolbar {
  margin-bottom: 16px;
}

.file-name {
  margin-left: 8px;
  color: var(--el-text-color-secondary);
  font-size: 14px;
}

.preview-content {
  min-height: 360px;
  height: 70vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: auto;
  background: #f0f2f5;
}

.preview-iframe {
  width: 100%;
  height: 100%;
  border: none;
  background: #fff;
}

.preview-image {
  display: block;
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.preview-content :deep(.el-empty) {
  margin: auto;
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
