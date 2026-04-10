<template>
  <div class="page">
    <header class="header">
      <RouterLink to="/dashboard" class="brand-name">StayNova</RouterLink>
      <nav class="header-nav">
        <RouterLink to="/rooms" class="nav-link">Номера</RouterLink>
        <RouterLink to="/dashboard" class="nav-link">Главная</RouterLink>
      </nav>
      <div class="header-right">
        <button class="btn-logout" @click="handleLogout">Выйти</button>
      </div>
    </header>

    <main class="main">
      <div class="page-header">
        <div class="page-label">История</div>
        <h1>Мои бронирования</h1>
      </div>

      <div v-if="booking.loading" class="state-msg">Загрузка...</div>

      <div v-else-if="booking.error" class="error-msg">{{ booking.error }}</div>

      <div v-else-if="booking.reservations.length === 0" class="empty">
        <div class="empty-line"></div>
        <div class="empty-text">У вас пока нет бронирований</div>
        <RouterLink to="/rooms" class="btn-primary">Посмотреть номера</RouterLink>
      </div>

      <div v-else class="reservations-list">
        <div v-for="r in booking.reservations" :key="r.id" class="reservation-card">
          <div class="res-left">
            <div class="res-id">Бронь #{{ r.id }}</div>
            <div class="res-room">Номер {{ r.roomId }}</div>
            <div class="res-dates">
              {{ formatDate(r.startDate) }} — {{ formatDate(r.endDate) }}
              <span class="res-nights">· {{ calcNights(r.startDate, r.endDate) }} ночей</span>
            </div>
          </div>

          <div class="res-right">
            <span class="res-status" :class="statusClass(r.reservationStatus)">
              {{ statusLabel(r.reservationStatus) }}
            </span>
            <template v-if="r.reservationStatus === 'PENDING' && r.paymentUrl">
              <a :href="r.paymentUrl" class="btn-pay-link">
                Перейти к оплате →
              </a>
            </template>
            <template v-else-if="r.reservationStatus === 'PENDING' && !r.paymentUrl">
              <span class="pending-hint">Ожидание платёжной ссылки...</span>
            </template>
            <template v-if="r.reservationStatus === 'APPROVED'">
              <button
                class="btn-cancel"
                @click="handleCancel(r.id)"
                :disabled="booking.loading || !canCancel(r.startDate)"
                :title="!canCancel(r.startDate) ? 'Отмена доступна минимум за 2 дня до заезда' : ''"
              >
                Отменить
              </button>
              <div v-if="!canCancel(r.startDate)" class="cancel-hint">
                Отмена недоступна — до заезда менее 2 дней
              </div>
            </template>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'
import { useBookingStore } from '../stores/booking.js'

const auth = useAuthStore()
const booking = useBookingStore()
const router = useRouter()

function formatDate(d) {
  return new Date(d).toLocaleDateString('ru-RU')
}

function calcNights(start, end) {
  const diff = new Date(end) - new Date(start)
  return Math.max(0, Math.floor(diff / (1000 * 60 * 60 * 24)))
}

function statusLabel(s) {
  const map = { PENDING: 'Ожидает', APPROVED: 'Подтверждено', CANCELLED: 'Отменено' }
  return map[s] || s
}

/**
 * Отмена доступна, если текущая дата строго раньше, чем startDate - 2 дня.
 * Т.е. последний день для отмены — startDate - 2 дня включительно.
 */
function canCancel(startDate) {
  const deadline = new Date(startDate)
  deadline.setDate(deadline.getDate() - 2)
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  deadline.setHours(0, 0, 0, 0)
  return today <= deadline
}

function statusClass(s) {
  return {
    PENDING: 'status-pending',
    APPROVED: 'status-approved',
    CANCELLED: 'status-cancelled'
  }[s] || ''
}

async function handleCancel(id) {
  if (!confirm('Отменить бронирование?')) return
  await booking.cancelReservation(id)
}

async function handleLogout() {
  await auth.doLogout()
  router.push('/login')
}

onMounted(() => booking.fetchReservations())
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

.nav-link:hover {
  color: rgba(247, 246, 243, 0.85);
  opacity: 1;
}

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

.main {
  max-width: 900px;
  margin: 0 auto;
  padding: 64px 56px;
}

.page-header { margin-bottom: 48px; }

.page-label {
  font-size: 10px;
  letter-spacing: 3px;
  text-transform: uppercase;
  color: var(--gold);
  margin-bottom: 12px;
}

.page-header h1 {
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
}

.error-msg {
  padding: 16px;
  background: var(--error-bg);
  border: 1px solid var(--error-border);
  border-radius: 4px;
  color: var(--error);
  font-size: 13px;
}

/* ── Empty state ── */
.empty {
  text-align: center;
  padding: 80px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.empty-line {
  width: 40px;
  height: 1px;
  background: var(--border);
}

.empty-text {
  color: var(--muted);
  font-size: 16px;
}

/* .btn-primary uses global style from style.css */

/* ── Reservations list ── */
.reservations-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.reservation-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 24px 28px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: border-color var(--transition);
}

.reservation-card:hover {
  border-color: rgba(17, 17, 17, 0.25);
}

.res-left {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.res-id {
  font-size: 10px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  color: var(--muted);
}

.res-room {
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: 400;
  color: var(--dark);
}

.res-dates {
  font-size: 14px;
  color: var(--dark);
}

.res-nights {
  color: var(--muted);
  margin-left: 4px;
}

.res-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
}

.res-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 11px;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.status-pending {
  background: #FEF3C7;
  color: #92400E;
}

.status-approved {
  background: var(--success-bg);
  color: var(--success);
}

.status-cancelled {
  background: #F3F4F6;
  color: #6B7280;
}

.btn-cancel {
  padding: 7px 16px;
  background: transparent;
  border: 1px solid var(--error-border);
  color: var(--error);
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 0.5px;
  cursor: pointer;
  transition: all var(--transition);
}

.btn-cancel:hover:not(:disabled) { background: var(--error-bg); }
.btn-cancel:disabled { opacity: 0.4; cursor: not-allowed; }

.cancel-hint {
  font-size: 11px;
  color: var(--muted);
  text-align: right;
  max-width: 180px;
  line-height: 1.4;
}

.btn-pay-link {
  display: inline-block;
  padding: 8px 16px;
  background: var(--dark);
  color: var(--gold);
  border: 1px solid rgba(201, 168, 76, 0.35);
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s var(--ease-out);
  white-space: nowrap;
}

.btn-pay-link:hover {
  border-color: var(--gold);
  background: #181512;
  opacity: 1;
}

.pending-hint {
  font-size: 11px;
  color: var(--muted);
  font-style: italic;
}

@media (max-width: 768px) {
  .header { padding: 18px 24px; }
  .header-nav { display: none; }
  .main { padding: 40px 24px; }
  .reservation-card { flex-direction: column; align-items: flex-start; gap: 16px; }
  .res-right { align-items: flex-start; }
}
</style>
