<template>
  <div class="page">
    <header class="header">
      <RouterLink to="/dashboard" class="brand-name">StayNova</RouterLink>
      <nav class="header-nav">
        <RouterLink to="/dashboard" class="nav-link">Главная</RouterLink>
        <RouterLink to="/admin/rooms" class="nav-link">Номера</RouterLink>
        <RouterLink to="/admin/reservations" class="nav-link">Бронирования</RouterLink>
      </nav>
      <div class="header-right">
        <span class="admin-badge">Администратор</span>
      </div>
    </header>

    <main class="main">
      <div class="page-header">
        <div>
          <div class="page-label">Администрирование</div>
          <h1 class="page-title">Управление отелями</h1>
        </div>
        <button class="btn-create" @click="openCreate">+ Новый отель</button>
      </div>

      <!-- Hotels list -->
      <div v-if="loading" class="center-msg">Загрузка...</div>
      <div v-else-if="hotels.length === 0" class="center-msg">Отели не найдены</div>

      <div v-else class="hotels-table">
        <div class="table-header">
          <span>Фото</span>
          <span>Название</span>
          <span>Страна</span>
          <span>Адрес</span>
          <span>Рейтинг</span>
          <span>Действия</span>
        </div>
        <div v-for="hotel in hotels" :key="hotel.id" class="table-row">
          <div class="col-photo">
            <img :src="hotel.mainPhotoUrl || defaultPhoto" :alt="hotel.name"
              @error="e => e.target.src = defaultPhoto" />
          </div>
          <div class="col-name">
            <div class="hotel-name">{{ hotel.name }}</div>
            <div class="hotel-id">ID: {{ hotel.id }}</div>
          </div>
          <div class="col-country">{{ countryLabel(hotel.country) }}</div>
          <div class="col-address">{{ hotel.address || '—' }}</div>
          <div class="col-rating">
            <span class="stars">{{ starsOf(hotel.averageRating || 0) }}</span>
            <span class="rating-num">{{ hotel.averageRating ? Number(hotel.averageRating).toFixed(1) : '—' }}</span>
          </div>
          <div class="col-actions">
            <button class="btn-edit" @click="openEdit(hotel)">Редактировать</button>
            <button class="btn-photos" @click="openPhotos(hotel)">Фото</button>
            <button class="btn-delete" @click="confirmDelete(hotel)">Удалить</button>
          </div>
        </div>
      </div>

      <!-- Pagination -->
      <div class="pagination" v-if="hotels.length >= pageSize">
        <button class="page-btn" :disabled="page === 0" @click="prevPage">← Назад</button>
        <span class="page-num">Стр. {{ page + 1 }}</span>
        <button class="page-btn" @click="nextPage">Вперёд →</button>
      </div>
    </main>

    <!-- CREATE / EDIT MODAL -->
    <div v-if="showForm" class="modal-overlay" @click.self="closeForm">
      <div class="modal">
        <div class="modal-title">{{ editId ? 'Редактировать отель' : 'Новый отель' }}</div>

        <div class="form-group">
          <label>Название *</label>
          <input v-model="form.name" type="text" placeholder="Название отеля" />
        </div>
        <div class="form-group">
          <label>Описание</label>
          <textarea v-model="form.description" rows="3" placeholder="Краткое описание..."></textarea>
        </div>
        <div class="form-group">
          <label>Адрес</label>
          <input v-model="form.address" type="text" placeholder="Улица, город" />
        </div>
        <div class="form-group">
          <label>Страна *</label>
          <select v-model="form.country">
            <option value="">Выберите страну</option>
            <option v-for="c in countries" :key="c.value" :value="c.value">{{ c.label }}</option>
          </select>
        </div>

        <div v-if="formError" class="form-error">{{ formError }}</div>

        <div class="modal-actions">
          <button class="btn-cancel" @click="closeForm">Отмена</button>
          <button class="btn-save" :disabled="formLoading" @click="submitForm">
            {{ formLoading ? 'Сохранение...' : (editId ? 'Сохранить' : 'Создать') }}
          </button>
        </div>
      </div>
    </div>

    <!-- PHOTO MANAGER MODAL -->
    <div v-if="showPhotos" class="modal-overlay" @click.self="closePhotos">
      <div class="modal modal-photos">
        <div class="modal-title">Фото отеля: {{ photoHotel?.name }}</div>

        <div class="photo-section">
          <div class="photo-label">Главное фото</div>
          <div class="main-photo-preview" v-if="photoHotel?.mainPhotoUrl">
            <img :src="photoHotel.mainPhotoUrl" @error="e => e.target.src = defaultPhoto" />
          </div>
          <label class="upload-btn">
            Загрузить главное фото
            <input type="file" accept="image/*" @change="uploadMain" hidden />
          </label>
        </div>

        <div class="photo-section">
          <div class="photo-label">Галерея</div>
          <div class="gallery-grid" v-if="photoHotel?.galleryUrl?.length">
            <div v-for="(url, i) in photoHotel.galleryUrl" :key="i" class="gallery-item">
              <img :src="url" @error="e => e.target.src = defaultPhoto" />
              <button class="gallery-delete" @click="deleteGalleryPhoto(i)" title="Удалить">&times;</button>
            </div>
          </div>
          <div v-else class="empty-gallery">Галерея пуста</div>
          <label class="upload-btn">
            Добавить фото в галерею
            <input type="file" accept="image/*" multiple @change="uploadGallery" hidden />
          </label>
        </div>

        <div v-if="photoError" class="form-error">{{ photoError }}</div>
        <div class="modal-actions">
          <button class="btn-save" @click="closePhotos">Закрыть</button>
        </div>
      </div>
    </div>

    <!-- DELETE CONFIRM -->
    <div v-if="showDeleteConfirm" class="modal-overlay" @click.self="showDeleteConfirm = false">
      <div class="modal modal-sm">
        <div class="modal-title">Удалить отель?</div>
        <p class="delete-msg">«{{ deleteTarget?.name }}» будет удалён. Действие нельзя отменить.</p>
        <div v-if="deleteError" class="form-error">{{ deleteError }}</div>
        <div class="modal-actions">
          <button class="btn-cancel" @click="showDeleteConfirm = false">Отмена</button>
          <button class="btn-delete-confirm" :disabled="deleteLoading" @click="doDelete">
            {{ deleteLoading ? 'Удаление...' : 'Удалить' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { hotelApi } from '../../api/hotel.js'

const defaultPhoto = 'http://localhost:9000/rooms/default-photo.png'
const pageSize = 12
const page = ref(0)
const hotels = ref([])
const loading = ref(false)

// Form state
const showForm = ref(false)
const editId = ref(null)
const formLoading = ref(false)
const formError = ref(null)
const form = ref({ name: '', description: '', address: '', country: '' })

// Photo manager
const showPhotos = ref(false)
const photoHotel = ref(null)
const photoError = ref(null)

// Delete
const showDeleteConfirm = ref(false)
const deleteTarget = ref(null)
const deleteLoading = ref(false)
const deleteError = ref(null)

const countries = [
  { value: 'RUSSIA', label: 'Россия' }, { value: 'GERMANY', label: 'Германия' },
  { value: 'FRANCE', label: 'Франция' }, { value: 'ITALY', label: 'Италия' },
  { value: 'SPAIN', label: 'Испания' }, { value: 'UAE', label: 'ОАЭ' },
  { value: 'TURKEY', label: 'Турция' }, { value: 'THAILAND', label: 'Таиланд' },
  { value: 'MALDIVES', label: 'Мальдивы' }, { value: 'GEORGIA', label: 'Грузия' },
]
const countryMap = Object.fromEntries(countries.map(c => [c.value, c.label]))
function countryLabel(code) { return countryMap[code] || code || '—' }
function starsOf(r) { const f = Math.round(r); return '★'.repeat(f) + '☆'.repeat(5 - f) }

async function loadHotels() {
  loading.value = true
  try {
    const res = await hotelApi.getPopular(page.value, pageSize)
    hotels.value = res.data
  } catch { hotels.value = [] }
  finally { loading.value = false }
}

function prevPage() { if (page.value > 0) { page.value--; loadHotels() } }
function nextPage() { page.value++; loadHotels() }

function openCreate() {
  editId.value = null
  form.value = { name: '', description: '', address: '', country: '' }
  formError.value = null
  showForm.value = true
}

function openEdit(hotel) {
  editId.value = hotel.id
  form.value = {
    name: hotel.name || '',
    description: hotel.description || '',
    address: hotel.address || '',
    country: hotel.country || ''
  }
  formError.value = null
  showForm.value = true
}

function closeForm() { showForm.value = false }

async function submitForm() {
  formError.value = null
  if (!form.value.name.trim()) { formError.value = 'Введите название отеля'; return }
  if (!form.value.country) { formError.value = 'Выберите страну'; return }
  formLoading.value = true
  try {
    if (editId.value) {
      await hotelApi.updateHotel(editId.value, form.value)
    } else {
      await hotelApi.createHotel(form.value)
    }
    closeForm()
    await loadHotels()
  } catch (e) {
    formError.value = e.response?.data?.message || 'Ошибка при сохранении'
  } finally { formLoading.value = false }
}

function openPhotos(hotel) {
  photoHotel.value = { ...hotel }
  photoError.value = null
  showPhotos.value = true
}

function closePhotos() { showPhotos.value = false; loadHotels() }

async function uploadMain(event) {
  const file = event.target.files[0]
  if (!file) return
  photoError.value = null
  try {
    await hotelApi.uploadMainPhoto(photoHotel.value.id, file)
    const res = await hotelApi.getById(photoHotel.value.id)
    photoHotel.value = res.data
  } catch (e) { photoError.value = e.response?.data?.message || 'Ошибка загрузки' }
  event.target.value = ''
}

async function uploadGallery(event) {
  const files = Array.from(event.target.files)
  if (!files.length) return
  photoError.value = null
  try {
    await hotelApi.uploadGallery(photoHotel.value.id, files)
    const res = await hotelApi.getById(photoHotel.value.id)
    photoHotel.value = res.data
  } catch (e) { photoError.value = e.response?.data?.message || 'Ошибка загрузки' }
  event.target.value = ''
}

async function deleteGalleryPhoto(index) {
  photoError.value = null
  try {
    await hotelApi.deleteGalleryPhoto(photoHotel.value.id, index)
    const res = await hotelApi.getById(photoHotel.value.id)
    photoHotel.value = res.data
  } catch (e) { photoError.value = e.response?.data?.message || 'Ошибка удаления' }
}

function confirmDelete(hotel) {
  deleteTarget.value = hotel
  deleteError.value = null
  showDeleteConfirm.value = true
}

async function doDelete() {
  deleteLoading.value = true
  deleteError.value = null
  try {
    await hotelApi.deleteHotel(deleteTarget.value.id)
    showDeleteConfirm.value = false
    await loadHotels()
  } catch (e) {
    deleteError.value = e.response?.data?.message || 'Не удалось удалить отель'
  } finally { deleteLoading.value = false }
}

onMounted(loadHotels)
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: var(--cream);
}

/* ── Header ── */
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
  gap: 20px;
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

/* ── Main ── */
.main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 56px 56px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 48px;
}

.page-label {
  font-size: 10px;
  letter-spacing: 3px;
  text-transform: uppercase;
  color: var(--gold);
  margin-bottom: 10px;
}

.page-title {
  font-family: var(--font-display);
  font-size: 40px;
  font-weight: 300;
  color: var(--dark);
  letter-spacing: -0.01em;
  margin: 0;
}

.btn-create {
  padding: 12px 28px;
  background: var(--dark);
  color: var(--cream);
  border: none;
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 2px;
  text-transform: uppercase;
  cursor: pointer;
  transition: opacity var(--transition);
}

.btn-create:hover { opacity: 0.85; }

.center-msg {
  text-align: center;
  padding: 60px;
  color: var(--muted);
  font-size: 15px;
}

/* ── Table ── */
.hotels-table {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 8px;
  overflow: hidden;
}

.table-header {
  display: grid;
  grid-template-columns: 80px 1fr 120px 1fr 120px 200px;
  gap: 16px;
  padding: 14px 20px;
  background: var(--cream);
  border-bottom: 1px solid var(--border);
  font-size: 10px;
  letter-spacing: 2px;
  text-transform: uppercase;
  color: var(--muted);
  font-weight: 500;
}

.table-row {
  display: grid;
  grid-template-columns: 80px 1fr 120px 1fr 120px 200px;
  gap: 16px;
  padding: 16px 20px;
  align-items: center;
  border-bottom: 1px solid var(--border);
  transition: background var(--transition);
}

.table-row:last-child { border-bottom: none; }
.table-row:hover { background: var(--cream); }

.col-photo img {
  width: 72px;
  height: 52px;
  object-fit: cover;
  border-radius: 4px;
  display: block;
}

.hotel-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--dark);
  margin-bottom: 3px;
}

.hotel-id {
  font-size: 11px;
  color: var(--muted);
}

.col-country,
.col-address {
  font-size: 13px;
  color: var(--dark);
}

.col-address {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.col-rating {
  display: flex;
  align-items: center;
  gap: 6px;
}

.stars {
  color: var(--gold);
  font-size: 13px;
  letter-spacing: 1px;
}

.rating-num {
  font-size: 13px;
  font-weight: 500;
  color: var(--dark);
}

.col-actions {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.btn-edit,
.btn-photos,
.btn-delete {
  padding: 6px 12px;
  border-radius: 3px;
  font-size: 10px;
  letter-spacing: 1px;
  text-transform: uppercase;
  cursor: pointer;
  transition: opacity var(--transition);
}

.btn-edit {
  background: var(--dark);
  color: var(--cream);
  border: none;
}

.btn-edit:hover { opacity: 0.85; }

.btn-photos {
  background: transparent;
  color: var(--dark);
  border: 1px solid rgba(201, 168, 76, 0.5);
}

.btn-photos:hover { background: rgba(201, 168, 76, 0.1); }

.btn-delete {
  background: var(--error-bg);
  color: var(--error);
  border: 1px solid var(--error-border);
}

.btn-delete:hover { background: #FECACA; }

/* ── Pagination ── */
.pagination {
  display: flex;
  align-items: center;
  gap: 20px;
  justify-content: center;
  margin-top: 36px;
}

.page-btn {
  padding: 9px 22px;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  cursor: pointer;
  color: var(--dark);
  transition: all var(--transition);
}

.page-btn:hover:not(:disabled) { border-color: var(--dark); }
.page-btn:disabled { opacity: 0.35; cursor: not-allowed; }

.page-num {
  font-size: 13px;
  color: var(--muted);
}

/* ── Modal ── */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(17, 17, 17, 0.5);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  padding: 20px;
}

.modal {
  background: var(--surface);
  border-radius: 8px;
  padding: 36px;
  width: 100%;
  max-width: 520px;
  max-height: 90vh;
  overflow-y: auto;
  border: 1px solid var(--border);
}

.modal-photos { max-width: 640px; }
.modal-sm { max-width: 400px; }

.modal-title {
  font-family: var(--font-display);
  font-size: 26px;
  font-weight: 300;
  color: var(--dark);
  margin-bottom: 28px;
  letter-spacing: -0.01em;
}

.form-group { margin-bottom: 20px; }

.form-group label {
  display: block;
  font-size: 10px;
  letter-spacing: 2px;
  text-transform: uppercase;
  color: var(--muted);
  font-weight: 500;
  margin-bottom: 8px;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid var(--border);
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  font-family: var(--font-body);
  color: var(--dark);
  box-sizing: border-box;
  transition: border-color var(--transition);
  background: var(--surface);
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  border-color: var(--dark);
}

.form-error {
  padding: 12px 14px;
  background: var(--error-bg);
  border: 1px solid var(--error-border);
  border-radius: 4px;
  color: var(--error);
  font-size: 13px;
  margin-bottom: 16px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 28px;
}

.btn-cancel {
  padding: 10px 20px;
  background: transparent;
  border: 1px solid var(--border);
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  cursor: pointer;
  color: var(--muted);
  transition: all var(--transition);
}

.btn-cancel:hover {
  border-color: var(--dark);
  color: var(--dark);
}

.btn-save {
  padding: 10px 24px;
  background: var(--dark);
  color: var(--cream);
  border: none;
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 2px;
  text-transform: uppercase;
  cursor: pointer;
  transition: opacity var(--transition);
}

.btn-save:hover:not(:disabled) { opacity: 0.85; }
.btn-save:disabled { opacity: 0.5; cursor: not-allowed; }

.btn-delete-confirm {
  padding: 10px 24px;
  background: var(--error);
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 2px;
  text-transform: uppercase;
  cursor: pointer;
  transition: opacity var(--transition);
}

.btn-delete-confirm:hover:not(:disabled) { opacity: 0.85; }

/* ── Photo manager ── */
.photo-section { margin-bottom: 28px; }

.photo-label {
  font-size: 10px;
  letter-spacing: 2px;
  text-transform: uppercase;
  color: var(--muted);
  font-weight: 500;
  margin-bottom: 12px;
}

.main-photo-preview img {
  width: 100%;
  max-height: 200px;
  object-fit: cover;
  border-radius: 4px;
  margin-bottom: 12px;
  border: 1px solid var(--border);
}

.gallery-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  margin-bottom: 12px;
}

.gallery-item {
  position: relative;
  aspect-ratio: 4 / 3;
}

.gallery-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
  display: block;
}

.gallery-delete {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 22px;
  height: 22px;
  background: rgba(185, 28, 28, 0.9);
  color: white;
  border: none;
  border-radius: 50%;
  font-size: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: opacity var(--transition);
}

.gallery-delete:hover { opacity: 0.85; }

.empty-gallery {
  text-align: center;
  padding: 24px;
  color: var(--muted);
  font-size: 13px;
  border: 1px dashed var(--border);
  border-radius: 4px;
  margin-bottom: 12px;
}

.upload-btn {
  display: inline-block;
  padding: 10px 20px;
  background: var(--cream);
  border: 1px solid var(--border);
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  cursor: pointer;
  transition: all var(--transition);
  color: var(--dark);
}

.upload-btn:hover { border-color: var(--dark); }

.delete-msg {
  font-size: 14px;
  color: var(--muted);
  margin-bottom: 24px;
  line-height: 1.7;
}
</style>
