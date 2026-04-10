import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '../api/auth.js'
import { getRecaptchaToken } from '../composables/useRecaptcha.js'

function parseRolesFromToken(token) {
    try {
        const payload = JSON.parse(atob(token.split('.')[1]))
        return payload.roles || []
    } catch {
        return []
    }
}

export const useAuthStore = defineStore('auth', () => {
    const token = ref(localStorage.getItem('token') || null)
    const login = ref(localStorage.getItem('login') || null)
    const error = ref(null)
    const loading = ref(false)

    const isLoggedIn = computed(() => !!token.value)
    const roles = computed(() => token.value ? parseRolesFromToken(token.value) : [])
    const isAdmin = computed(() => roles.value.includes('ROLE_ADMIN'))

    function setToken(t, l) {
        token.value = t
        login.value = l
        localStorage.setItem('token', t)
        localStorage.setItem('login', l)
    }

    function clearAuth() {
        token.value = null
        login.value = null
        localStorage.removeItem('token')
        localStorage.removeItem('login')
    }

    async function doLogin(loginVal, password) {
        loading.value = true
        error.value = null
        try {
            const recaptchaToken = await getRecaptchaToken('login')
            const res = await authApi.login(loginVal, password, recaptchaToken)
            setToken(res.data, loginVal)
            return true
        } catch (e) {
            error.value = e.response?.data?.message || 'Неверный логин или пароль'
            return false
        } finally {
            loading.value = false
        }
    }

    async function doRegister(loginVal, email, password, adminSecret) {
        loading.value = true
        error.value = null
        try {
            const recaptchaToken = await getRecaptchaToken('register')
            const res = await authApi.register(loginVal, email, password, adminSecret, recaptchaToken)
            setToken(res.data, loginVal)
            return true
        } catch (e) {
            error.value = e.response?.data?.message || 'Ошибка регистрации'
            return false
        } finally {
            loading.value = false
        }
    }

    async function doLogout() {
        loading.value = true
        try {
            await authApi.logout(token.value)
        } catch (e) {
            console.error('Logout error', e)
        } finally {
            clearAuth()
            loading.value = false
        }
    }

    return { token, login, error, loading, isLoggedIn, roles, isAdmin, doLogin, doRegister, doLogout }
})