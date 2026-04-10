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

    <main class="main" v-if="booking.currentRoom">
      <div class="room-layout">
        <!-- Photos -->
        <div class="photos-section">
          <div class="main-photo">
            <img
              :src="activePhoto || defaultRoomPhoto"
              alt="Фото номера"
              @error="e => e.target.src = defaultRoomPhoto"
            />
          </div>
          <div class="gallery" v-if="allPhotos.length > 1">
            <div
              v-for="(url, i) in allPhotos"
              :key="i"
              class="gallery-thumb"
              :class="{ active: activePhoto === url }"
              @click="activePhoto = url"
            >
              <img :src="url" />
            </div>
          </div>
        </div>

        <!-- Info -->
        <div class="info-section">
          <div class="room-type">{{ roomTypeLabel(booking.currentRoom.roomType) }}</div>
          <h1 class="room-title">{{ booking.currentRoom.title || 'Номер' }}</h1>

          <div
            class="room-status"
            :class="booking.currentRoom.availabilityStatus === 'AVAILABLE' ? 'status-available' : 'status-unavailable'"
          >
            {{ booking.currentRoom.availabilityStatus === 'AVAILABLE' ? 'Доступен для бронирования' : 'Временно недоступен' }}
          </div>

          <p class="room-desc" v-if="booking.currentRoom.description">
            {{ booking.currentRoom.description }}
          </p>

          <div class="room-meta" v-if="booking.currentRoom.maxGuests">
            Вмещает до {{ booking.currentRoom.maxGuests }} гостей
          </div>

          <div class="room-price" v-if="booking.currentRoom.price">
            {{ booking.currentRoom.price }}
            <span class="currency">{{ booking.currentRoom.currency || 'RUB' }}</span>
            <span class="per-night">/ ночь</span>
          </div>

          <!-- Booking form -->
          <div class="booking-form" v-if="booking.currentRoom.availabilityStatus === 'AVAILABLE'">
            <div class="form-label">Забронировать</div>

            <DateRangePicker
              :modelValue="form"
              @update:modelValue="v => { form.startDate = v.startDate; form.endDate = v.endDate }"
              :disabledRanges="unavailableDates"
              :minDate="today"
            />

            <div class="nights-info" v-if="nights > 0">
              {{ nights }} {{ nightsLabel(nights) }} — {{ totalPrice }} {{ booking.currentRoom.currency || 'RUB' }}
            </div>

            <div class="guests-field">
              <label class="guests-label">Количество гостей</label>
              <div class="guests-stepper">
                <button
                  type="button"
                  class="stepper-btn"
                  @click="form.guests = Math.max(1, form.guests - 1)"
                  :disabled="form.guests <= 1"
                >−</button>
                <span class="guests-count">{{ form.guests }}</span>
                <button
                  type="button"
                  class="stepper-btn"
                  @click="form.guests = Math.min(booking.currentRoom.maxGuests || 10, form.guests + 1)"
                  :disabled="form.guests >= (booking.currentRoom.maxGuests || 10)"
                >+</button>
                <span class="guests-max">из {{ booking.currentRoom.maxGuests || 10 }}</span>
              </div>
            </div>

            <div v-if="bookingError" class="error-msg">{{ bookingError }}</div>
            <div v-if="bookingSuccess" class="success-msg">Бронирование подтверждено. Оплата прошла успешно.</div>

            <button
              class="btn-primary"
              @click="openPaymentModal"
              :disabled="!form.startDate || !form.endDate"
            >
              Перейти к оплате &#8594;
            </button>
          </div>
        </div>
      </div>
    </main>

    <div v-else-if="booking.loading" class="state-msg">Загрузка...</div>
    <div v-else-if="booking.error" class="state-msg error">{{ booking.error }}</div>

    <!-- PAYMENT MODAL -->
    <div v-if="showPayment" class="modal-overlay" @click.self="closePayment">
      <div class="modal">
        <div class="modal-title">Подтверждение бронирования</div>

        <div class="booking-summary">
          <div v-if="hotelName" class="summary-row">
            <span>Отель</span>
            <span class="summary-val">{{ hotelName }}</span>
          </div>
          <div class="summary-row">
            <span>Номер</span>
            <span class="summary-val">{{ booking.currentRoom?.title }}</span>
          </div>
          <div class="summary-row">
            <span>Заезд</span>
            <span class="summary-val">{{ formatDate(form.startDate) }}</span>
          </div>
          <div class="summary-row">
            <span>Выезд</span>
            <span class="summary-val">{{ formatDate(form.endDate) }}</span>
          </div>
          <div class="summary-row">
            <span>Ночей</span>
            <span class="summary-val">{{ nights }}</span>
          </div>
          <div class="summary-row">
            <span>Гостей</span>
            <span class="summary-val">{{ form.guests }}</span>
          </div>
          <div class="summary-row total">
            <span>Итого</span>
            <span class="summary-val">{{ totalPrice }} {{ booking.currentRoom?.currency || 'RUB' }}</span>
          </div>
        </div>

        <div v-if="paymentError" class="error-msg">{{ paymentError }}</div>

        <div class="modal-actions">
          <button class="btn-cancel" @click="closePayment" :disabled="paymentLoading">Отмена</button>
          <button class="btn-pay" :disabled="paymentLoading" @click="processPayment">
            <span v-if="paymentLoading" class="spinner"></span>
            <span v-else>Оплатить {{ totalPrice }} {{ booking.currentRoom?.currency || 'RUB' }}</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'
import { useBookingStore } from '../stores/booking.js'
import { bookingApi } from '../api/booking.js'
import { hotelApi } from '../api/hotel.js'
import DateRangePicker from '../components/DateRangePicker.vue'


const auth = useAuthStore()
const booking = useBookingStore()
const route = useRoute()
const router = useRouter()
const defaultRoomPhoto = 'http://localhost:9000/rooms/default-photo.png'
const activePhoto = ref(null)
const unavailableDates = ref([])
const bookingError = ref(null)
const bookingSuccess = ref(false)
const hotelName = ref(null)
const today = new Date().toISOString().split('T')[0]

const form = reactive({ startDate: '', endDate: '', guests: 1 })
const showPayment = ref(false)
const paymentLoading = ref(false)
const paymentError = ref(null)

const allPhotos = computed(() => {
  const room = booking.currentRoom
  if (!room) return []
  const photos = []
  if (room.mainPhotoUrl) photos.push(room.mainPhotoUrl)
  if (room.photoUrls) photos.push(...room.photoUrls.filter(u => u !== room.mainPhotoUrl))
  return photos
})

const nights = computed(() => {
  if (!form.startDate || !form.endDate) return 0
  const diff = new Date(form.endDate) - new Date(form.startDate)
  return Math.max(0, Math.floor(diff / (1000 * 60 * 60 * 24)))
})

const totalPrice = computed(() => {
  if (!booking.currentRoom?.price) return 0
  return nights.value * booking.currentRoom.price
})

function roomTypeLabel(type) {
  return { BUSINESS: 'Бизнес', COMMON: 'Стандарт', PRESIDENT: 'Президентский' }[type] || type
}

function nightsLabel(n) {
  if (n % 10 === 1 && n % 100 !== 11) return 'ночь'
  if ([2, 3, 4].includes(n % 10) && ![12, 13, 14].includes(n % 100)) return 'ночи'
  return 'ночей'
}

function formatDate(d) {
  if (!d) return ''
  return new Date(d).toLocaleDateString('ru-RU')
}

function openPaymentModal() {
  paymentError.value = null
  showPayment.value = true
}

function closePayment() {
  if (!paymentLoading.value) showPayment.value = false
}

async function processPayment() {
  paymentError.value = null
  paymentLoading.value = true
  try {
    const reservation = await booking.bookReservation({
      roomId: booking.currentRoom.id,
      startDate: form.startDate,
      endDate: form.endDate,
      guests: form.guests,
    })
    if (!reservation) {
      paymentError.value = booking.error || 'Не удалось создать бронирование'
      return
    }
    if (reservation.paymentUrl) {
      window.location.href = reservation.paymentUrl
    } else {
      showPayment.value = false
      bookingSuccess.value = true
      form.startDate = ''
      form.endDate = ''
      form.guests = 1
      setTimeout(() => { bookingSuccess.value = false }, 5000)
      try {
        const res = await bookingApi.getNotAvailableDates(route.params.id)
        unavailableDates.value = res.data
      } catch {}
    }
  } catch (e) {
    paymentError.value = e.response?.data?.message || 'Ошибка при создании бронирования'
  } finally {
    paymentLoading.value = false
  }
}

async function handleLogout() {
  await auth.doLogout()
  router.push('/login')
}

onMounted(async () => {
  await booking.fetchRoomById(route.params.id)
  if (booking.currentRoom) {
    activePhoto.value = allPhotos.value[0] || null
    try {
      const res = await bookingApi.getNotAvailableDates(route.params.id)
      unavailableDates.value = res.data
    } catch {}
    if (booking.currentRoom.hotelId) {
      try {
        const hotelRes = await hotelApi.getById(booking.currentRoom.hotelId)
        hotelName.value = hotelRes.data?.name || null
      } catch {}
    }
  }
})
</script>

<style scoped>
.page { min-height: 100vh; background: var(--cream); }

.header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 22px 56px; background: var(--dark);
}

.brand-name {
  font-family: var(--font-display); font-size: 22px; font-weight: 300;
  color: var(--cream); letter-spacing: 2px; text-decoration: none; opacity: 1;
}

.brand-name:hover { opacity: 0.8; }

.header-nav { display: flex; align-items: center; gap: 28px; }

.nav-link {
  font-size: 13px; color: rgba(247,246,243,0.5); text-decoration: none;
  transition: color var(--transition); opacity: 1;
}
.nav-link:hover { color: rgba(247,246,243,0.85); opacity: 1; }

.header-right { display: flex; align-items: center; gap: 16px; }

.btn-logout {
  padding: 8px 20px; background: transparent;
  color: rgba(247,246,243,0.6); border: 1px solid rgba(247,246,243,0.15);
  border-radius: 4px; font-size: 11px; letter-spacing: 1.5px; text-transform: uppercase;
  transition: all var(--transition); cursor: pointer;
}
.btn-logout:hover { border-color: var(--gold); color: var(--gold); }

.main { max-width: 1100px; margin: 0 auto; padding: 64px 56px; }

.state-msg { text-align: center; padding: 80px; color: var(--muted); }
.state-msg.error { color: var(--error); }

.room-layout { display: grid; grid-template-columns: 1fr 420px; gap: 48px; }

/* Photos */
.main-photo {
  width: 100%; aspect-ratio: 16/10; border-radius: 8px; overflow: hidden;
  background: var(--border); display: flex; align-items: center; justify-content: center;
}
.main-photo img { width: 100%; height: 100%; object-fit: cover; }

.gallery { display: flex; gap: 8px; margin-top: 10px; flex-wrap: wrap; }

.gallery-thumb {
  width: 72px; height: 52px; border-radius: 4px; overflow: hidden;
  cursor: pointer; border: 2px solid transparent; transition: border-color var(--transition);
}
.gallery-thumb.active { border-color: var(--dark); }
.gallery-thumb img { width: 100%; height: 100%; object-fit: cover; }

/* Info */
.info-section { display: flex; flex-direction: column; gap: 16px; }

.room-type {
  font-size: 10px; letter-spacing: 2.5px; text-transform: uppercase;
  color: var(--gold); font-weight: 500;
}

.room-title {
  font-family: var(--font-display); font-size: 38px; font-weight: 300;
  color: var(--dark); line-height: 1.1; letter-spacing: -0.02em;
}

.room-status {
  display: inline-block; padding: 5px 14px; border-radius: 20px;
  font-size: 11px; font-weight: 500; letter-spacing: 0.3px; width: fit-content;
}
.status-available { background: var(--success-bg); color: var(--success); }
.status-unavailable { background: var(--error-bg); color: var(--error); }

.room-desc { font-size: 14px; color: var(--muted); line-height: 1.7; }
.room-meta { font-size: 13px; color: var(--muted); }

.room-price {
  font-family: var(--font-display); font-size: 34px; font-weight: 300; color: var(--dark);
}
.currency { font-size: 18px; margin-left: 4px; }
.per-night { font-size: 14px; color: var(--muted); margin-left: 4px; font-family: var(--font-body); }

/* Booking form */
.booking-form {
  background: var(--surface); border: 1px solid var(--border);
  border-radius: 8px; padding: 24px; display: flex; flex-direction: column; gap: 16px;
}

.form-label {
  font-size: 10px; letter-spacing: 2px; text-transform: uppercase;
  color: var(--muted); font-weight: 500;
}

.nights-info {
  font-size: 14px; color: var(--dark); padding: 12px;
  background: var(--cream); border-radius: 4px; text-align: center; font-weight: 500;
}

.error-msg {
  padding: 12px; background: var(--error-bg); border: 1px solid var(--error-border);
  border-radius: 4px; color: var(--error); font-size: 13px;
}

.success-msg {
  padding: 12px; background: var(--success-bg); border: 1px solid var(--success-border);
  border-radius: 4px; color: var(--success); font-size: 13px;
}

.btn-primary {
  padding: 14px; background: var(--dark); color: var(--cream); -webkit-text-fill-color: var(--cream); background-clip: unset; -webkit-background-clip: unset; background-image: none; border: none;
  border-radius: 4px; font-size: 11px; letter-spacing: 2px; text-transform: uppercase;
  font-weight: 500; transition: opacity var(--transition); display: flex;
  align-items: center; justify-content: center; gap: 8px; cursor: pointer;
}
.btn-primary:hover:not(:disabled) { opacity: 0.85; }
.btn-primary:disabled { opacity: 0.45; cursor: not-allowed; }

/* Guests stepper */
.guests-field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.guests-label {
  font-size: 10px;
  letter-spacing: 2px;
  text-transform: uppercase;
  color: var(--muted);
  font-weight: 500;
}

.guests-stepper {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stepper-btn {
  width: 32px;
  height: 32px;
  border: 1px solid var(--border);
  border-radius: 4px;
  background: white;
  font-size: 18px;
  line-height: 1;
  cursor: pointer;
  transition: all var(--transition);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--dark);
}

.stepper-btn:hover:not(:disabled) {
  border-color: var(--dark);
}

.stepper-btn:disabled {
  opacity: 0.35;
  cursor: not-allowed;
}

.guests-count {
  font-size: 18px;
  font-weight: 500;
  color: var(--dark);
  min-width: 24px;
  text-align: center;
}

.guests-max {
  font-size: 12px;
  color: var(--muted);
}

/* Payment modal */
.modal-overlay {
  position: fixed; inset: 0; background: rgba(17,17,17,0.6);
  backdrop-filter: blur(4px); display: flex; align-items: center;
  justify-content: center; z-index: 100; padding: 20px;
}

.modal {
  background: var(--surface); border-radius: 10px; padding: 40px;
  width: 100%; max-width: 480px; max-height: 90vh; overflow-y: auto;
}

.modal-title {
  font-family: var(--font-display); font-size: 28px; font-weight: 300;
  color: var(--dark); margin-bottom: 28px; letter-spacing: -0.01em;
}

.booking-summary {
  background: var(--cream); border-radius: 6px; padding: 18px 20px; margin-bottom: 28px;
}

.summary-row {
  display: flex; justify-content: space-between; font-size: 13px;
  color: var(--muted); padding: 5px 0;
}
.summary-val { font-weight: 500; color: var(--dark); }
.summary-row.total {
  border-top: 1px solid var(--border); margin-top: 8px; padding-top: 14px;
  font-size: 15px; font-weight: 600;
}

.modal-actions {
  display: flex; gap: 12px; justify-content: flex-end; margin-top: 8px;
}

.btn-cancel {
  padding: 12px 20px; background: transparent;
  border: 1px solid var(--border); border-radius: 4px;
  font-size: 11px; letter-spacing: 1px; text-transform: uppercase;
  cursor: pointer; transition: all var(--transition);
}
.btn-cancel:hover { border-color: var(--dark); }

.btn-pay {
  padding: 12px 24px; background: var(--dark); color: var(--cream); border: none;
  border-radius: 4px; font-size: 11px; letter-spacing: 1.5px; text-transform: uppercase;
  cursor: pointer; transition: opacity var(--transition); display: flex;
  align-items: center; gap: 8px; white-space: nowrap;
}
.btn-pay:hover:not(:disabled) { opacity: 0.85; }
.btn-pay:disabled { opacity: 0.45; cursor: not-allowed; }


.spinner {
  width: 14px; height: 14px;
  border: 2px solid rgba(247,246,243,0.3); border-top-color: var(--cream);
  border-radius: 50%; animation: spin 0.7s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

@media (max-width: 900px) {
  .header { padding: 18px 24px; }
  .header-nav { display: none; }
  .main { padding: 40px 24px; }
  .room-layout { grid-template-columns: 1fr; }
}
</style>
