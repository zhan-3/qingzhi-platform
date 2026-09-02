<template>
  <el-col :span="6">
    <el-card shadow="hover">
      <div class="stat-row">
        <div>
          <div class="stat-label">{{ label }}</div>
          <div class="stat-value">{{ value }}</div>
        </div>
        <el-icon :size="40" :color="color"><component :is="icon" /></el-icon>
      </div>
    </el-card>
  </el-col>
</template>

<script setup lang="ts">
import type { Component } from 'vue'

defineProps<{
  label: string
  value: string | number
  icon: Component
  color: string
}>()
</script>

<style scoped>
.stat-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.stat-label {
  color: #999;
  font-size: 14px;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin-top: 8px;
}
</style>
