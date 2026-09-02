import axios from 'axios';
import { ElMessage } from 'element-plus';

const request = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    timeout: 10000,
})

// 请求拦截器
request.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// 响应拦截器
request.interceptors.response.use(
    (response) => {
        // 文件预览需要保留 AxiosResponse，调用方会读取 Blob 和响应头。
        if (response.config.responseType === 'blob') {
            return response
        }

        const body = response.data
        if (body.code !== 1) {
            ElMessage({ message: body.msg || '请求失败', type: 'error', duration: 3000 })
            return Promise.reject(new Error(body.msg))
        }
        return body
    },
    (error) => {
        if (error.response) {
            const status = error.response.status
            const msg = error.response.data?.msg
            if (status === 401) {
                ElMessage({ message: '请先登录后再操作', type: 'error', duration: 3000 })
            } else if (status === 403) {
                ElMessage({ message: msg || '没有操作权限', type: 'error', duration: 3000 })
            } else {
                ElMessage({ message: msg || `服务器错误(${status})`, type: 'error', duration: 3000 })
            }
        } else if (error.code === 'ECONNABORTED') {
            ElMessage({ message: '网络连接超时，请稍后重试', type: 'error', duration: 3000 })
        } else {
            ElMessage({ message: '无法连接到服务器，请检查网络', type: 'error', duration: 3000 })
        }
        return Promise.reject(error);
    }
);

export default request;