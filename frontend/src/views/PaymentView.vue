<template>
  <div class="page">
    <header class="header">
      <RouterLink to="/dashboard" class="brand-name">StayNova</RouterLink>
    </header>

    <main class="main">
      <div v-if="processing" class="state-msg">
        <div class="spinner-lg"></div>
        <p>Обрабатываем платёж...</p>
      </div>

      <div v-else-if="done" class="done-card" :class="doneSuccess ? 'done-success' : 'done-fail'">
        <div class="done-icon">{{ doneSuccess ? '✓' : '✕' }}</div>
        <div class="done-title">
          {{ doneSuccess ? 'Оплата прошла успешно' : 'Оплата отклонена' }}
        </div>
        <div class="done-text">
          {{ doneSuccess
            ? 'Бронь подтверждена. Проверьте уведомления.'
            : 'Попробуйте снова или выберите другой способ оплаты.' }}
        </div>
        <RouterLink to="/reservations" class="btn-primary">Мои бронирования</RouterLink>
      </div>

      <div v-else class="payment-card">
        <div class="card-label">Подтверждение оплаты</div>
        <h1 class="card-title">Оплата бронирования</h1>

        <div class="info-block">
          <div class="info-row">
            <span class="info-key">Номер брони</span>
            <span class="info-val">#{{ reservationId || '—' }}</span>
          </div>
          <div class="info-row">
            <span class="info-key">Сессия оплаты</span>
            <span class="info-val session-id">{{ sessionId }}</span>
          </div>
        </div>

        <div class="divider"></div>

        <div class="stub-notice">
          Это тестовая страница оплаты. Выберите результат:
        </div>

        <div class="action-row">
          <button class="btn-pay" @click="pay(true)">
            Подтвердить оплату
          </button>
          <button class="btn-fail" @click="pay(false)">
            Отклонить оплату
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const sessionId = computed(() => route.params.sessionId)
const reservationId = computed(() => route.query.reservationId)

const processing = ref(false)
const done = ref(false)
const doneSuccess = ref(false)

const paymentApi = axios.create({ baseURL: '/payment-api' })

async function pay(success) {
  processing.value = true
  try {
    await paymentApi.post('/webhook', {
      sessionId: sessionId.value,
      success,
      errorMessage: success ? null : 'Платёж отклонён пользователем'
    })
    doneSuccess.value = success
  } catch (e) {
    doneSuccess.value = false
  } finally {
    processing.value = false
    done.value = true
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: var(--cream);
  display: flex;
  flex-direction: column;
}

.header {
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
}

.main {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 24px;
}

/* Loading */
.state-msg {
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  color: var(--muted);
}

.spinner-lg {
  width: 40px;
  height: 40px;
  border: 3px solid var(--border);
  border-top-color: var(--gold);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Payment card */
.payment-card {
  background: white;
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 48px 52px;
  width: 100%;
  max-width: 480px;
}

.card-label {
  font-size: 10px;
  letter-spacing: 3px;
  text-transform: uppercase;
  color: var(--gold);
  margin-bottom: 12px;
}

.card-title {
  font-family: var(--font-display);
  font-size: 32px;
  font-weight: 300;
  color: var(--dark);
  margin: 0 0 32px;
}

.info-block {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 28px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
  gap: 16px;
}

.info-key {
  font-size: 11px;
  letter-spacing: 1px;
  text-transform: uppercase;
  color: var(--muted);
}

.info-val {
  font-size: 15px;
  color: var(--dark);
  font-weight: 500;
}

.session-id {
  font-size: 11px;
  font-family: monospace;
  color: var(--muted);
  word-break: break-all;
  text-align: right;
  max-width: 260px;
}

.divider {
  height: 1px;
  background: var(--border);
  margin-bottom: 24px;
}

.stub-notice {
  font-size: 12px;
  color: var(--muted);
  margin-bottom: 28px;
  padding: 12px 16px;
  background: #FFFBEB;
  border: 1px solid #FEF3C7;
  border-radius: 4px;
}

.action-row {
  display: flex;
  gap: 12px;
}

.btn-pay {
  flex: 1;
  padding: 14px;
  background: var(--dark);
  color: var(--gold);
  -webkit-text-fill-color: var(--gold);
  background-clip: unset;
  -webkit-background-clip: unset;
  background-image: none;
  border: 1px solid rgba(201, 168, 76, 0.35);
  border-radius: 4px;
  font-size: 12px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s var(--ease-out);
}

.btn-pay:hover {
  border-color: var(--gold);
  background: #181512;
}

.btn-fail {
  flex: 1;
  padding: 14px;
  background: transparent;
  color: var(--error);
  border: 1px solid var(--error-border);
  border-radius: 4px;
  font-size: 12px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s var(--ease-out);
}

.btn-fail:hover {
  background: var(--error-bg);
}

/* Done state */
.done-card {
  background: white;
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 56px 52px;
  width: 100%;
  max-width: 400px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.done-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  font-weight: 500;
}

.done-success .done-icon {
  background: var(--success-bg);
  color: var(--success);
  border: 1px solid var(--success-border);
}

.done-fail .done-icon {
  background: var(--error-bg);
  color: var(--error);
  border: 1px solid var(--error-border);
}

.done-title {
  font-family: var(--font-display);
  font-size: 26px;
  font-weight: 400;
  color: var(--dark);
}

.done-text {
  font-size: 14px;
  color: var(--muted);
  max-width: 300px;
  line-height: 1.6;
}

.btn-primary {
  margin-top: 8px;
  display: inline-block;
  padding: 12px 28px;
  background: var(--dark);
  color: var(--gold);
  border: 1px solid rgba(201, 168, 76, 0.35);
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 2px;
  text-transform: uppercase;
  font-weight: 500;
  text-decoration: none;
  transition: all 0.2s var(--ease-out);
  cursor: pointer;
}

.btn-primary:hover {
  border-color: var(--gold);
  background: #181512;
}
</style>
