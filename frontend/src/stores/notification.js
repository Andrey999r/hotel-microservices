import { defineStore } from 'pinia'
import { ref } from 'vue'
import { notificationApi } from '../api/notification.js'

export const useNotificationStore = defineStore('notification', () => {
    const notifications = ref([])
    const loading = ref(false)
    const error = ref(null)
    const unreadCount = ref(0)

    async function fetchNotifications() {
        loading.value = true
        error.value = null
        try {
            const res = await notificationApi.getAll()
            notifications.value = res.data
            unreadCount.value = res.data.length
        } catch (e) {
            error.value = e.response?.data?.message || 'Ошибка загрузки уведомлений'
        } finally {
            loading.value = false
        }
    }

    async function subscribeEmail(email) {
        loading.value = true
        error.value = null
        try {
            await notificationApi.subscribe({
                channelType: 'EMAIL',
                endpoint: email,
                deviceToken: null
            })
            return true
        } catch (e) {
            error.value = e.response?.data?.message || 'Ошибка подписки'
            return false
        } finally {
            loading.value = false
        }
    }

    async function unsubscribe(channelType) {
        loading.value = true
        error.value = null
        try {
            await notificationApi.unsubscribe(channelType)
            return true
        } catch (e) {
            error.value = e.response?.data?.message || 'Ошибка отписки'
            return false
        } finally {
            loading.value = false
        }
    }

    return { notifications, loading, error, unreadCount, fetchNotifications, subscribeEmail, unsubscribe }
})