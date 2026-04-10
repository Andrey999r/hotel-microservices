<template>
  <div class="dashboard">
    <header class="header">
      <RouterLink to="/dashboard" class="brand-name">StayNova</RouterLink>
      <nav class="header-nav">
        <RouterLink to="/dashboard" class="nav-link">Главная</RouterLink>
        <RouterLink to="/admin/hotels" class="nav-link">Отели</RouterLink>
        <RouterLink to="/admin/rooms" class="nav-link">Номера</RouterLink>
      </nav>
      <div class="header-right">
        <span class="admin-badge">Администратор</span>
        <button class="btn-logout" @click="handleLogout">Выйти</button>
      </div>
    </header>

    <main class="main">
      <div class="page-header">
        <div>
          <div class="page-label">Администрирование</div>
          <h1 class="page-title">Все бронирования</h1>
        </div>
        <div class="filters">
          <input
            v-model="roomIdFilter"
            class="filter-input"
            type="number"
            placeholder="Фильтр по ID комнаты"
            @input="onFilterChange"
          />
        </div>
      </div>

      <div v-if="loading" class="loading">Загрузка...</div>
      <div v-else-if="error" class="error-msg">{{ error }}</div>
      <div v-else-if="reservations.length === 0" class="empty">Бронирования не найдены</div>

      <div v-else class="reservations-grid">
        <div
          v-for="r in reservations"
          :key="r.id"
          class="reservation-card"
        >
          <div class="card-top">
            <span class="res-id">#{{ r.id }}</span>
            <span class="res-status" :class="statusClass(r.reservationStatus)">
              {{ statusLabel(r.reservationStatus) }}
            </span>
          </div>
          <div class="card-body">
            <div class="card-row">
              <span class="card-label">Пользователь</span>
              <span class="card-value user">{{ r.userLogin }}</span>
            </div>
            <div class="card-row">
              <span class="card-label">Комната</span>
              <span class="card-value">#{{ r.roomId }}</span>
            </div>
            <div class="card-row">
              <span class="card-label">Заезд</span>
              <span class="card-value">{{ formatDate(r.startDate) }}</span>
            </div>
            <div class="card-row">
              <span class="card-label">Выезд</span>
              <span class="card-value">{{ formatDate(r.endDate) }}</span>
            </div>
          </div>
          <div class="card-footer">
            <RouterLink :to="`/admin/reservations/${r.id}`" class="btn-detail">
              Подробнее →
            </RouterLink>
          </div>
        </div>
      </div>

      <div class="pagination" v-if="!loading && !error">
        <button class="page-btn" :disabled="page === 0" @click="prevPage">← Назад</button>
        <span class="page-info">Страница {{ page + 1 }}</span>
        <button class="page-btn" :disabled="reservations.length < pageSize" @click="nextPage">
          Вперёд →
        </button>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth.js'
import { bookingApi } from '../../api/booking.js'

const router = useRouter()
const auth = useAuthStore()

const reservations = ref([])
const loading = ref(false)
const error = ref(null)
const page = ref(0)
const pageSize = 10
const roomIdFilter = ref(null)

function formatDate(d) {
  return new Date(d).toLocaleDateString('ru-RU', { day: '2-digit', month: '2-digit', year: 'numeric' })
}

function statusLabel(s) {
  return { PENDING: 'Ожидает', APPROVED: 'Подтверждено', CANCELLED: 'Отменено' }[s] || s
}

function statusClass(s) {
  return { PENDING: 'status-pending', APPROVED: 'status-approved', CANCELLED: 'status-cancelled' }[s] || ''
}

async function fetchReservations() {
  loading.value = true
  error.value = null
  try {
    const params = { pageNumber: page.value, pageSize }
    if (roomIdFilter.value) params.roomId = roomIdFilter.value
    const res = await bookingApi.getReservations(params)
    reservations.value = res.data
  } catch (e) {
    error.value = e.response?.data?.message || 'Ошибка загрузки'
  } finally {
    loading.value = false
  }
}

function onFilterChange() {
  page.value = 0
  fetchReservations()
}

function prevPage() {
  if (page.value > 0) { page.value--; fetchReservations() }
}

function nextPage() {
  page.value++
  fetchReservations()
}

async function handleLogout() {
  await auth.doLogout()
  router.push('/login')
}

onMounted(fetchReservations)
</script>

<style scoped>
.dashboard {
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
  max-width: 1200px;
  margin: 0 auto;
  padding: 56px 56px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 48px;
  flex-wrap: wrap;
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

.filters {
  display: flex;
  align-items: center;
}

.filter-input {
  padding: 11px 16px;
  border: 1px solid var(--border);
  border-radius: 4px;
  font-size: 13px;
  background: var(--surface);
  color: var(--dark);
  width: 220px;
  outline: none;
  transition: border-color var(--transition);
}

.filter-input:focus { border-color: var(--dark); }

/* ── States ── */
.loading,
.empty {
  text-align: center;
  padding: 60px;
  color: var(--muted);
  font-size: 14px;
}

.error-msg {
  padding: 16px;
  background: var(--error-bg);
  border: 1px solid var(--error-border);
  border-radius: 4px;
  color: var(--error);
  font-size: 13px;
}

/* ── Cards ── */
.reservations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 16px;
  margin-bottom: 36px;
}

.reservation-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 6px;
  overflow: hidden;
  transition: border-color var(--transition), transform var(--transition);
}

.reservation-card:hover {
  border-color: rgba(17, 17, 17, 0.2);
  transform: translateY(-2px);
}

.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px 14px;
  border-bottom: 1px solid var(--border);
}

.res-id {
  font-family: var(--font-display);
  font-size: 18px;
  font-weight: 400;
  color: var(--dark);
}

.res-status {
  padding: 3px 10px;
  border-radius: 20px;
  font-size: 10px;
  font-weight: 500;
  letter-spacing: 0.5px;
}

.status-pending { background: #FEF3C7; color: #92400E; }
.status-approved { background: var(--success-bg); color: var(--success); }
.status-cancelled { background: #F3F4F6; color: #6B7280; }

.card-body {
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.card-row {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 8px;
}

.card-label {
  font-size: 10px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  color: var(--muted);
  white-space: nowrap;
}

.card-value {
  font-size: 13px;
  font-weight: 500;
  color: var(--dark);
  text-align: right;
}

.card-value.user {
  color: var(--dark);
  font-weight: 600;
  font-family: var(--font-display);
  font-size: 15px;
}

.card-footer {
  padding: 12px 20px 16px;
  border-top: 1px solid var(--border);
}

.btn-detail {
  display: inline-block;
  font-size: 11px;
  letter-spacing: 2px;
  text-transform: uppercase;
  color: var(--dark);
  text-decoration: none;
  font-weight: 500;
  transition: opacity var(--transition);
}

.btn-detail:hover { opacity: 0.6; }

/* ── Pagination ── */
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  margin-top: 8px;
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

.page-info {
  font-size: 13px;
  color: var(--muted);
}

@media (max-width: 768px) {
  .header { padding: 18px 24px; }
  .header-nav { display: none; }
  .main { padding: 40px 24px; }
  .page-header { flex-direction: column; align-items: flex-start; }
  .reservations-grid { grid-template-columns: 1fr; }
}
</style>
