<template>
  <div class="page">
    <header class="header">
      <RouterLink to="/dashboard" class="brand-name">StayNova</RouterLink>
      <nav class="header-nav">
        <RouterLink to="/dashboard" class="nav-link">Главная</RouterLink>
        <RouterLink to="/rooms" class="nav-link">Номера</RouterLink>
      </nav>
      <div class="header-right">
        <button class="btn-logout" @click="handleLogout">Выйти</button>
      </div>
    </header>

    <main class="main">
      <div class="page-header">
        <div class="page-label">Активность</div>
        <h1>Уведомления</h1>
      </div>

      <!-- Email subscription -->
      <div class="subscribe-card">
        <div class="subscribe-left">
          <div class="subscribe-title">Email уведомления</div>
          <div class="subscribe-desc">Получайте уведомления о бронированиях на почту</div>
        </div>
        <div class="subscribe-right">
          <div v-if="!showEmailForm" class="subscribe-actions">
            <button class="btn-action" @click="showEmailForm = true">Подписаться</button>
            <button class="btn-ghost" @click="handleUnsubscribe('EMAIL')">Отписаться</button>
          </div>
          <div v-else class="email-form">
            <input
              v-model="emailInput"
              type="email"
              placeholder="Введите email"
              class="email-input"
            />
            <button class="btn-action" @click="handleEmailSubscribe" :disabled="!emailInput">Подтвердить</button>
            <button class="btn-ghost" @click="showEmailForm = false">Отмена</button>
          </div>
        </div>
        <div v-if="subscribeSuccess" class="subscribe-success">{{ subscribeSuccess }}</div>
      </div>

      <!-- Notifications list -->
      <div v-if="notif.loading" class="state-msg">Загрузка...</div>

      <div v-else-if="notif.error" class="error-msg">{{ notif.error }}</div>

      <div v-else-if="notif.notifications.length === 0" class="empty">
        <div class="empty-line"></div>
        <div class="empty-text">Уведомлений пока нет</div>
      </div>

      <div v-else class="notifications-list">
        <div v-for="(n, i) in notif.notifications" :key="i" class="notification-item">
          <div class="notif-dot" :class="notifClass(n.message)"></div>
          <div class="notif-body">
            <div class="notif-message">{{ n.message }}</div>
            <div class="notif-time">{{ formatTime(n.time) }}</div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'
import { useNotificationStore } from '../stores/notification.js'

const auth = useAuthStore()
const notif = useNotificationStore()
const router = useRouter()

const showEmailForm = ref(false)
const emailInput = ref('')
const subscribeSuccess = ref('')

function formatTime(t) {
  return new Date(t).toLocaleString('ru-RU')
}

function notifClass(message) {
  if (!message) return 'dot-neutral'
  if (message.includes('подтвержден') || message.includes('подтверждена')) return 'dot-success'
  if (message.includes('отмен')) return 'dot-error'
  if (message.includes('создан') || message.includes('создана')) return 'dot-info'
  return 'dot-neutral'
}

async function handleEmailSubscribe() {
  const ok = await notif.subscribeEmail(emailInput.value)
  if (ok) {
    subscribeSuccess.value = 'Вы подписаны на email уведомления'
    showEmailForm.value = false
    emailInput.value = ''
    setTimeout(() => { subscribeSuccess.value = '' }, 3000)
  }
}

async function handleUnsubscribe(type) {
  const ok = await notif.unsubscribe(type)
  if (ok) {
    subscribeSuccess.value = 'Вы отписались от уведомлений'
    setTimeout(() => { subscribeSuccess.value = '' }, 3000)
  }
}

async function handleLogout() {
  await auth.doLogout()
  router.push('/login')
}

onMounted(() => notif.fetchNotifications())
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
  max-width: 800px;
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

/* ── Subscribe card ── */
.subscribe-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 24px 28px;
  margin-bottom: 36px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  flex-wrap: wrap;
}

.subscribe-title {
  font-size: 15px;
  font-weight: 500;
  color: var(--dark);
  margin-bottom: 4px;
}

.subscribe-desc {
  font-size: 13px;
  color: var(--muted);
}

.subscribe-actions,
.email-form {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.email-input {
  padding: 10px 14px;
  border: 1px solid var(--border);
  border-radius: 4px;
  font-size: 14px;
  color: var(--dark);
  outline: none;
  width: 220px;
  transition: border-color var(--transition);
}

.email-input:focus { border-color: var(--dark); }

.btn-action {
  padding: 9px 18px;
  background: var(--dark);
  color: var(--cream);
  border: none;
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 1px;
  text-transform: uppercase;
  cursor: pointer;
  transition: opacity var(--transition);
  white-space: nowrap;
}

.btn-action:hover:not(:disabled) { opacity: 0.85; }
.btn-action:disabled { opacity: 0.45; cursor: not-allowed; }

.btn-ghost {
  padding: 9px 18px;
  background: transparent;
  border: 1px solid var(--border);
  color: var(--muted);
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 1px;
  text-transform: uppercase;
  cursor: pointer;
  transition: all var(--transition);
  white-space: nowrap;
}

.btn-ghost:hover {
  border-color: var(--dark);
  color: var(--dark);
}

.subscribe-success {
  width: 100%;
  padding: 10px 14px;
  background: var(--success-bg);
  border: 1px solid var(--success-border);
  border-radius: 4px;
  color: var(--success);
  font-size: 13px;
  margin-top: 8px;
}

/* ── States ── */
.state-msg {
  text-align: center;
  padding: 60px;
  color: var(--muted);
}

.error-msg {
  padding: 14px;
  background: var(--error-bg);
  border: 1px solid var(--error-border);
  border-radius: 4px;
  color: var(--error);
  font-size: 13px;
  margin-bottom: 16px;
}

.empty {
  text-align: center;
  padding: 80px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.empty-line {
  width: 40px;
  height: 1px;
  background: var(--border);
}

.empty-text {
  color: var(--muted);
  font-size: 15px;
}

/* ── Notifications ── */
.notifications-list {
  display: flex;
  flex-direction: column;
  gap: 0;
  border: 1px solid var(--border);
  border-radius: 8px;
  overflow: hidden;
}

.notification-item {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  padding: 20px 24px;
  background: var(--surface);
  border-bottom: 1px solid var(--border);
  transition: background var(--transition);
}

.notification-item:last-child { border-bottom: none; }

.notification-item:hover { background: var(--cream); }

.notif-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
  margin-top: 6px;
}

.dot-success { background: #16a34a; }
.dot-error { background: var(--error); }
.dot-info { background: var(--gold); }
.dot-neutral { background: var(--border); }

.notif-body { flex: 1; }

.notif-message {
  font-size: 14px;
  color: var(--dark);
  line-height: 1.5;
  margin-bottom: 4px;
}

.notif-time {
  font-size: 11px;
  color: var(--muted);
  letter-spacing: 0.3px;
}

@media (max-width: 768px) {
  .header { padding: 18px 24px; }
  .header-nav { display: none; }
  .main { padding: 40px 24px; }
  .subscribe-card { flex-direction: column; align-items: flex-start; }
}
</style>
