<template>
  <div class="home">
    <header class="header">
      <span class="brand-name">StayNova</span>
      <div class="header-right">
        <template v-if="auth.isLoggedIn">
          <span class="user-login">{{ auth.login }}</span>
          <RouterLink to="/dashboard" class="btn-header">Личный кабинет</RouterLink>
        </template>
        <template v-else>
          <RouterLink to="/login" class="btn-header-outline">Войти</RouterLink>
          <RouterLink to="/register" class="btn-header">Зарегистрироваться</RouterLink>
        </template>
      </div>
    </header>

    <section class="hero">
      <div class="hero-inner">
        <div class="hero-label">Аренда жилья по всему миру</div>
        <h1 class="hero-title">StayNova</h1>
        <p class="hero-sub">Отели, апартаменты, виллы — найдите идеальное место для отдыха и работы.</p>
        <div class="search-bar">
          <input
            v-model="searchQuery"
            class="search-input"
            type="text"
            placeholder="Поиск по названию, стране, адресу..."
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">Найти</button>
        </div>
      </div>
      <div class="hero-aside">
        <div class="hero-stat">
          <span class="stat-num">500+</span>
          <span class="stat-label">Отелей</span>
        </div>
        <div class="hero-divider"></div>
        <div class="hero-stat">
          <span class="stat-num">10</span>
          <span class="stat-label">Стран</span>
        </div>
        <div class="hero-divider"></div>
        <div class="hero-stat">
          <span class="stat-num">24/7</span>
          <span class="stat-label">Поддержка</span>
        </div>
      </div>
    </section>

    <section class="filter-strip">
      <div class="filter-inner">
        <div class="filter-field">
          <label>Заезд</label>
          <input v-model="filterCheckIn" type="date" :min="today" />
        </div>
        <div class="filter-field">
          <label>Выезд</label>
          <input v-model="filterCheckOut" type="date" :min="filterCheckIn || today" />
        </div>
        <div class="filter-field">
          <label>Гостей</label>
          <input v-model.number="filterGuests" type="number" min="1" max="20" placeholder="1" />
        </div>
        <div class="filter-field">
          <label>Страна</label>
          <select v-model="filterCountry">
            <option value="">Все страны</option>
            <option v-for="c in countries" :key="c.value" :value="c.value">{{ c.label }}</option>
          </select>
        </div>
        <button class="filter-btn" @click="applyFilter">Подобрать</button>
        <button class="filter-reset" v-if="isFiltered" @click="resetFilter">Сбросить</button>
      </div>
    </section>

    <section class="hotels">
      <div class="section-header">
        <div class="section-label">{{ modeLabel }}</div>
        <h2 class="section-title">{{ titleLabel }}</h2>
      </div>

      <div v-if="loading" class="state-msg">Загрузка...</div>
      <div v-else-if="hotels.length === 0" class="state-msg">
        {{ searchMode ? 'Отели не найдены. Попробуйте другой запрос.' : 'Отели пока не добавлены' }}
      </div>

      <div v-else class="hotels-grid">
        <div
          v-for="hotel in hotels"
          :key="hotel.id"
          class="hotel-card"
          @click="goToHotel(hotel.id)"
        >
          <div class="hotel-photo">
            <img
              :src="hotel.mainPhotoUrl || defaultPhoto"
              :alt="hotel.name"
              @error="e => e.target.src = defaultPhoto"
            />
            <span v-if="hotel.country" class="hotel-country">{{ countryLabel(hotel.country) }}</span>
          </div>
          <div class="hotel-info">
            <div class="hotel-rating">
              <span class="stars" :class="{ grey: !hotel.averageRating }">{{ starsOf(hotel.averageRating || 0) }}</span>
              <span class="rating-val" v-if="hotel.averageRating > 0">{{ Number(hotel.averageRating).toFixed(1) }}</span>
              <span class="rating-count">({{ hotel.ratingCount || 0 }})</span>
            </div>
            <div class="hotel-name">{{ hotel.name }}</div>
            <div class="hotel-address" v-if="hotel.address">{{ hotel.address }}</div>
            <div class="hotel-desc">{{ hotel.description || '' }}</div>
            <div class="hotel-footer">
              <button class="btn-view">Подробнее</button>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'
import { hotelApi } from '../api/hotel.js'

const auth = useAuthStore()
const router = useRouter()
const today = new Date().toISOString().split('T')[0]
const defaultPhoto = 'http://localhost:9000/rooms/default-photo.png'

const hotels = ref([])
const loading = ref(false)
const searchQuery = ref('')
const lastQuery = ref('')
const searchMode = ref(false)

const filterCheckIn = ref('')
const filterCheckOut = ref('')
const filterGuests = ref(null)
const filterCountry = ref('')

const isFiltered = computed(() =>
  !!(filterCheckIn.value || filterCheckOut.value || filterGuests.value || filterCountry.value)
)

const modeLabel = computed(() =>
  searchMode.value ? 'Результаты поиска' : (isFiltered.value ? 'Подходящие отели' : 'Популярное')
)
const titleLabel = computed(() =>
  searchMode.value ? `«${lastQuery.value}»` : (isFiltered.value ? 'Отели по вашим параметрам' : 'Популярные отели')
)

const countries = [
  { value: 'RUSSIA', label: 'Россия' },
  { value: 'GERMANY', label: 'Германия' },
  { value: 'FRANCE', label: 'Франция' },
  { value: 'ITALY', label: 'Италия' },
  { value: 'SPAIN', label: 'Испания' },
  { value: 'UAE', label: 'ОАЭ' },
  { value: 'TURKEY', label: 'Турция' },
  { value: 'THAILAND', label: 'Таиланд' },
  { value: 'MALDIVES', label: 'Мальдивы' },
  { value: 'GEORGIA', label: 'Грузия' },
]
const countryMap = Object.fromEntries(countries.map(c => [c.value, c.label]))
function countryLabel(code) { return countryMap[code] || code }
function starsOf(r) { const f = Math.round(r); return '★'.repeat(f) + '☆'.repeat(5 - f) }

async function loadPopular() {
  loading.value = true; searchMode.value = false
  try { const res = await hotelApi.getPopular(0, 9); hotels.value = res.data }
  catch { hotels.value = [] }
  finally { loading.value = false }
}

async function handleSearch() {
  const q = searchQuery.value.trim()
  if (!q) { return loadPopular() }
  loading.value = true; searchMode.value = true; lastQuery.value = q
  try { const res = await hotelApi.search(q); hotels.value = res.data }
  catch { hotels.value = [] }
  finally { loading.value = false }
}

async function applyFilter() {
  loading.value = true; searchMode.value = false
  const params = {}
  if (filterCountry.value) params.country = filterCountry.value
  if (filterCheckIn.value) params.checkIn = filterCheckIn.value
  if (filterCheckOut.value) params.checkOut = filterCheckOut.value
  if (filterGuests.value) params.guests = filterGuests.value
  try { const res = await hotelApi.filter(params); hotels.value = res.data }
  catch { hotels.value = [] }
  finally { loading.value = false }
}

function resetFilter() {
  filterCheckIn.value = ''; filterCheckOut.value = ''
  filterGuests.value = null; filterCountry.value = ''
  searchQuery.value = ''; loadPopular()
}

function goToHotel(id) {
  if (!auth.isLoggedIn) { router.push('/login'); return }
  router.push(`/hotels/${id}`)
}

onMounted(loadPopular)
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: var(--cream);
}

/* ── Header ── */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 22px 64px;
  background: var(--dark);
  position: sticky;
  top: 0;
  z-index: 100;
}

.brand-name {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 300;
  color: var(--cream);
  letter-spacing: 2px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-login {
  font-size: 13px;
  color: rgba(247, 246, 243, 0.5);
  letter-spacing: 0.3px;
}

.btn-header {
  padding: 9px 22px;
  background: var(--gold);
  color: var(--dark);
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  font-weight: 500;
  text-decoration: none;
  transition: opacity var(--transition);
}

.btn-header:hover {
  opacity: 0.85;
}

.btn-header-outline {
  padding: 9px 22px;
  background: transparent;
  color: rgba(247, 246, 243, 0.7);
  border: 1px solid rgba(247, 246, 243, 0.2);
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  text-decoration: none;
  transition: all var(--transition);
}

.btn-header-outline:hover {
  border-color: var(--gold);
  color: var(--gold);
  opacity: 1;
}

/* ── Hero ── */
.hero {
  background: var(--dark);
  padding: 80px 64px 72px;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 48px;
  overflow: hidden;
  position: relative;
}

.hero::before {
  content: '';
  position: absolute;
  top: -120px;
  right: -120px;
  width: 480px;
  height: 480px;
  border-radius: 50%;
  border: 1px solid rgba(201, 168, 76, 0.08);
  pointer-events: none;
}

.hero::after {
  content: '';
  position: absolute;
  top: -60px;
  right: -60px;
  width: 320px;
  height: 320px;
  border-radius: 50%;
  border: 1px solid rgba(201, 168, 76, 0.06);
  pointer-events: none;
}

.hero-inner {
  max-width: 600px;
  z-index: 1;
}

.hero-label {
  font-size: 11px;
  letter-spacing: 3px;
  text-transform: uppercase;
  color: var(--gold);
  margin-bottom: 20px;
}

.hero-title {
  font-family: var(--font-display);
  font-size: 72px;
  font-weight: 300;
  color: var(--cream);
  line-height: 0.95;
  letter-spacing: -0.02em;
  margin-bottom: 20px;
}

.hero-sub {
  font-size: 15px;
  color: rgba(247, 246, 243, 0.45);
  line-height: 1.7;
  margin-bottom: 36px;
  max-width: 460px;
}

.search-bar {
  display: flex;
  max-width: 540px;
}

.search-input {
  flex: 1;
  padding: 15px 20px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(247, 246, 243, 0.15);
  border-right: none;
  border-radius: 4px 0 0 4px;
  color: var(--cream);
  font-size: 14px;
  outline: none;
  transition: border-color var(--transition), background var(--transition);
}

.search-input::placeholder {
  color: rgba(247, 246, 243, 0.3);
}

.search-input:focus {
  border-color: rgba(201, 168, 76, 0.5);
  background: rgba(255, 255, 255, 0.09);
}

.search-btn {
  padding: 15px 28px;
  background: var(--gold);
  color: var(--dark);
  border: none;
  border-radius: 0 4px 4px 0;
  font-size: 11px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  font-weight: 600;
  cursor: pointer;
  transition: opacity var(--transition);
  white-space: nowrap;
}

.search-btn:hover {
  opacity: 0.88;
}

/* Hero aside stats */
.hero-aside {
  display: flex;
  flex-direction: column;
  gap: 0;
  z-index: 1;
  flex-shrink: 0;
}

.hero-stat {
  padding: 20px 40px;
  text-align: center;
}

.stat-num {
  display: block;
  font-family: var(--font-display);
  font-size: 36px;
  font-weight: 300;
  color: var(--cream);
  letter-spacing: -0.02em;
  line-height: 1;
  margin-bottom: 6px;
}

.stat-label {
  display: block;
  font-size: 10px;
  letter-spacing: 2px;
  text-transform: uppercase;
  color: rgba(247, 246, 243, 0.35);
}

.hero-divider {
  height: 1px;
  background: rgba(247, 246, 243, 0.08);
  margin: 0 40px;
}

/* ── Filter strip ── */
.filter-strip {
  background: var(--surface);
  border-bottom: 1px solid var(--border);
  padding: 20px 64px;
}

.filter-inner {
  display: flex;
  align-items: flex-end;
  gap: 20px;
  flex-wrap: wrap;
}

.filter-field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.filter-field label {
  font-size: 10px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  color: var(--muted);
  font-weight: 500;
}

.filter-field input,
.filter-field select {
  padding: 10px 14px;
  border: 1px solid var(--border);
  border-radius: 4px;
  font-size: 13px;
  color: var(--dark);
  background: var(--surface);
  outline: none;
  min-width: 140px;
  transition: border-color var(--transition);
}

.filter-field input:focus,
.filter-field select:focus {
  border-color: var(--gold);
}

.filter-btn {
  padding: 10px 24px;
  background: var(--dark);
  color: var(--cream);
  border: none;
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  font-weight: 500;
  cursor: pointer;
  transition: opacity var(--transition);
  align-self: flex-end;
}

.filter-btn:hover {
  opacity: 0.85;
}

.filter-reset {
  padding: 10px 18px;
  background: transparent;
  color: var(--muted);
  border: 1px solid var(--border);
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 1px;
  text-transform: uppercase;
  cursor: pointer;
  align-self: flex-end;
  transition: all var(--transition);
}

.filter-reset:hover {
  color: var(--dark);
  border-color: var(--dark);
}

/* ── Hotels section ── */
.hotels {
  max-width: 1200px;
  margin: 0 auto;
  padding: 72px 64px;
}

.section-header {
  margin-bottom: 48px;
}

.section-label {
  font-size: 10px;
  letter-spacing: 3px;
  text-transform: uppercase;
  color: var(--gold);
  margin-bottom: 12px;
}

.section-title {
  font-family: var(--font-display);
  font-size: 44px;
  font-weight: 300;
  color: var(--dark);
  letter-spacing: -0.01em;
}

.state-msg {
  text-align: center;
  padding: 80px;
  color: var(--muted);
  font-size: 15px;
}

/* ── Hotel cards ── */
.hotels-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 28px;
}

.hotel-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s var(--ease-out), box-shadow 0.3s var(--ease-out);
}

.hotel-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 16px 40px rgba(17, 17, 17, 0.08);
}

.hotel-photo {
  height: 220px;
  overflow: hidden;
  position: relative;
  background: var(--cream);
}

.hotel-photo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s var(--ease-out);
}

.hotel-card:hover .hotel-photo img {
  transform: scale(1.04);
}

.hotel-country {
  position: absolute;
  bottom: 14px;
  left: 14px;
  background: rgba(17, 17, 17, 0.72);
  color: rgba(247, 246, 243, 0.9);
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 10px;
  letter-spacing: 1px;
  text-transform: uppercase;
}

.hotel-info {
  padding: 22px 24px 26px;
}

.hotel-rating {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 10px;
}

.stars {
  color: var(--gold);
  font-size: 13px;
  letter-spacing: 1px;
}

.stars.grey {
  color: #d4cfc9;
}

.rating-val {
  font-size: 13px;
  font-weight: 500;
  color: var(--dark);
}

.rating-count {
  font-size: 12px;
  color: var(--muted);
}

.hotel-name {
  font-family: var(--font-display);
  font-size: 22px;
  font-weight: 400;
  color: var(--dark);
  margin-bottom: 4px;
  line-height: 1.2;
}

.hotel-address {
  font-size: 12px;
  color: var(--muted);
  margin-bottom: 8px;
}

.hotel-desc {
  font-size: 13px;
  color: var(--muted);
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 40px;
  margin-bottom: 20px;
}

.hotel-footer {
  display: flex;
  justify-content: flex-end;
}

.btn-view {
  padding: 9px 20px;
  background: var(--dark);
  color: var(--cream);
  border: none;
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  cursor: pointer;
  transition: background var(--transition);
}

.btn-view:hover {
  background: var(--gold);
  color: var(--dark);
}

/* Responsive */
@media (max-width: 1100px) {
  .hotels-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 768px) {
  .header { padding: 18px 24px; }
  .hero { padding: 48px 24px 40px; flex-direction: column; align-items: flex-start; gap: 36px; }
  .hero-title { font-size: 52px; }
  .hero-aside { flex-direction: row; }
  .hero-divider { width: 1px; height: auto; margin: 20px 0; }
  .hero-stat { padding: 16px 24px; }
  .filter-strip { padding: 16px 24px; }
  .hotels { padding: 48px 24px; }
  .hotels-grid { grid-template-columns: 1fr; }
  .section-title { font-size: 34px; }
}
</style>
