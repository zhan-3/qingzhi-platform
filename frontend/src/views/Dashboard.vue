<template>
  <template v-if="isAdmin">
    <el-row :gutter="20">
      <StatCard label="用户总数" :value="stats.totalUsers" :icon="User" color="#409EFF" />
      <StatCard label="资源总数" :value="stats.totalResources" :icon="Folder" color="#67C23A" />
      <StatCard label="待审核资源" :value="stats.pendingResources" :icon="Warning" color="#E6A23C" />
      <StatCard label="今日上传" :value="stats.todayUploads" :icon="Upload" color="#F56C6C" />
    </el-row>
    <el-card class="section-card">
      <template #header><span class="card-title">待审核资源</span></template>
      <el-table :data="recentResources" stripe>
        <el-table-column prop="title" label="资源标题" />
        <el-table-column prop="uploader" label="上传者" />
        <el-table-column prop="course" label="课程" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="uploadTime" label="上传时间" width="180" />
      </el-table>
    </el-card>
  </template>
  <template v-else>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="所有资源" name="all">
        <el-table :data="publicResources" stripe v-loading="resLoading" empty-text="暂无资源">
          <el-table-column prop="title" label="标题" min-width="160" />
          <el-table-column prop="course" label="课程" width="120" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="resStatusType(row.status)" size="small">{{ resStatusLabel(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="发布时间" width="170" />
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="我的资源" name="mine">
        <el-table :data="myResources" stripe empty-text="你还没有发布过资源">
          <el-table-column prop="title" label="标题" min-width="160" />
          <el-table-column prop="course" label="课程" width="120" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="resStatusType(row.status)" size="small">{{ resStatusLabel(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="发布时间" width="170" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </template>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { User, Folder, Warning, Upload } from '@element-plus/icons-vue'
import { getResourceList } from '@/api/resource'
import StatCard from '@/components/StatCard.vue'

const isAdmin = computed(() => Number(localStorage.getItem('role')) === 2)
const activeTab = ref('all')
const resLoading = ref(false)
const publicResources = ref<any[]>([])
const myResources = ref<any[]>([])

const stats = reactive({ totalUsers: 0, totalResources: 0, pendingResources: 0, todayUploads: 0 })
const recentResources = ref<any[]>([])

const statusTagType = (s: string) => ({ APPROVED: 'success', PENDING: 'warning', REJECTED: 'danger' }[s] ?? 'info')
const statusLabel = (s: string) => ({ APPROVED: '已通过', PENDING: '待审核', REJECTED: '已拒绝' }[s] ?? '未知')
const resStatusType = (s: number) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[s] ?? 'info')
const resStatusLabel = (s: number) => ({ 0: '待审核', 1: '已通过', 2: '已拒绝' }[s] ?? '未知')

onMounted(async () => {
  try {
    const { data } = await getResourceList({ pageSize: 100 })
    publicResources.value = data.records.filter((r: any) => r.status === 1)
    myResources.value = data.records.filter((r: any) => r.userId === Number(localStorage.getItem('userId')))
  } catch {
    /* 拦截器已处理 */
  }
})
</script>

<style scoped>
.section-card {
  margin-top: 20px;
}

.card-title {
  font-weight: bold;
}
</style>
