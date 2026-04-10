<template>
  <div class="unavailable-layout">
    <div class="unavailable-card">
      <div class="status-badge">503</div>
      <h1 class="title">Сервис недоступен</h1>
      <p class="description">
        <strong>{{ serviceLabel }}</strong> временно недоступен. Попробуйте позже или вернитесь на главную.
      </p>
      <div class="actions">
        <button class="btn-retry" @click="retry">Повторить попытку</button>
        <RouterLink to="/" class="btn-home">На главную</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, RouterLink } from 'vue-router'

const route = useRoute()

const serviceNames = {
  'user-service': 'Сервис пользователей',
  'auth-service': 'Сервис авторизации',
  'booking-service': 'Сервис бронирования',
  'notification-service': 'Сервис уведомлений',
  'payment-service': 'Сервис оплаты',
}

const serviceLabel = computed(() => serviceNames[route.params.service] ?? 'Сервис')

function retry() {
  window.location.reload()
}
</script>

<style scoped>
.unavailable-layout {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--cream);
  padding: 24px;
}

.unavailable-card {
  max-width: 480px;
  width: 100%;
  text-align: center;
}

.status-badge {
  display: inline-block;
  font-family: var(--font-display);
  font-size: 80px;
  font-weight: 300;
  color: var(--gold);
  letter-spacing: -0.02em;
  line-height: 1;
  margin-bottom: 24px;
}

.title {
  font-family: var(--font-display);
  font-size: 32px;
  font-weight: 400;
  color: var(--dark);
  letter-spacing: -0.01em;
  margin-bottom: 16px;
}

.description {
  font-size: 15px;
  color: var(--muted);
  line-height: 1.7;
  margin-bottom: 36px;
}

.description strong {
  color: var(--dark);
  font-weight: 500;
}

.actions {
  display: flex;
  gap: 12px;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-retry {
  padding: 13px 28px;
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

.btn-retry:hover {
  opacity: 0.85;
}

.btn-home {
  padding: 13px 28px;
  background: transparent;
  color: var(--dark);
  border: 1px solid var(--border);
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 2px;
  text-transform: uppercase;
  font-weight: 500;
  text-decoration: none;
  transition: border-color var(--transition);
  display: inline-block;
}

.btn-home:hover {
  border-color: var(--dark);
}
</style>
