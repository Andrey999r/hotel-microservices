<template>
  <div class="page">
    <header class="header">
      <RouterLink to="/dashboard" class="brand-name">StayNova</RouterLink>
      <nav class="header-nav">
        <RouterLink to="/admin/reservations" class="nav-link">Все бронирования</RouterLink>
        <RouterLink to="/admin/hotels" class="nav-link">Отели</RouterLink>
      </nav>
      <div class="header-right">
        <span class="admin-badge">Администратор</span>
        <button class="btn-logout" @click="handleLogout">Выйти</button>
      </div>
    </header>

    <main class="main">
      <div v-if="loading" class="state-msg">Загрузка...</div>
      <div v-else-if="error" class="error-msg">{{ error }}</div>
      <div v-else-if="reservation" class="reservation-detail">

        <div class="detail-top">
          <div>
            <div class="page-label">Администрирование</div>
            <h1 class="page-title">Бронь #{{ reservation.id }}</h1>
          </div>
          <span class="res-status" :class="statusClass(reservation.reservationStatus)">
            {{ statusLabel(reservation.reservationStatus) }}
          </span>
        </div>

        <div class="detail-card">
          <div class="info-row">
            <span class="label">Пользователь</span>
            <span class="value">{{ reservation.userLogin }}</span>
          </div>
          <div class="info-row">
            <span class="label">Комната</span>
            <span class="value">#{{ reservation.roomId }}</span>
          </div>
          <div class="info-row">
            <span class="label">Дата заезда</span>
            <span class="value">{{ formatDate(reservation.startDate) }}</span>
          </div>
          <div class="info-row">
            <span class="label">Дата выезда</span>
            <span class="value">{{ formatDate(reservation.endDate) }}</span>
          </div>
          <div class="info-row">
            <span class="label">Ночей</span>
            <span class="value">{{ calcNights(reservation.startDate, reservation.endDate) }}</span>
          </div>
        </div>

        <div class="actions" v-if="reservation.reservationStatus !== 'CANCELLED'">
          <button class="btn-cancel" @click="handleCancel" :disabled="loading">
            Отменить бронь
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '../../stores/auth.js'
import { bookingApi } from '../../api/booking.js'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const reservation = ref(null)
const loading = ref(false)
const error = ref(null)

function formatDate(d) {
  return new Date(d).toLocaleDateString('ru-RU')
}

function calcNights(start, end) {
  const diff = new Date(end) - new Date(start)
  return Math.max(0, Math.floor(diff / (1000 * 60 * 60 * 24)))
}

function statusLabel(s) {
  return { PENDING: 'Ожидает', APPROVED: 'Подтверждено', CANCELLED: 'Отменено' }[s] || s
}

function statusClass(s) {
  return { PENDING: 'status-pending', APPROVED: 'status-approved', CANCELLED: 'status-cancelled' }[s] || ''
}

async function fetchReservation() {
  loading.value = true
  error.value = null
  try {
    const res = await bookingApi.getReservationById(route.params.id)
    reservation.value = res.data
  } catch (e) {
    error.value = e.response?.data?.message || 'Ошибка загрузки бронирования'
  } finally {
    loading.value = false
  }
}

async function handleCancel() {
  if (!confirm('Отменить бронирование?')) return
  loading.value = true
  try {
    await bookingApi.cancelReservation(reservation.value.id)
    await fetchReservation()
  } catch (e) {
    alert(e.response?.data?.message || 'Ошибка отмены')
  } finally {
    loading.value = false
  }
}

async function handleLogout() {
  await auth.doLogout()
  router.push('/login')
}

onMounted(fetchReservation)
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

/* ── Main ── */
.main {
  max-width: 760px;
  margin: 0 auto;
  padding: 64px 56px;
}

.state-msg {
  text-align: center;
  padding: 60px;
  color: var(--muted);
}

.error-msg {
  padding: 16px;
  background: var(--error-bg);
  border: 1px solid var(--error-border);
  border-radius: 4px;
  color: var(--error);
  font-size: 13px;
}

/* ── Detail ── */
.detail-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 40px;
  gap: 20px;
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

.res-status {
  padding: 5px 14px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 500;
  letter-spacing: 0.5px;
  flex-shrink: 0;
}

.status-pending { background: #FEF3C7; color: #92400E; }
.status-approved { background: var(--success-bg); color: var(--success); }
.status-cancelled { background: #F3F4F6; color: #6B7280; }

/* ── Info card ── */
.detail-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 32px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid var(--border);
  transition: background var(--transition);
}

.info-row:last-child { border-bottom: none; }
.info-row:hover { background: var(--cream); }

.label {
  font-size: 10px;
  letter-spacing: 2px;
  text-transform: uppercase;
  color: var(--muted);
  font-weight: 500;
}

.value {
  font-size: 15px;
  color: var(--dark);
  font-weight: 400;
}

/* ── Actions ── */
.actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.btn-cancel {
  padding: 12px 28px;
  background: transparent;
  border: 1px solid var(--error-border);
  color: var(--error);
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 2px;
  text-transform: uppercase;
  cursor: pointer;
  transition: all var(--transition);
}

.btn-cancel:hover:not(:disabled) { background: var(--error-bg); }

button:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .header { padding: 18px 24px; }
  .header-nav { display: none; }
  .main { padding: 40px 24px; }
  .detail-top { flex-direction: column; align-items: flex-start; }
  .actions { justify-content: flex-start; }
}
</style>
