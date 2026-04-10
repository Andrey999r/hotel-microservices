import { defineStore } from 'pinia'
import { ref } from 'vue'
import { bookingApi } from '../api/booking.js'

export const useBookingStore = defineStore('booking', () => {
    const rooms = ref([])
    const currentRoom = ref(null)
    const reservations = ref([])
    const popularRooms = ref([])
    const homeData = ref(null)
    const loading = ref(false)
    const error = ref(null)

    async function fetchRooms(params = {}) {
        loading.value = true
        error.value = null
        try {
            const res = await bookingApi.getAllRooms(params)
            rooms.value = res.data
        } catch (e) {
            error.value = e.response?.data?.message || 'Ошибка загрузки номеров'
        } finally {
            loading.value = false
        }
    }

    async function searchRooms(query) {
        loading.value = true
        error.value = null
        try {
            const res = await bookingApi.searchRooms(query)
            rooms.value = res.data
        } catch (e) {
            error.value = e.response?.data?.message || 'Ошибка поиска'
        } finally {
            loading.value = false
        }
    }

    async function fetchRoomById(id) {
        loading.value = true
        error.value = null
        try {
            const res = await bookingApi.getRoomById(id)
            currentRoom.value = res.data
        } catch (e) {
            error.value = e.response?.data?.message || 'Номер не найден'
        } finally {
            loading.value = false
        }
    }

    async function fetchReservations(params = {}) {
        loading.value = true
        error.value = null
        try {
            const res = await bookingApi.getReservations(params)
            reservations.value = res.data
        } catch (e) {
            error.value = e.response?.data?.message || 'Ошибка загрузки бронирований'
        } finally {
            loading.value = false
        }
    }

    /**
     * Создаёт бронирование (статус PENDING) и инициирует оплату через Kafka.
     * Возвращает ReservationDto с paymentUrl для перехода к оплате.
     */
    async function bookReservation(data) {
        loading.value = true
        error.value = null
        try {
            const res = await bookingApi.bookReservation(data)
            reservations.value.unshift(res.data)
            return res.data
        } catch (e) {
            error.value = e.response?.data?.message || 'Ошибка создания бронирования'
            return null
        } finally {
            loading.value = false
        }
    }

    async function cancelReservation(id) {
        loading.value = true
        error.value = null
        try {
            await bookingApi.cancelReservation(id)
            await fetchReservations()
            return true
        } catch (e) {
            error.value = e.response?.data?.message || 'Ошибка отмены бронирования'
            return false
        } finally {
            loading.value = false
        }
    }

    async function fetchHome() {
        loading.value = true
        error.value = null
        try {
            const res = await bookingApi.getHome()
            homeData.value = res.data
            popularRooms.value = res.data.popularRooms || []
        } catch (e) {
            error.value = 'Ошибка загрузки главной страницы'
        } finally {
            loading.value = false
        }
    }

    return {
        rooms, currentRoom, reservations, popularRooms, homeData,
        loading, error,
        fetchRooms, fetchRoomById, fetchReservations,
        bookReservation, cancelReservation, fetchHome,
        searchRooms,
    }
})
