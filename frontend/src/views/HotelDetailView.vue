<template>
  <div class="page">
    <header class="header">
      <RouterLink to="/" class="brand-name">StayNova</RouterLink>
      <nav class="header-nav">
        <RouterLink to="/" class="nav-link">Все отели</RouterLink>
        <RouterLink to="/reservations" class="nav-link">Мои бронирования</RouterLink>
      </nav>
      <div class="header-right">
        <button class="btn-logout" @click="handleLogout">Выйти</button>
      </div>
    </header>

    <div v-if="loading" class="center-msg">Загрузка...</div>
    <div v-else-if="error" class="center-msg error">{{ error }}</div>

    <main v-else-if="hotel" class="main">
      <!-- Hotel photos + info -->
      <div class="hotel-layout">
        <div class="photos-section">
          <div class="main-photo">
            <img :src="activePhoto || defaultPhoto" alt="Фото отеля"
              @error="e => e.target.src = defaultPhoto" />
          </div>
          <div class="gallery" v-if="allPhotos.length > 1">
            <div v-for="(url, i) in allPhotos" :key="i" class="thumb"
              :class="{ active: activePhoto === url }" @click="activePhoto = url">
              <img :src="url" @error="e => e.target.src = defaultPhoto" />
            </div>
          </div>
        </div>

        <div class="hotel-info">
          <div class="hotel-country" v-if="hotel.country">{{ countryLabel(hotel.country) }}</div>
          <h1 class="hotel-name">{{ hotel.name }}</h1>
          <div class="hotel-address" v-if="hotel.address">{{ hotel.address }}</div>

          <div class="rating-row">
            <span class="stars">{{ starsOf(hotel.averageRating || 0) }}</span>
            <span class="rating-num" v-if="hotel.averageRating > 0">
              {{ Number(hotel.averageRating).toFixed(1) }}
            </span>
            <span class="rating-count">({{ hotel.ratingCount || 0 }} отзывов)</span>
          </div>

          <p class="hotel-desc" v-if="hotel.description">{{ hotel.description }}</p>

          <!-- Rate hotel -->
          <div class="rate-block" v-if="canReviewStatus === 'can'">
            <div class="rate-title">Оставить оценку</div>
            <div class="star-pick">
              <span v-for="s in 5" :key="s" class="star-btn"
                :class="{ active: ratingForm.rating >= s }"
                @click="ratingForm.rating = s">★</span>
            </div>
            <textarea v-model="ratingForm.comment" class="rate-comment"
              placeholder="Ваш комментарий (необязательно)..." rows="3"></textarea>
            <div v-if="ratingError" class="rate-error">{{ ratingError }}</div>
            <div v-if="ratingSuccess" class="rate-success">Спасибо за оценку!</div>
            <button class="btn-rate" :disabled="!ratingForm.rating" @click="submitRating">
              Отправить
            </button>
          </div>
          <div class="already-rated" v-else-if="canReviewStatus === 'done'">
            Вы уже оценили этот отель
          </div>
          <div class="no-stay-hint" v-else-if="canReviewStatus === 'no-stay'">
            Оставить отзыв смогут гости с завершённым подтверждённым проживанием
          </div>
        </div>
      </div>

      <!-- Reviews section -->
      <div class="reviews-section">
        <h2 class="section-title">Отзывы гостей</h2>
        <div v-if="reviewsLoading" class="state-msg">Загрузка отзывов...</div>
        <div v-else-if="reviews.length === 0" class="empty-reviews">
          <div class="empty-line"></div>
          <span>Отзывов пока нет</span>
        </div>
        <div v-else class="reviews-list">
          <div v-for="(review, i) in reviews" :key="i" class="review-card">
            <div class="review-header">
              <div class="review-user">
                <div class="review-avatar">{{ review.userLogin?.charAt(0)?.toUpperCase() }}</div>
                <div>
                  <div class="review-login">{{ review.userLogin }}</div>
                  <div class="review-meta">
                    <span v-if="review.roomTitle && review.roomTitle !== '—'">{{ review.roomTitle }}</span>
                    <span v-if="review.nights > 0"> · {{ review.nights }} {{ nightsLabel(review.nights) }}</span>
                  </div>
                </div>
              </div>
              <div class="review-right">
                <div class="review-stars">{{ starsOf(review.rating) }}</div>
                <div class="review-date">{{ formatDate(review.createdAt) }}</div>
              </div>
            </div>
            <p v-if="review.comment" class="review-comment">{{ review.comment }}</p>
          </div>
        </div>
      </div>

      <!-- Rooms section -->
      <div class="rooms-section">
        <div class="rooms-header">
          <h2 class="rooms-title">Номера отеля</h2>
          <div class="room-search">
            <input v-model="roomQuery" type="text" placeholder="Поиск номеров..."
              class="room-search-input" @keyup.enter="searchRooms" @input="onRoomInput" />
            <button class="room-search-btn" @click="searchRooms">Найти</button>
            <button class="room-search-reset" v-if="roomQuery" @click="clearRoomSearch">Сбросить</button>
          </div>
        </div>

        <div v-if="roomsLoading" class="state-msg">Загрузка номеров...</div>
        <div v-else-if="rooms.length === 0" class="empty-rooms">
          {{ roomQuery ? 'Номера не найдены' : 'У этого отеля пока нет номеров' }}
        </div>
        <div v-else class="rooms-grid">
          <div v-for="room in rooms" :key="room.id" class="room-card"
            @click="goToRoom(room.id)">
            <div class="room-photo">
              <img :src="room.mainPhotoUrl || defaultPhoto" :alt="room.title"
                @error="e => e.target.src = defaultPhoto" />
              <span class="room-badge"
                :class="room.availabilityStatus === 'AVAILABLE' ? 'badge-ok' : 'badge-no'">
                {{ room.availabilityStatus === 'AVAILABLE' ? 'Доступен' : 'Занят' }}
              </span>
            </div>
            <div class="room-info">
              <div class="room-type">{{ roomTypeLabel(room.roomType) }}</div>
              <div class="room-name">{{ room.title }}</div>
              <div class="room-meta" v-if="room.maxGuests">до {{ room.maxGuests }} гостей</div>
              <div class="room-footer">
                <span class="room-price" v-if="room.price">
                  {{ room.price }} {{ room.currency || 'RUB' }}<span class="per"> / ночь</span>
                </span>
                <button class="btn-book">Выбрать</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'
import { hotelApi } from '../api/hotel.js'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()
const defaultPhoto = 'http://localhost:9000/rooms/default-photo.png'

const hotel = ref(null)
const loading = ref(false)
const error = ref(null)
const activePhoto = ref(null)
const rooms = ref([])
const roomsLoading = ref(false)
const roomQuery = ref('')
// 'can' = eligible to review | 'done' = already reviewed | 'no-stay' = no completed stay | '' = unknown
const canReviewStatus = ref('')
const ratingForm = ref({ rating: 0, comment: '' })
const ratingError = ref(null)
const ratingSuccess = ref(false)
const reviews = ref([])
const reviewsLoading = ref(false)

const allPhotos = computed(() => {
  if (!hotel.value) return []
  const photos = []
  if (hotel.value.mainPhotoUrl) photos.push(hotel.value.mainPhotoUrl)
  if (hotel.value.galleryUrl) photos.push(...hotel.value.galleryUrl.filter(u => u !== hotel.value.mainPhotoUrl))
  return photos
})

const countries = [
  { value: 'RUSSIA', label: 'Россия' }, { value: 'GERMANY', label: 'Германия' },
  { value: 'FRANCE', label: 'Франция' }, { value: 'ITALY', label: 'Италия' },
  { value: 'SPAIN', label: 'Испания' }, { value: 'UAE', label: 'ОАЭ' },
  { value: 'TURKEY', label: 'Турция' }, { value: 'THAILAND', label: 'Таиланд' },
  { value: 'MALDIVES', label: 'Мальдивы' }, { value: 'GEORGIA', label: 'Грузия' },
]
const countryMap = Object.fromEntries(countries.map(c => [c.value, c.label]))
function countryLabel(code) { return countryMap[code] || code }
function starsOf(r) { const f = Math.round(r); return '★'.repeat(f) + '☆'.repeat(5 - f) }
function roomTypeLabel(t) { return { BUSINESS: 'Бизнес', COMMON: 'Стандарт', PRESIDENT: 'Президентский' }[t] || t }
function nightsLabel(n) { if (n >= 11 && n <= 14) return 'ночей'; const m = n % 10; if (m === 1) return 'ночь'; if (m >= 2 && m <= 4) return 'ночи'; return 'ночей' }
function formatDate(dt) { if (!dt) return ''; const d = new Date(dt); return d.toLocaleDateString('ru-RU', { day: '2-digit', month: 'long', year: 'numeric' }) }

async function loadHotel() {
  loading.value = true
  try {
    const res = await hotelApi.getById(route.params.id)
    hotel.value = res.data
    activePhoto.value = allPhotos.value[0] || null
    rooms.value = hotel.value.rooms || []
    if (rooms.value.length === 0) await loadRooms()
    await Promise.all([loadReviews(), checkCanReview()])
  } catch (e) {
    error.value = 'Отель не найден'
  } finally {
    loading.value = false
  }
}

async function checkCanReview() {
  try {
    const res = await hotelApi.canReview(route.params.id)
    if (res.data === true) {
      canReviewStatus.value = 'can'
    } else {
      // false from canReview: either already reviewed or no stay
      // check if already reviewed via the reviews list (will be loaded)
      canReviewStatus.value = 'no-stay'
    }
  } catch {
    canReviewStatus.value = 'no-stay'
  }
}

async function loadReviews() {
  reviewsLoading.value = true
  try {
    const res = await hotelApi.getReviews(route.params.id)
    reviews.value = res.data
    // If canReview returned false but the user's login is in reviews → already reviewed
    const myLogin = localStorage.getItem('login')
    if (canReviewStatus.value === 'no-stay' && myLogin) {
      const alreadyReviewed = reviews.value.some(r => r.userLogin === myLogin)
      if (alreadyReviewed) canReviewStatus.value = 'done'
    }
  } catch {
    reviews.value = []
  } finally {
    reviewsLoading.value = false
  }
}

async function loadRooms(query) {
  roomsLoading.value = true
  try {
    const res = await hotelApi.getRooms(route.params.id, query)
    rooms.value = res.data
  } catch {
    rooms.value = []
  } finally {
    roomsLoading.value = false
  }
}

async function searchRooms() {
  await loadRooms(roomQuery.value.trim() || undefined)
}

function onRoomInput() {
  if (!roomQuery.value) loadRooms()
}

function clearRoomSearch() {
  roomQuery.value = ''
  loadRooms()
}

async function submitRating() {
  ratingError.value = null
  try {
    await hotelApi.addRating(route.params.id, {
      rating: ratingForm.value.rating,
      comment: ratingForm.value.comment || null
    })
    ratingSuccess.value = true
    canReviewStatus.value = 'done'
    // Refresh hotel to update average and reload reviews
    const res = await hotelApi.getById(route.params.id)
    hotel.value = { ...hotel.value, averageRating: res.data.averageRating, ratingCount: res.data.ratingCount }
    await loadReviews()
  } catch (e) {
    const msg = e.response?.data?.message || 'Не удалось отправить оценку'
    ratingError.value = msg
    if (e.response?.status === 409) alreadyRated.value = true
  }
}

function goToRoom(id) {
  router.push(`/rooms/${id}`)
}

async function handleLogout() {
  await auth.doLogout()
  router.push('/login')
}

onMounted(loadHotel)
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

.nav-link:hover { color: rgba(247, 246, 243, 0.85); }

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
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
  transition: all var(--transition);
  cursor: pointer;
}

.btn-logout:hover {
  border-color: var(--gold);
  color: var(--gold);
}

/* ── States ── */
.center-msg {
  text-align: center;
  padding: 80px;
  color: var(--muted);
  font-size: 15px;
}

.center-msg.error { color: var(--error); }

.state-msg {
  text-align: center;
  padding: 40px;
  color: var(--muted);
  font-size: 14px;
}

/* ── Main ── */
.main {
  max-width: 1100px;
  margin: 0 auto;
  padding: 64px 56px;
}

/* ── Hotel layout ── */
.hotel-layout {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 56px;
  margin-bottom: 72px;
}

.main-photo {
  width: 100%;
  aspect-ratio: 16 / 10;
  border-radius: 4px;
  overflow: hidden;
  background: var(--border);
}

.main-photo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition);
}

.gallery {
  display: flex;
  gap: 8px;
  margin-top: 10px;
  flex-wrap: wrap;
}

.thumb {
  width: 72px;
  height: 52px;
  border-radius: 3px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: border-color var(--transition);
}

.thumb.active { border-color: var(--gold); }

.thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* ── Hotel info ── */
.hotel-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.hotel-country {
  font-size: 10px;
  letter-spacing: 3px;
  text-transform: uppercase;
  color: var(--gold);
  font-weight: 500;
}

.hotel-name {
  font-family: var(--font-display);
  font-size: 36px;
  font-weight: 300;
  line-height: 1.2;
  color: var(--dark);
  letter-spacing: -0.01em;
  margin: 0;
}

.hotel-address {
  font-size: 13px;
  color: var(--muted);
  line-height: 1.5;
}

.rating-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stars {
  color: var(--gold);
  font-size: 16px;
  letter-spacing: 2px;
}

.rating-num {
  font-size: 17px;
  font-weight: 600;
  color: var(--dark);
}

.rating-count {
  font-size: 13px;
  color: var(--muted);
}

.hotel-desc {
  font-size: 14px;
  color: var(--muted);
  line-height: 1.8;
  margin: 0;
}

/* ── Rate block ── */
.rate-block {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.rate-title {
  font-size: 10px;
  letter-spacing: 2.5px;
  text-transform: uppercase;
  color: var(--muted);
  font-weight: 500;
}

.star-pick {
  display: flex;
  gap: 4px;
}

.star-btn {
  font-size: 26px;
  cursor: pointer;
  color: var(--border);
  transition: color var(--transition);
  line-height: 1;
}

.star-btn.active,
.star-btn:hover { color: var(--gold); }

.rate-comment {
  padding: 10px 12px;
  border: 1px solid var(--border);
  border-radius: 4px;
  font-size: 14px;
  resize: vertical;
  outline: none;
  font-family: var(--font-body);
  color: var(--dark);
  transition: border-color var(--transition);
}

.rate-comment:focus { border-color: var(--dark); }

.rate-error {
  font-size: 13px;
  color: var(--error);
}

.rate-success {
  font-size: 13px;
  color: var(--success);
  padding: 10px 12px;
  background: var(--success-bg);
  border: 1px solid var(--success-border);
  border-radius: 4px;
}

.btn-rate {
  padding: 11px;
  background: var(--dark);
  color: var(--cream);
  border: none;
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 2px;
  text-transform: uppercase;
  font-weight: 500;
  cursor: pointer;
  transition: opacity var(--transition);
}

.btn-rate:hover:not(:disabled) { opacity: 0.85; }
.btn-rate:disabled { opacity: 0.45; cursor: not-allowed; }

.already-rated {
  font-size: 13px;
  color: var(--success);
  padding: 12px;
  background: var(--success-bg);
  border: 1px solid var(--success-border);
  border-radius: 4px;
}

.no-stay-hint {
  font-size: 13px;
  color: var(--muted);
  padding: 12px;
  background: var(--cream);
  border: 1px solid var(--border);
  border-radius: 4px;
  line-height: 1.6;
}

/* ── Reviews section ── */
.reviews-section {
  border-top: 1px solid var(--border);
  padding-top: 56px;
  margin-bottom: 56px;
}

.section-title {
  font-family: var(--font-display);
  font-size: 32px;
  font-weight: 300;
  color: var(--dark);
  letter-spacing: -0.01em;
  margin: 0 0 32px;
}

.empty-reviews {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 40px 0;
  color: var(--muted);
  font-size: 14px;
}

.empty-line {
  width: 40px;
  height: 1px;
  background: var(--border);
  flex-shrink: 0;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 0;
  border: 1px solid var(--border);
  border-radius: 6px;
  overflow: hidden;
}

.review-card {
  background: var(--surface);
  padding: 24px 28px;
  border-bottom: 1px solid var(--border);
  transition: background var(--transition);
}

.review-card:last-child { border-bottom: none; }
.review-card:hover { background: var(--cream); }

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  gap: 16px;
}

.review-user {
  display: flex;
  align-items: center;
  gap: 14px;
}

.review-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: var(--dark);
  color: var(--cream);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  font-weight: 500;
  flex-shrink: 0;
}

.review-login {
  font-weight: 500;
  font-size: 14px;
  color: var(--dark);
  margin-bottom: 3px;
}

.review-meta {
  font-size: 12px;
  color: var(--muted);
}

.review-right { text-align: right; flex-shrink: 0; }

.review-stars {
  color: var(--gold);
  font-size: 14px;
  letter-spacing: 2px;
  margin-bottom: 4px;
}

.review-date {
  font-size: 11px;
  color: var(--muted);
}

.review-comment {
  font-size: 14px;
  color: var(--muted);
  line-height: 1.7;
  margin: 0;
}

/* ── Rooms section ── */
.rooms-section {
  border-top: 1px solid var(--border);
  padding-top: 56px;
}

.rooms-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 36px;
  flex-wrap: wrap;
  gap: 20px;
}

.rooms-title {
  font-family: var(--font-display);
  font-size: 32px;
  font-weight: 300;
  color: var(--dark);
  letter-spacing: -0.01em;
  margin: 0;
}

.room-search {
  display: flex;
  gap: 0;
  align-items: center;
}

.room-search-input {
  padding: 10px 14px;
  border: 1px solid var(--border);
  border-right: none;
  border-radius: 4px 0 0 4px;
  font-size: 13px;
  outline: none;
  min-width: 200px;
  color: var(--dark);
  background: var(--surface);
  transition: border-color var(--transition);
}

.room-search-input:focus { border-color: var(--dark); }

.room-search-btn {
  padding: 10px 18px;
  background: var(--dark);
  color: var(--cream);
  border: none;
  font-size: 11px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  cursor: pointer;
  transition: opacity var(--transition);
}

.room-search-btn:hover { opacity: 0.85; }

.room-search-reset {
  padding: 10px 14px;
  background: transparent;
  color: var(--muted);
  border: 1px solid var(--border);
  border-left: none;
  border-radius: 0 4px 4px 0;
  font-size: 11px;
  letter-spacing: 1px;
  text-transform: uppercase;
  cursor: pointer;
  transition: all var(--transition);
}

.room-search-reset:hover {
  color: var(--dark);
  border-color: var(--dark);
}

.empty-rooms {
  text-align: center;
  padding: 40px;
  color: var(--muted);
  font-size: 14px;
}

.rooms-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.room-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
  transition: border-color var(--transition), transform var(--transition);
}

.room-card:hover {
  border-color: rgba(17, 17, 17, 0.25);
  transform: translateY(-2px);
}

.room-photo {
  height: 180px;
  overflow: hidden;
  position: relative;
  background: var(--cream);
}

.room-photo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition);
}

.room-card:hover .room-photo img { transform: scale(1.03); }

.room-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.badge-ok { background: #D1FAE5; color: #065F46; }
.badge-no { background: #FEE2E2; color: #991B1B; }

.room-info {
  padding: 16px 18px 18px;
}

.room-type {
  font-size: 10px;
  letter-spacing: 2.5px;
  text-transform: uppercase;
  color: var(--gold);
  font-weight: 500;
  margin-bottom: 6px;
}

.room-name {
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 400;
  color: var(--dark);
  margin-bottom: 6px;
}

.room-meta {
  font-size: 12px;
  color: var(--muted);
  margin-bottom: 14px;
}

.room-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.room-price {
  font-size: 15px;
  font-weight: 500;
  color: var(--dark);
}

.per {
  font-size: 12px;
  color: var(--muted);
  font-weight: 400;
}

.btn-book {
  padding: 7px 14px;
  background: var(--dark);
  color: var(--cream);
  border: none;
  border-radius: 3px;
  font-size: 11px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  cursor: pointer;
  transition: opacity var(--transition);
}

.btn-book:hover { opacity: 0.85; }

@media (max-width: 1000px) {
  .hotel-layout { grid-template-columns: 1fr; }
  .rooms-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 768px) {
  .header { padding: 18px 24px; }
  .header-nav { display: none; }
  .main { padding: 40px 24px; }
}

@media (max-width: 640px) {
  .rooms-grid { grid-template-columns: 1fr; }
  .rooms-header { flex-direction: column; align-items: flex-start; }
}
</style>
