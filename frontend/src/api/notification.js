import axios from 'axios'

const api = axios.create({
    baseURL: '/notification-api',
})

api.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) config.headers.Authorization = `Bearer ${token}`
    return config
})

api.interceptors.response.use(
    response => response,
    error => {
        if (error.response?.status === 401) {
            localStorage.removeItem('token')
            localStorage.removeItem('login')
            localStorage.removeItem('roles')
            window.location.href = '/login'
        }
        return Promise.reject(error)
    }
)

export const notificationApi = {
    getAll() {
        return api.get('/notifications')
    },
    subscribe(data) {
        return api.post('/notifications/subscribe', data)
    },
    unsubscribe(channelType) {
        return api.delete('/notifications/unsubscribe', { params: { channelType } })
    }
}