import axios from 'axios'

const api = axios.create({
    baseURL: '/user-api',
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

export const userApi = {
    getProfile() {
        return api.get('/user/profile')
    },
    updateProfile(data) {
        return api.put('/user/profile', data)
    },
    uploadPhoto(file) {
        const formData = new FormData()
        formData.append('file', file)
        return api.post('/user/photo', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        })
    },

    getAllRoles(pageable) {
        return api.get('/admin/roles', { params: pageable })
    },
    getUserRoles(login) {
        return api.get('/admin/roles/users', { params: { login } })
    },
    addRoleToUser(login, roleName) {
        return api.put('/admin/roles/users', { login, roleName })
    },
    addNewRole(roleName) {
        return api.post('/admin/roles', roleName, {
            headers: { 'Content-Type': 'text/plain' }
        })
    }
}