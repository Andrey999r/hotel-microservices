<template>
  <div class="page">
    <header class="header">
      <RouterLink to="/dashboard" class="brand-name">StayNova</RouterLink>
      <nav class="header-nav">
        <RouterLink to="/reservations" class="nav-link">Мои бронирования</RouterLink>
        <RouterLink to="/profile" class="nav-link">Профиль</RouterLink>
      </nav>
      <div class="header-right">
        <button class="btn-logout" @click="handleLogout">Выйти</button>
      </div>
    </header>

    <main class="main">
      <!-- STEP 1: Hotel selection -->
      <div class="step-block">
        <div class="step-header">
          <div class="step-num-wrap">
            <span class="step-index">01</span>
          </div>
          <h2 class="step-title">Выберите отель</h2>
        </div>

        <div v-if="hotelsLoading" class="state-msg-sm">Загрузка отелей...</div>
        <div v-else class="hotels-grid">
          <div
            v-for="h in hotels"
            :key="h.id"
            class="hotel-chip"
            :class="{ selected: selectedHotelId === h.id }"
            @click="selectHotel(h)"
          >
            <div class="chip-photo">
              <img
                :src="h.mainPhotoUrl || defaultPhoto"
                :alt="h.name"
                @error="e => e.target.src = defaultPhoto"
              />
              <div v-if="selectedHotelId === h.id" class="chip-selected-mark"></div>
            </div>
            <div class="chip-info">
              <div class="chip-name">{{ h.name }}</div>
              <div class="chip-meta">
                <span v-if="h.country">{{ countryLabel(h.country) }}</span>
                <span v-if="h.averageRating > 0" class="chip-rating">{{ Number(h.averageRating).toFixed(1) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- STEP 2: Rooms -->
      <template v-if="selectedHotelId">
        <div class="step-block">
          <div class="step-header">
            <div class="step-num-wrap">
              <span class="step-index">02</span>
            </div>
            <h2 class="step-title">
              Номера — <span class="step-hotel-name">{{ selectedHotel?.name }}</span>
            </h2>
            <RouterLink :to="`/hotels/${selectedHotelId}`" class="hotel-page-link">
              Страница отеля &#8594;
            </RouterLink>
          </div>

          <div class="filters">
            <input
              v-model="search"
              type="text"
              placeholder="Поиск по названию..."
              class="search-input"
              @input="handleSearch"
            />
            <select v-model="filters.availability" @change="fetchRooms" class="select">
              <option value="">Все статусы</option>
              <option value="AVAILABLE">Доступные</option>
              <option value="UNAVAILABLE">Недоступные</option>
            </select>
            <select v-model="filters.sortByPrice" @change="fetchRooms" class="select">
              <option value="">По популярности</option>
              <option value="asc">Цена: по возрастанию</option>
              <option value="desc">Цена: по убыванию</option>
            </select>
          </div>

          <div v-if="roomsLoading" class="state-msg">Загрузка номеров...</div>
          <div v-else-if="rooms.length === 0" class="state-msg">Номера не найдены</div>

          <div v-else class="rooms-grid">
            <RouterLink
              v-for="room in rooms"
              :key="room.id"
              :to="`/rooms/${room.id}`"
              class="room-card"
            >
              <div class="room-photo">
                <img
                  :src="room.mainPhotoUrl || defaultPhoto"
                  :alt="room.title"
                  @error="e => e.target.src = defaultPhoto"
                />
                <span
                  class="room-badge"
                  :class="room.availabilityStatus === 'AVAILABLE' ? 'badge-available' : 'badge-unavailable'"
                >
                  {{ room.availabilityStatus === 'AVAILABLE' ? 'Доступен' : 'Занят' }}
                </span>
              </div>
              <div class="room-info">
                <div class="room-type">{{ roomTypeLabel(room.roomType) }}</div>
                <div class="room-title">{{ room.title || 'Без названия' }}</div>
                <div class="room-desc">{{ room.description || '' }}</div>
                <div class="room-footer">
                  <span class="room-price" v-if="room.price">
                    {{ room.price }} {{ room.currency || 'RUB' }}
                    <span class="price-per">/ ночь</span>
                  </span>
                  <span class="room-cta">Забронировать &#8594;</span>
                </div>
              </div>
            </RouterLink>
          </div>

          <div class="pagination" v-if="!roomsLoading && rooms.length > 0">
            <button class="page-btn" :disabled="page === 0" @click="prevPage">&#8592; Назад</button>
            <span class="page-info">Страница {{ page + 1 }}</span>
            <button class="page-btn" :disabled="rooms.length < pageSize" @click="nextPage">Вперёд &#8594;</button>
          </div>
        </div>
      </template>

      <div v-else class="step-hint">
        Выберите отель выше, чтобы посмотреть доступные номера
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'
import { hotelApi } from '../api/hotel.js'

const auth = useAuthStore()
const router = useRouter()
const defaultPhoto = 'http://localhost:9000/rooms/default-photo.png'

const hotels = ref([])
const hotelsLoading = ref(false)
const selectedHotelId = ref(null)
const selectedHotel = ref(null)

const rooms = ref([])
const roomsLoading = ref(false)
const search = ref('')
const page = ref(0)
const pageSize = 9
const filters = reactive({ availability: '', sortByPrice: '' })
let searchTimeout = null

const countries = [
  { value: 'RUSSIA', label: 'Россия' }, { value: 'GERMANY', label: 'Германия' },
  { value: 'FRANCE', label: 'Франция' }, { value: 'ITALY', label: 'Италия' },
  { value: 'SPAIN', label: 'Испания' }, { value: 'UAE', label: 'ОАЭ' },
  { value: 'TURKEY', label: 'Турция' }, { value: 'THAILAND', label: 'Таиланд' },
  { value: 'MALDIVES', label: 'Мальдивы' }, { value: 'GEORGIA', label: 'Грузия' },
]
const countryMap = Object.fromEntries(countries.map(c => [c.value, c.label]))
function countryLabel(code) { return countryMap[code] || code }
function roomTypeLabel(type) {
  return { BUSINESS: 'Бизнес', COMMON: 'Стандарт', PRESIDENT: 'Президентский' }[type] || type
}

async function selectHotel(hotel) {
  selectedHotelId.value = hotel.id
  selectedHotel.value = hotel
  page.value = 0
  search.value = ''
  filters.availability = ''
  filters.sortByPrice = ''
  await fetchRooms()
}

async function fetchRooms() {
  if (!selectedHotelId.value) return
  roomsLoading.value = true
  try {
    const query = search.value.trim() || undefined
    const res = await hotelApi.getRooms(selectedHotelId.value, query)
    let result = res.data || []
    if (filters.availability) result = result.filter(r => r.availabilityStatus === filters.availability)
    if (filters.sortByPrice === 'asc') result = [...result].sort((a, b) => (a.price || 0) - (b.price || 0))
    else if (filters.sortByPrice === 'desc') result = [...result].sort((a, b) => (b.price || 0) - (a.price || 0))
    const start = page.value * pageSize
    rooms.value = result.slice(start, start + pageSize)
  } catch { rooms.value = [] }
  finally { roomsLoading.value = false }
}

function handleSearch() {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => { page.value = 0; fetchRooms() }, 400)
}

function prevPage() { page.value--; fetchRooms() }
function nextPage() { page.value++; fetchRooms() }

async function handleLogout() {
  await auth.doLogout()
  router.push('/login')
}

onMounted(async () => {
  hotelsLoading.value = true
  try {
    const res = await hotelApi.filter({ pageSize: 50, pageNumber: 0 })
    hotels.value = res.data || []
  } catch { hotels.value = [] }
  finally { hotelsLoading.value = false }
})
</script>

<style scoped>
.page {
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
}

.brand-name:hover { opacity: 0.8; }

.header-nav {
  display: flex;
  align-items: center;
  gap: 28px;
}

.nav-link {
  font-size: 13px;
  color: rgba(247, 246, 243, 0.5);
  text-decoration: none;
  transition: color var(--transition);
  opacity: 1;
}

.nav-link:hover { color: rgba(247, 246, 243, 0.85); opacity: 1; }

.header-right { display: flex; align-items: center; gap: 16px; }

.btn-logout {
  padding: 8px 20px;
  background: transparent;
  color: rgba(247, 246, 243, 0.6);
  border: 1px solid rgba(247, 246, 243, 0.15);
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  transition: all var(--transition);
  cursor: pointer;
}

.btn-logout:hover { border-color: var(--gold); color: var(--gold); }

.main {
  max-width: 1100px;
  margin: 0 auto;
  padding: 64px 56px;
}

/* ── Steps ── */
.step-block { margin-bottom: 56px; }

.step-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 28px;
  flex-wrap: wrap;
}

.step-num-wrap {
  width: 36px;
  height: 36px;
  border: 1px solid var(--border);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.step-index {
  font-size: 11px;
  color: var(--muted);
  letter-spacing: 1px;
}

.step-title {
  font-family: var(--font-display);
  font-size: 28px;
  font-weight: 300;
  color: var(--dark);
  letter-spacing: -0.01em;
}

.step-hotel-name { color: var(--gold); }

.hotel-page-link {
  font-size: 12px;
  color: var(--muted);
  text-decoration: none;
  margin-left: 4px;
  transition: color var(--transition);
  opacity: 1;
}

.hotel-page-link:hover { color: var(--dark); opacity: 1; }

.state-msg-sm { color: var(--muted); font-size: 13px; }
.state-msg { text-align: center; padding: 60px; color: var(--muted); }

/* ── Hotel chips ── */
.hotels-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

.hotel-chip {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.25s var(--ease-out);
}

.hotel-chip:hover { border-color: rgba(17, 17, 17, 0.3); transform: translateY(-2px); }

.hotel-chip.selected {
  border-color: var(--dark);
  box-shadow: 0 0 0 1px var(--dark);
}

.chip-photo {
  height: 100px;
  position: relative;
  overflow: hidden;
  background: var(--cream);
}

.chip-photo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s var(--ease-out);
}

.hotel-chip:hover .chip-photo img { transform: scale(1.04); }

.chip-selected-mark {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 22px;
  height: 22px;
  background: var(--dark);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.chip-selected-mark::after {
  content: '';
  width: 8px;
  height: 5px;
  border-left: 1.5px solid var(--cream);
  border-bottom: 1.5px solid var(--cream);
  transform: rotate(-45deg) translateY(-1px);
}

.chip-info { padding: 12px 14px; }

.chip-name {
  font-size: 14px;
  font-weight: 500;
  color: var(--dark);
  margin-bottom: 4px;
}

.chip-meta {
  display: flex;
  gap: 8px;
  font-size: 11px;
  color: var(--muted);
}

.chip-rating { color: var(--gold); }

/* ── Filters ── */
.filters {
  display: flex;
  gap: 12px;
  margin-bottom: 28px;
  flex-wrap: wrap;
}

.search-input {
  flex: 1;
  min-width: 200px;
  padding: 11px 16px;
  border: 1px solid var(--border);
  background: var(--surface);
  border-radius: 4px;
  font-size: 14px;
  color: var(--dark);
  outline: none;
  transition: border-color var(--transition);
}

.search-input:focus { border-color: var(--dark); }

.select {
  padding: 11px 16px;
  border: 1px solid var(--border);
  background: var(--surface);
  border-radius: 4px;
  font-size: 13px;
  color: var(--dark);
  outline: none;
  cursor: pointer;
  transition: border-color var(--transition);
}

.select:focus { border-color: var(--dark); }

/* ── Room cards ── */
.rooms-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 40px;
}

.room-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 8px;
  overflow: hidden;
  text-decoration: none;
  color: inherit;
  transition: transform 0.25s var(--ease-out), box-shadow 0.25s var(--ease-out);
  display: block;
  opacity: 1;
}

.room-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 32px rgba(17, 17, 17, 0.07);
  opacity: 1;
}

.room-photo {
  height: 200px;
  background: var(--cream);
  position: relative;
  overflow: hidden;
}

.room-photo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s var(--ease-out);
}

.room-card:hover .room-photo img { transform: scale(1.04); }

.room-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 10px;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.badge-available { background: var(--success-bg); color: var(--success); }
.badge-unavailable { background: var(--error-bg); color: var(--error); }

.room-info { padding: 20px; }

.room-type {
  font-size: 10px;
  letter-spacing: 2px;
  text-transform: uppercase;
  color: var(--gold);
  font-weight: 500;
  margin-bottom: 7px;
}

.room-title {
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: 400;
  color: var(--dark);
  margin-bottom: 8px;
}

.room-desc {
  font-size: 13px;
  color: var(--muted);
  line-height: 1.5;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.room-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.room-price {
  font-size: 17px;
  font-weight: 500;
  color: var(--dark);
}

.price-per {
  font-size: 12px;
  color: var(--muted);
  font-weight: 400;
  margin-left: 3px;
}

.room-cta {
  font-size: 12px;
  color: var(--gold);
  letter-spacing: 0.5px;
}

/* Pagination */
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
}

.page-btn {
  padding: 10px 24px;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 4px;
  font-size: 13px;
  color: var(--dark);
  cursor: pointer;
  transition: all var(--transition);
}

.page-btn:hover:not(:disabled) { border-color: var(--dark); }
.page-btn:disabled { opacity: 0.4; cursor: not-allowed; }

.page-info { font-size: 13px; color: var(--muted); }

.step-hint {
  text-align: center;
  padding: 48px;
  color: var(--muted);
  font-size: 15px;
}

@media (max-width: 900px) {
  .header { padding: 18px 24px; }
  .header-nav { display: none; }
  .main { padding: 40px 24px; }
  .hotels-grid { grid-template-columns: repeat(2, 1fr); }
  .rooms-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 600px) {
  .rooms-grid { grid-template-columns: 1fr; }
}
</style>
