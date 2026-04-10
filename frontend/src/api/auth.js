import axios from 'axios'

const api = axios.create({
    baseURL: '/auth-api',
})

export const authApi = {
    login(login, password, recaptchaToken = '') {
        const headers = {}
        if (recaptchaToken) headers['X-Recaptcha-Token'] = recaptchaToken
        return api.post('/auth/login', { login, password }, { headers })
    },
    register(login, email, password, adminSecret = null, recaptchaToken = '') {
        const headers = {}
        if (adminSecret) headers['X-Admin-Secret'] = adminSecret
        if (recaptchaToken) headers['X-Recaptcha-Token'] = recaptchaToken
        return api.post('/auth/register', { login, email, password }, { headers })
    },
    logout(token) {
        return api.post('/auth/logout', null, {
            headers: { Authorization: `Bearer ${token}` }
        })
    }
}