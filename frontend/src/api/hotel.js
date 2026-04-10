import axios from 'axios'

const api = axios.create({ baseURL: '/booking-api' })

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
  r => r,
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

export const hotelApi = {
  getPopular(page = 0, size = 9) {
    return api.get('/hotels/popular', { params: { page, size } })
  },
  search(query) {
    return api.get('/hotels/search', { params: { query } })
  },
  filter(params = {}) {
    return api.get('/hotels/filter', { params })
  },
  getById(id) {
    return api.get(`/hotels/${id}`)
  },
  getRooms(hotelId, query) {
    return api.get(`/hotels/${hotelId}/rooms`, { params: query ? { query } : {} })
  },
  addRating(hotelId, data) {
    return api.post(`/hotels/${hotelId}/rating`, data)
  },
  getReviews(hotelId) {
    return api.get(`/hotels/${hotelId}/reviews`)
  },
  canReview(hotelId) {
    return api.get(`/hotels/${hotelId}/can-review`)
  },

  // Admin
  createHotel(data) {
    return api.post('/admin/hotels', data)
  },
  updateHotel(id, data) {
    return api.put(`/admin/hotels/${id}`, data)
  },
  deleteHotel(id) {
    return api.delete(`/admin/hotels/${id}`)
  },
  uploadMainPhoto(id, file) {
    const fd = new FormData()
    fd.append('file', file)
    return api.post(`/admin/hotels/${id}/main`, fd, { headers: { 'Content-Type': 'multipart/form-data' } })
  },
  uploadGallery(id, files) {
    const fd = new FormData()
    files.forEach(f => fd.append('files', f))
    return api.post(`/admin/hotels/${id}/gallery`, fd, { headers: { 'Content-Type': 'multipart/form-data' } })
  },
  deleteGalleryPhoto(id, photoIndex) {
    return api.delete(`/admin/hotels/${id}/photo/${photoIndex}`)
  }
}
