import { defineStore } from 'pinia'
import { ref } from 'vue'
import { userApi } from '../api/user.js'

export const useUserStore = defineStore('user', () => {
    // Profile
    const profile = ref(null)
    const loading = ref(false)
    const error = ref(null)

    async function fetchProfile() {
        loading.value = true
        error.value = null
        try {
            const res = await userApi.getProfile()
            profile.value = res.data
        } catch (e) {
            error.value = e.response?.data?.message || 'Ошибка загрузки профиля'
        } finally {
            loading.value = false
        }
    }

    async function updateProfile(data) {
        loading.value = true
        error.value = null
        try {
            const res = await userApi.updateProfile(data)
            profile.value = res.data
            return true
        } catch (e) {
            error.value = e.response?.data?.message || 'Ошибка обновления профиля'
            return false
        } finally {
            loading.value = false
        }
    }

    async function uploadPhoto(file) {
        loading.value = true
        error.value = null
        try {
            await userApi.uploadPhoto(file)
            await fetchProfile()
            return true
        } catch (e) {
            error.value = e.response?.data?.message || 'Ошибка загрузки фото'
            return false
        } finally {
            loading.value = false
        }
    }

    const adminLoading = ref(false)
    const adminError = ref(null)

    async function fetchAllRoles(pageable = { page: 0, size: 10 }) {
        adminLoading.value = true
        adminError.value = null
        try {
            const res = await userApi.getAllRoles(pageable)
            return res.data
        } catch (e) {
            adminError.value = e.response?.data?.message || 'Ошибка загрузки ролей'
            return []
        } finally {
            adminLoading.value = false
        }
    }

    async function fetchUserRoles(login) {
        adminLoading.value = true
        adminError.value = null
        try {
            const res = await userApi.getUserRoles(login)
            return res.data
        } catch (e) {
            adminError.value = e.response?.data?.message || 'Ошибка загрузки ролей пользователя'
            return []
        } finally {
            adminLoading.value = false
        }
    }

    async function addRoleToUser(login, role) {
        adminLoading.value = true
        adminError.value = null
        try {
            await userApi.addRoleToUser(login, role)
            return true
        } catch (e) {
            adminError.value = e.response?.data?.message || 'Ошибка добавления роли'
            return false
        } finally {
            adminLoading.value = false
        }
    }

    async function addNewRole(role) {
        adminLoading.value = true
        adminError.value = null
        try {
            await userApi.addNewRole(role)
            return true
        } catch (e) {
            adminError.value = e.response?.data?.message || 'Ошибка создания роли'
            return false
        } finally {
            adminLoading.value = false
        }
    }

    return {
        profile,
        loading,
        error,
        adminLoading,
        adminError,
        fetchProfile,
        updateProfile,
        uploadPhoto,
        fetchAllRoles,
        fetchUserRoles,
        addRoleToUser,
        addNewRole
    }
})