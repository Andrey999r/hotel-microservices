<template>
  <div class="dashboard">
    <header class="header">
      <RouterLink to="/dashboard" class="brand-name">StayNova</RouterLink>
      <nav class="header-nav">
        <RouterLink to="/dashboard" class="nav-link">Главная</RouterLink>
        <RouterLink to="/admin/hotels" class="nav-link">Отели</RouterLink>
        <RouterLink to="/admin/reservations" class="nav-link">Бронирования</RouterLink>
      </nav>
      <div class="header-right">
        <span class="admin-badge">Администратор</span>
        <button class="btn-logout" @click="handleLogout">Выйти</button>
      </div>
    </header>

    <main class="main">
      <div class="page-header">
        <div class="page-label">Администрирование</div>
        <h1>Управление номерами</h1>
      </div>

      <!-- Step 1: Hotel selector -->
      <div class="hotel-selector card">
        <div class="card-title">Выберите отель</div>
        <div v-if="hotelsLoading" class="loading-sm">Загрузка отелей...</div>
        <div v-else class="hotels-row">
          <button
            v-for="h in hotels"
            :key="h.id"
            class="hotel-chip"
            :class="{ active: selectedHotelId === h.id }"
            @click="selectHotel(h)"
          >
            <img
              :src="h.mainPhotoUrl || defaultRoomPhoto"
              class="hotel-chip-img"
              @error="e => e.target.src = defaultRoomPhoto"
            />
            <span>{{ h.name }}</span>
          </button>
        </div>
        <div v-if="selectedHotel" class="selected-hotel-info">
          <span class="selected-label">Выбран:</span>
          <strong>{{ selectedHotel.name }}</strong>
          <span class="selected-country">{{ selectedHotel.country }}</span>
        </div>
      </div>

      <!-- Step 2: Create form (only after hotel selected) -->
      <div v-if="selectedHotelId" class="page-action-row">
        <button class="btn-primary" @click="showCreateForm = !showCreateForm">
          {{ showCreateForm ? 'Отмена' : '+ Создать номер' }}
        </button>
      </div>

      <div v-if="showCreateForm && selectedHotelId" class="create-form card">
        <div class="card-title">Новый номер — {{ selectedHotel?.name }}</div>
        <div class="form-grid">
          <div class="field">
            <label>Название</label>
            <input v-model="newRoom.title" placeholder="Название номера"/>
          </div>
          <div class="field">
            <label>Тип</label>
            <select v-model="newRoom.roomType">
              <option value="COMMON">Стандарт</option>
              <option value="BUSINESS">Бизнес</option>
              <option value="PRESIDENT">Президентский</option>
            </select>
          </div>
          <div class="field">
            <label>Цена</label>
            <input v-model.number="newRoom.price" type="number" placeholder="0"/>
          </div>
          <div class="field">
            <label>Валюта</label>
            <select v-model="newRoom.currency">
              <option value="RUB">RUB</option>
              <option value="USD">USD</option>
              <option value="EUR">EUR</option>
            </select>
          </div>
          <div class="field">
            <label>Статус</label>
            <select v-model="newRoom.availabilityStatus">
              <option value="AVAILABLE">Доступен</option>
              <option value="UNAVAILABLE">Недоступен</option>
            </select>
          </div>
          <div class="field">
            <label>Гостей макс.</label>
            <input v-model.number="newRoom.maxGuests" type="number" placeholder="1" min="1"/>
          </div>
        </div>

        <div class="field full">
          <label>Описание</label>
          <textarea v-model="newRoom.description" placeholder="Описание номера" rows="3"></textarea>
        </div>

        <div class="field full photo-preview-section">
          <label>Главное фото</label>
          <div class="photo-preview-wrap">
            <img
                :src="photoPreview || defaultRoomPhoto"
                class="photo-preview-img"
                @error="e => e.target.src = defaultRoomPhoto"
            />
            <label class="photo-upload-btn">
              <input type="file" accept="image/*" @change="handleNewRoomPhoto" hidden/>
              {{ photoFile ? photoFile.name : 'Выбрать фото' }}
            </label>
            <button v-if="photoFile" class="photo-clear-btn" @click="clearPhoto">Убрать</button>
          </div>
        </div>

        <div v-if="createError" class="error-msg">{{ createError }}</div>
        <div v-if="createSuccess" class="success-msg">Номер успешно создан</div>

        <button class="btn-primary" @click="handleCreate" :disabled="booking.loading">
          <span v-if="booking.loading" class="spinner"></span>
          <span v-else>Создать</span>
        </button>
      </div>

      <!-- Room list for selected hotel -->
      <div v-if="selectedHotelId">
        <div v-if="booking.loading && !showCreateForm" class="loading">Загрузка...</div>

        <div v-else-if="hotelRooms.length === 0 && !booking.loading" class="empty-rooms">
          У этого отеля пока нет номеров
        </div>

        <div v-else class="rooms-table">
          <div class="table-header">
            <span>ID</span>
            <span>Фото</span>
            <span>Название</span>
            <span>Тип</span>
            <span>Цена</span>
            <span>Статус</span>
            <span>Загрузить фото</span>
            <span>Действия</span>
          </div>
          <div v-for="room in hotelRooms" :key="room.id" class="table-row">
            <span class="cell-id">#{{ room.id }}</span>
            <span>
              <img
                  :src="room.mainPhotoUrl || defaultRoomPhoto"
                  class="room-thumb"
                  @error="e => e.target.src = defaultRoomPhoto"
              />
            </span>
            <span class="cell-title">{{ room.title || '—' }}</span>
            <span>{{ roomTypeLabel(room.roomType) }}</span>
            <span>{{ room.price ? `${room.price} ${room.currency}` : '—' }}</span>
            <span>
              <span class="badge"
                    :class="room.availabilityStatus === 'AVAILABLE' ? 'badge-available' : 'badge-unavailable'">
                {{ room.availabilityStatus === 'AVAILABLE' ? 'Доступен' : 'Занят' }}
              </span>
            </span>
            <span>
              <label class="photo-upload-label" :title="'Загрузить фото для #' + room.id">
                <input type="file" accept="image/*" @change="e => handlePhotoUpload(room.id, e)" hidden/>
                Фото
              </label>
            </span>
            <span>
              <button class="btn-delete" @click="handleDelete(room.id)">Удалить</button>
            </span>
          </div>
        </div>
      </div>

      <div v-else class="no-hotel-hint">
        ↑ Выберите отель, чтобы увидеть и создавать номера
      </div>
    </main>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted, computed} from 'vue'
import {useRouter, RouterLink} from 'vue-router'
import {useAuthStore} from '../../stores/auth.js'
import {useBookingStore} from '../../stores/booking.js'
import {bookingApi} from '../../api/booking.js'
import {hotelApi} from '../../api/hotel.js'

const auth = useAuthStore()
const booking = useBookingStore()
const router = useRouter()

const showCreateForm = ref(false)
const createError = ref(null)
const createSuccess = ref(false)
const photoFile = ref(null)
const photoPreview = ref(null)
const defaultRoomPhoto = 'http://localhost:9000/rooms/default-photo.png'

const hotels = ref([])
const hotelsLoading = ref(false)
const selectedHotelId = ref(null)
const selectedHotel = ref(null)
const hotelRooms = ref([])

const newRoom = reactive({
  title: '',
  description: '',
  roomType: 'COMMON',
  availabilityStatus: 'AVAILABLE',
  price: null,
  currency: 'RUB',
  maxGuests: 1,
  hotelId: null
})

function roomTypeLabel(type) {
  return {BUSINESS: 'Бизнес', COMMON: 'Стандарт', PRESIDENT: 'Президентский'}[type] || type
}

async function selectHotel(hotel) {
  selectedHotelId.value = hotel.id
  selectedHotel.value = hotel
  newRoom.hotelId = hotel.id
  showCreateForm.value = false
  createSuccess.value = false
  createError.value = null
  await loadHotelRooms(hotel.id)
}

async function loadHotelRooms(hotelId) {
  try {
    const res = await hotelApi.getRooms(hotelId)
    hotelRooms.value = res.data || []
  } catch {
    hotelRooms.value = []
  }
}

function handleNewRoomPhoto(e) {
  const file = e.target.files[0]
  if (!file) return
  photoFile.value = file
  photoPreview.value = URL.createObjectURL(file)
}

function clearPhoto() {
  photoFile.value = null
  photoPreview.value = null
}

async function handleCreate() {
  createError.value = null
  createSuccess.value = false

  // Step 1: create the room
  let createdId = null
  try {
    const res = await bookingApi.createRoom({...newRoom})
    createdId = res.data?.id
  } catch (e) {
    createError.value = e.response?.data?.message || 'Ошибка создания номера'
    return
  }

  // Step 2: upload photo (independent — room already exists)
  if (photoFile.value && createdId) {
    try {
      await bookingApi.uploadMainPhoto(createdId, photoFile.value)
    } catch {
      createError.value = 'Номер создан, но фото не загрузилось. Загрузите фото через таблицу.'
    }
  }

  createSuccess.value = true
  showCreateForm.value = false
  clearPhoto()
  Object.assign(newRoom, {
    title: '',
    description: '',
    roomType: 'COMMON',
    availabilityStatus: 'AVAILABLE',
    price: null,
    currency: 'RUB',
    maxGuests: 1,
    hotelId: selectedHotelId.value
  })
  await loadHotelRooms(selectedHotelId.value)
}

async function handleDelete(id) {
  if (!confirm(`Удалить номер #${id}?`)) return
  try {
    await bookingApi.deleteRoom(id)
    await loadHotelRooms(selectedHotelId.value)
  } catch (e) {
    alert(e.response?.data?.message || 'Ошибка удаления')
  }
}

async function handlePhotoUpload(roomId, e) {
  const file = e.target.files[0]
  if (!file) return
  try {
    await bookingApi.uploadMainPhoto(roomId, file)
    await loadHotelRooms(selectedHotelId.value)
  } catch {
    alert('Ошибка загрузки фото')
  }
}

async function handleLogout() {
  await auth.doLogout()
  router.push('/login')
}

onMounted(async () => {
  hotelsLoading.value = true
  try {
    const res = await hotelApi.filter({pageSize: 100, pageNumber: 0})
    hotels.value = res.data || []
  } catch {
    hotels.value = []
  } finally {
    hotelsLoading.value = false
  }
})
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background: var(--cream);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 22px 56px;
  background: var(--dark);
}

.brand-name {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 300;
  color: var(--cream);
  letter-spacing: 2px;
  text-decoration: none;
  opacity: 1;
  transition: opacity var(--transition);
}

.brand-name:hover { opacity: 0.8; }

.header-nav {
  display: flex;
  align-items: center;
  gap: 28px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.nav-link {
  font-size: 13px;
  color: rgba(247, 246, 243, 0.5);
  text-decoration: none;
  transition: color var(--transition);
}

.nav-link:hover { color: rgba(247, 246, 243, 0.85); }

.admin-badge {
  padding: 4px 12px;
  background: transparent;
  color: var(--gold);
  border: 1px solid rgba(201, 168, 76, 0.4);
  border-radius: 20px;
  font-size: 10px;
  font-weight: 500;
  letter-spacing: 1.5px;
  text-transform: uppercase;
}

.btn-logout {
  padding: 8px 20px;
  background: transparent;
  color: rgba(247, 246, 243, 0.6);
  border: 1px solid rgba(247, 246, 243, 0.15);
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  cursor: pointer;
  transition: all var(--transition);
}

.btn-logout:hover {
  border-color: var(--gold);
  color: var(--gold);
}

.main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 56px 56px;
}

.page-header {
  margin-bottom: 40px;
}

.page-label {
  font-size: 10px;
  letter-spacing: 3px;
  text-transform: uppercase;
  color: var(--gold);
  margin-bottom: 10px;
}

.page-header h1 {
  font-family: var(--font-display);
  font-size: 40px;
  font-weight: 300;
  color: var(--dark);
  letter-spacing: -0.01em;
  margin: 0;
}

.page-action-row {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

/* .btn-primary uses global style from style.css */

.card {
  background: white;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 28px;
  margin-bottom: 28px;
}

.card-title {
  font-size: 12px;
  letter-spacing: 2px;
  text-transform: uppercase;
  color: var(--muted);
  font-weight: 500;
  margin-bottom: 20px;
}

/* Hotel selector */
.loading-sm {
  color: var(--muted);
  font-size: 13px;
}

.hotels-row {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.hotel-chip {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  background: var(--cream);
  border: 2px solid var(--border);
  border-radius: 6px;
  font-size: 14px;
  color: var(--dark);
  cursor: pointer;
  transition: all 0.2s;
}

.hotel-chip:hover {
  border-color: var(--gold-light);
}

.hotel-chip.active {
  border-color: var(--gold);
  background: white;
  font-weight: 500;
}

.hotel-chip-img {
  width: 36px;
  height: 36px;
  object-fit: cover;
  border-radius: 4px;
  flex-shrink: 0;
}

.selected-hotel-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  padding-top: 12px;
  border-top: 1px solid var(--border);
}

.selected-label {
  color: var(--muted);
}

.selected-country {
  color: var(--muted);
  font-size: 12px;
}

/* Form */
.form-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.full {
  margin-bottom: 16px;
}

.field label {
  font-size: 11px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  color: var(--muted);
  font-weight: 500;
}

.field input, .field select, .field textarea {
  padding: 12px 14px;
  border: 1px solid var(--border);
  border-radius: 4px;
  font-size: 14px;
  color: var(--dark);
  background: white;
  outline: none;
  font-family: var(--font-body);
  transition: border-color 0.2s;
}

.field input:focus, .field select:focus, .field textarea:focus {
  border-color: var(--gold);
}

.field textarea {
  resize: vertical;
}

.photo-preview-section {
  margin-bottom: 20px;
}

.photo-preview-wrap {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.photo-preview-img {
  width: 140px;
  height: 90px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid var(--border);
}

.photo-upload-btn {
  padding: 9px 18px;
  background: white;
  border: 1px solid var(--border);
  border-radius: 4px;
  font-size: 13px;
  color: var(--dark);
  cursor: pointer;
  transition: border-color 0.2s;
  white-space: nowrap;
}

.photo-upload-btn:hover {
  border-color: var(--gold);
}

.photo-clear-btn {
  padding: 8px 12px;
  background: transparent;
  border: 1px solid #FECACA;
  color: var(--error);
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.photo-clear-btn:hover {
  background: #FEF2F2;
}

/* Table */
.loading {
  text-align: center;
  padding: 60px;
  color: var(--muted);
}

.empty-rooms {
  text-align: center;
  padding: 48px;
  color: var(--muted);
  font-size: 14px;
}

.no-hotel-hint {
  text-align: center;
  padding: 48px;
  color: var(--muted);
  font-size: 15px;
}

.rooms-table {
  background: white;
  border: 1px solid var(--border);
  border-radius: 6px;
  overflow: hidden;
}

.table-header {
  display: grid;
  grid-template-columns: 60px 80px 1fr 110px 130px 110px 120px 100px;
  padding: 14px 20px;
  background: var(--cream);
  border-bottom: 1px solid var(--border);
  font-size: 11px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  color: var(--muted);
  font-weight: 500;
  gap: 12px;
  align-items: center;
}

.table-row {
  display: grid;
  grid-template-columns: 60px 80px 1fr 110px 130px 110px 120px 100px;
  padding: 14px 20px;
  border-bottom: 1px solid var(--border);
  align-items: center;
  font-size: 14px;
  gap: 12px;
  transition: background 0.15s;
}

.table-row:last-child {
  border-bottom: none;
}

.table-row:hover {
  background: #FAFAF8;
}

.cell-id {
  color: var(--muted);
  font-size: 13px;
}

.cell-title {
  font-weight: 500;
}

.room-thumb {
  width: 64px;
  height: 44px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid var(--border);
  display: block;
}

.badge {
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 500;
  white-space: nowrap;
}

.badge-available {
  background: #D1FAE5;
  color: #065F46;
}

.badge-unavailable {
  background: #FEE2E2;
  color: #991B1B;
}

.photo-upload-label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: white;
  border: 1px solid var(--border);
  border-radius: 3px;
  font-size: 12px;
  color: var(--muted);
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.photo-upload-label:hover {
  border-color: var(--gold);
  color: var(--dark);
}

.btn-delete {
  padding: 6px 14px;
  background: transparent;
  border: 1px solid #FECACA;
  color: var(--error);
  border-radius: 3px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-delete:hover {
  background: #FEF2F2;
}

.error-msg {
  padding: 12px;
  background: #FEF2F2;
  border: 1px solid #FECACA;
  border-radius: 4px;
  color: var(--error);
  font-size: 13px;
  margin-bottom: 12px;
}

.success-msg {
  padding: 12px;
  background: #F0FDF4;
  border: 1px solid #BBF7D0;
  border-radius: 4px;
  color: var(--success);
  font-size: 13px;
  margin-bottom: 12px;
}

.spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(245, 240, 232, 0.3);
  border-top-color: var(--cream);
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 900px) {
  .header {
    padding: 16px 24px;
  }

  .main {
    padding: 32px 24px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .table-header, .table-row {
    grid-template-columns: 60px 1fr 100px 100px;
  }

  .table-header span:nth-child(n+5), .table-row span:nth-child(n+5) {
    display: none;
  }
}
</style>
