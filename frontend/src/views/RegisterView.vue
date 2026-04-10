<template>
  <div class="auth-layout">
    <div class="auth-left">
      <div class="brand">
        <span class="brand-name">StayNova</span>
        <span class="brand-tagline">Роскошь в каждой детали</span>
      </div>
      <div class="brand-line"></div>
      <p class="brand-desc">
        Создайте аккаунт и получите доступ к эксклюзивным предложениям в лучших отелях мира.
      </p>
    </div>

    <div class="auth-right">
      <div class="auth-card">
        <div class="auth-header">
          <h1>Регистрация</h1>
          <p>Создайте аккаунт для бронирования</p>
        </div>

        <form @submit.prevent="handleRegister" class="auth-form">
          <div class="field">
            <label>Логин</label>
            <input v-model="form.login" type="text" placeholder="Придумайте логин" required />
          </div>
          <div class="field">
            <label>Email</label>
            <input v-model="form.email" type="email" placeholder="Введите email" required />
          </div>
          <div class="field">
            <label>Пароль</label>
            <input v-model="form.password" type="password" placeholder="Придумайте пароль" required />
          </div>
          <div class="field">
            <label>Подтвердите пароль</label>
            <input v-model="form.confirmPassword" type="password" placeholder="Повторите пароль" required />
            <span v-if="passwordMismatch" class="field-error">Пароли не совпадают</span>
          </div>

          <div class="field admin-field">
            <div class="admin-toggle" @click="showAdminSecret = !showAdminSecret">
              <span class="toggle-label">Регистрация администратора</span>
              <span class="toggle-icon">{{ showAdminSecret ? '−' : '+' }}</span>
            </div>
            <input
              v-if="showAdminSecret"
              v-model="form.adminSecret"
              type="password"
              placeholder="Секретный ключ"
              class="admin-input"
            />
          </div>

          <div v-if="auth.error" class="error-msg">{{ auth.error }}</div>

          <button type="submit" class="btn-primary" :disabled="auth.loading || passwordMismatch">
            <span v-if="auth.loading" class="spinner"></span>
            <span v-else>Создать аккаунт</span>
          </button>
        </form>

        <div class="auth-footer">
          Уже есть аккаунт?
          <RouterLink to="/login">Войти</RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, computed } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'

const auth = useAuthStore()
const router = useRouter()
const showAdminSecret = ref(false)
const form = reactive({ login: '', email: '', password: '', confirmPassword: '', adminSecret: '' })
const passwordMismatch = computed(() => form.confirmPassword.length > 0 && form.password !== form.confirmPassword)

async function handleRegister() {
  if (passwordMismatch.value) return
  const ok = await auth.doRegister(form.login, form.email, form.password, form.adminSecret || null)
  if (ok) router.push('/dashboard')
}
</script>

<style scoped>
.auth-layout {
  display: flex;
  min-height: 100vh;
}

.auth-left {
  flex: 1;
  background: var(--dark);
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 72px 64px;
  position: relative;
  overflow: hidden;
}

.auth-left::before {
  content: '';
  position: absolute;
  bottom: -100px;
  right: -100px;
  width: 400px;
  height: 400px;
  border-radius: 50%;
  border: 1px solid rgba(201, 168, 76, 0.1);
  pointer-events: none;
}

.auth-left::after {
  content: '';
  position: absolute;
  bottom: -40px;
  right: -40px;
  width: 250px;
  height: 250px;
  border-radius: 50%;
  border: 1px solid rgba(201, 168, 76, 0.07);
  pointer-events: none;
}

.brand {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-bottom: 36px;
}

.brand-name {
  font-family: var(--font-display);
  font-size: 56px;
  font-weight: 300;
  color: var(--cream);
  letter-spacing: -0.01em;
  line-height: 1;
}

.brand-tagline {
  font-size: 11px;
  color: var(--gold);
  letter-spacing: 3px;
  text-transform: uppercase;
  font-weight: 300;
}

.brand-line {
  width: 48px;
  height: 1px;
  background: var(--gold);
  margin-bottom: 32px;
  opacity: 0.7;
}

.brand-desc {
  font-size: 14px;
  color: rgba(247, 246, 243, 0.35);
  line-height: 1.8;
  max-width: 340px;
}

.auth-right {
  width: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
  background: var(--cream);
  overflow-y: auto;
}

.auth-card {
  width: 100%;
  max-width: 380px;
}

.auth-header {
  margin-bottom: 36px;
}

.auth-header h1 {
  font-family: var(--font-display);
  font-size: 34px;
  font-weight: 400;
  color: var(--dark);
  margin-bottom: 8px;
  letter-spacing: -0.01em;
}

.auth-header p {
  color: var(--muted);
  font-size: 14px;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.field label {
  font-size: 10px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  color: var(--muted);
  font-weight: 500;
}

.field input {
  padding: 14px 16px;
  border: 1px solid var(--border);
  background: var(--surface);
  border-radius: 4px;
  font-size: 15px;
  color: var(--dark);
  transition: border-color var(--transition);
  outline: none;
}

.field input:focus {
  border-color: var(--dark);
}

.field input::placeholder {
  color: #C5BDB5;
}

.field-error {
  font-size: 12px;
  color: var(--error);
}

.admin-field {
  border: 1px solid var(--border);
  border-radius: 4px;
  padding: 14px 16px;
  background: var(--surface);
}

.admin-toggle {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  user-select: none;
}

.toggle-label {
  font-size: 13px;
  color: var(--muted);
}

.toggle-icon {
  color: var(--gold);
  font-size: 18px;
  line-height: 1;
}

.admin-input {
  margin-top: 12px;
  padding: 12px 14px;
  border: 1px solid var(--border);
  background: var(--cream);
  border-radius: 4px;
  font-size: 14px;
  color: var(--dark);
  outline: none;
  width: 100%;
  transition: border-color var(--transition);
}

.admin-input:focus {
  border-color: var(--dark);
}

.error-msg {
  padding: 12px 16px;
  background: var(--error-bg);
  border: 1px solid var(--error-border);
  border-radius: 4px;
  color: var(--error);
  font-size: 13px;
}

.btn-primary {
  padding: 15px;
  background: var(--dark);
  color: var(--cream);
  -webkit-text-fill-color: var(--cream);
  background-clip: unset;
  -webkit-background-clip: unset;
  background-image: none;
  border: none;
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 2px;
  text-transform: uppercase;
  font-weight: 500;
  transition: opacity var(--transition);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 8px;
  cursor: pointer;
}

.btn-primary:hover:not(:disabled) {
  opacity: 0.85;
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(247, 246, 243, 0.3);
  border-top-color: var(--cream);
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.auth-footer {
  margin-top: 28px;
  text-align: center;
  font-size: 14px;
  color: var(--muted);
}

.auth-footer a {
  color: var(--dark);
  font-weight: 500;
  margin-left: 4px;
  border-bottom: 1px solid var(--border);
  transition: border-color var(--transition);
  opacity: 1;
}

.auth-footer a:hover {
  border-color: var(--dark);
  opacity: 1;
}

@media (max-width: 768px) {
  .auth-layout { flex-direction: column; }
  .auth-left { padding: 48px 28px; }
  .brand-name { font-size: 40px; }
  .auth-right { width: 100%; padding: 36px 24px; }
}
</style>
