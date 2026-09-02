<template>
  <el-dialog :model-value="modelValue" @update:model-value="$emit('update:modelValue', $event)" :title="title" width="360px" :close-on-click-modal="false">
    <div class="confirm-body">
      <el-icon :size="48" color="#F56C6C"><WarningFilled /></el-icon>
      <p>
        {{ message }}<br />
        <b>{{ targetName }}</b> 吗？
      </p>
    </div>
    <template #footer>
      <el-button @click="$emit('cancel')">取消</el-button>
      <el-button type="danger" :loading="loading" @click="$emit('confirm')">删除</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { WarningFilled } from '@element-plus/icons-vue'

defineProps<{
  modelValue: boolean
  title?: string
  message?: string
  targetName: string
  loading?: boolean
}>()

defineEmits<{
  'update:modelValue': [value: boolean]
  confirm: []
  cancel: []
}>()
</script>

<style scoped>
.confirm-body {
  text-align: center;
  padding: 16px 0;
}
.confirm-body p {
  margin-top: 12px;
  font-size: 15px;
  color: #333;
}
</style>
