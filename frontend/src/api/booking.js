import axios from 'axios'

const api = axios.create({
    baseURL: '/booking-api',
})

api.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    const login = localStorage.getItem('login')
    const roles = localStorage.getItem('roles')

    if (token) config.headers.Authorization = `Bearer ${token}`
    if (login) config.headers['X-User-Login'] = login
    if (roles) config.headers['X-User-Roles'] = roles

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

export const bookingApi = {
    getAllRooms(params = {}) {
        return api.get('/rooms', { params })
    },
    getRoomById(roomId) {
        return api.get(`/rooms/${roomId}`)
    },
    searchRooms(query) {
        return api.get('/rooms/search', { params: { query } })
    },
    getPopularRooms(pageSize = 6, pageNumber = 0) {
        return api.get('/rooms/popular', { params: { pageSize, pageNumber } })
    },

    createRoom(data) {
        return api.post('/admin/rooms', data)
    },
    createManyRooms(data) {
        return api.post('/admin/rooms/batch', data)
    },
    deleteRoom(roomId) {
        return api.delete(`/admin/rooms/${roomId}`)
    },
    uploadMainPhoto(roomId, file) {
        const formData = new FormData()
        formData.append('file', file)
        return api.post(`/admin/rooms/${roomId}/main`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        })
    },
    uploadGalleryPhotos(roomId, files) {
        const formData = new FormData()
        files.forEach(f => formData.append('files', f))
        return api.post(`/admin/rooms/${roomId}/gallery`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        })
    },
    deletePhoto(roomId, photoId) {
        return api.delete(`/admin/rooms/${roomId}/photos/${photoId}`)
    },

    getReservations(params = {}) {
        return api.get('/reservations', { params })
    },
    getReservationById(id) {
        return api.get(`/reservations/${id}`)
    },

    bookReservation(data) {
        return api.post('/reservations', data)
    },
    cancelReservation(id) {
        return api.delete(`/reservations/${id}`)
    },

    checkAvailability(roomId) {
        return api.get(`/availability/rooms/${roomId}`)
    },
    getAllAvailableRooms(params = {}) {
        return api.get('/availability/rooms', { params })
    },
    getNotAvailableDates(roomId) {
        return api.get(`/availability/reservations/notAvailableDates/${roomId}`)
    },

    getHome() {
        return api.get('/home')
    },
}
