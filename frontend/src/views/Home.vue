<template>
  <div class="home">
    <h1 class="home-title">青知共享平台</h1>
    <p class="home-tagline">开放共享 · 知识同行</p>
    <div class="home-actions">
      <el-button size="large" class="home-btn-primary" @click="$router.push('/login')">
        登录
      </el-button>
      <el-button size="large" class="home-btn-secondary" @click="$router.push('/register')">
        注册
      </el-button>
    </div>
    <el-button link class="home-guest" @click="guestBrowse">游客浏览</el-button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()
const guestBrowse = () => router.push('/dashboard')
</script>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(135deg, #304156 0%, #409EFF 100%);
  color: #fff;
}

.home-title {
  font-size: 48px;
  font-weight: 300;
  margin-bottom: 8px;
}

.home-tagline {
  font-size: 18px;
  opacity: 0.8;
  margin-bottom: 48px;
}

.home-actions {
  display: flex;
  gap: 20px;
}

.home-btn-primary {
  color: #fff;
  border-color: rgba(255, 255, 255, 0.5);
  background: rgba(255, 255, 255, 0.15);
}

.home-btn-secondary {
  color: #409EFF;
}

.home-guest {
  margin-top: 32px;
  color: rgba(255, 255, 255, 0.6);
}
</style>
