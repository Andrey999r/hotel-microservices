<template>
  <div class="page">
    <header class="header">
      <RouterLink to="/dashboard" class="brand-name">StayNova</RouterLink>
      <nav class="header-nav">
        <RouterLink to="/dashboard" class="nav-link">Главная</RouterLink>
        <RouterLink to="/reservations" class="nav-link">Бронирования</RouterLink>
      </nav>
      <div class="header-right">
        <span class="user-login">{{ auth.login }}</span>
        <button class="btn-logout" @click="handleLogout">Выйти</button>
      </div>
    </header>

    <main class="main">
      <div class="page-header">
        <div class="page-label">Аккаунт</div>
        <h1>Профиль</h1>
      </div>

      <div v-if="user.loading && !user.profile" class="state-msg">
        Загрузка...
      </div>

      <div v-else-if="user.profile" class="profile-grid">
        <!-- Avatar card -->
        <div class="card avatar-card">
          <div class="avatar-wrap">
            <img
              :src="user.profile.avatarUrl || defaultAvatar"
              alt="avatar"
              class="avatar-img"
              @error="onAvatarError"
            />
            <label class="avatar-overlay">
              <input type="file" accept="image/*" @change="handlePhotoUpload" hidden />
              <span>Изменить</span>
            </label>
          </div>
          <div class="avatar-info">
            <span class="avatar-login">{{ user.profile.login }}</span>
            <span class="avatar-hint">Нажмите на фото, чтобы изменить</span>
          </div>
        </div>

        <!-- Profile data card -->
        <div class="card info-card">
          <div class="card-label">Личные данные</div>

          <form @submit.prevent="handleUpdate" class="profile-form">
            <div class="field">
              <label>Логин</label>
              <input :value="user.profile.login" disabled class="disabled" />
            </div>

            <div class="field">
              <label>Email для уведомлений</label>
              <input v-model="form.email" type="email" placeholder="Введите email" />
            </div>

            <div class="field">
              <div class="toggle-row">
                <div>
                  <span class="toggle-title">Email уведомления</span>
                  <span class="toggle-desc">Получать уведомления о бронированиях</span>
                </div>
                <button
                  type="button"
                  class="toggle-btn"
                  :class="{ active: form.emailNotificationsEnabled }"
                  @click="form.emailNotificationsEnabled = !form.emailNotificationsEnabled"
                >
                  <span class="toggle-knob"></span>
                </button>
              </div>
            </div>

            <div v-if="user.error" class="error-msg">{{ user.error }}</div>
            <div v-if="saved" class="success-msg">Изменения сохранены</div>

            <button type="submit" class="btn-primary" :disabled="user.loading">
              <span v-if="user.loading" class="spinner"></span>
              <span v-else>Сохранить изменения</span>
            </button>
          </form>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, watch } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'
import { useUserStore } from '../stores/user.js'

const defaultAvatar = 'http://localhost:9000/users/avatars/default-avatar.png'
const auth = useAuthStore()
const user = useUserStore()
const router = useRouter()
const saved = ref(false)

const form = reactive({
  email: '',
  emailNotificationsEnabled: false
})

onMounted(async () => {
  await user.fetchProfile()
  if (user.profile) {
    form.email = user.profile.email || ''
    form.emailNotificationsEnabled = user.profile.emailNotificationsEnabled || false
  }
})

watch(() => user.profile, (p) => {
  if (p) {
    form.email = p.email || ''
    form.emailNotificationsEnabled = p.emailNotificationsEnabled || false
  }
})

async function handleUpdate() {
  const ok = await user.updateProfile({
    email: form.email || null,
    emailNotificationsEnabled: form.emailNotificationsEnabled
  })
  if (ok) {
    saved.value = true
    setTimeout(() => { saved.value = false }, 2500)
  }
}

async function handlePhotoUpload(e) {
  const file = e.target.files[0]
  if (!file) return
  await user.uploadPhoto(file)
}

function onAvatarError(e) {
  e.target.src = defaultAvatar
}

async function handleLogout() {
  await auth.doLogout()
  router.push('/login')
}
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

.user-login {
  font-size: 13px;
  color: rgba(247, 246, 243, 0.4);
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
  max-width: 960px;
  margin: 0 auto;
  padding: 64px 56px;
}

.page-header {
  margin-bottom: 48px;
}

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

/* ── Profile grid ── */
.profile-grid {
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 24px;
}

.card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 36px;
}

/* Avatar card */
.avatar-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.avatar-wrap {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
}

.avatar-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  background: rgba(17, 17, 17, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity var(--transition);
  cursor: pointer;
}

.avatar-wrap:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay span {
  color: var(--cream);
  font-size: 10px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
}

.avatar-info {
  text-align: center;
}

.avatar-login {
  display: block;
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: 400;
  color: var(--dark);
  margin-bottom: 6px;
}

.avatar-hint {
  font-size: 11px;
  color: var(--muted);
}

/* Info card */
.card-label {
  font-size: 10px;
  letter-spacing: 2px;
  text-transform: uppercase;
  color: var(--muted);
  font-weight: 500;
  margin-bottom: 32px;
}

.profile-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
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

.field input:focus { border-color: var(--dark); }

.field input::placeholder { color: #C5BDB5; }

.field input.disabled {
  background: var(--cream);
  color: var(--muted);
  cursor: not-allowed;
}

/* Toggle */
.toggle-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 16px;
  background: var(--cream);
  border-radius: 4px;
}

.toggle-title {
  display: block;
  font-size: 14px;
  color: var(--dark);
  margin-bottom: 3px;
}

.toggle-desc {
  font-size: 12px;
  color: var(--muted);
}

.toggle-btn {
  width: 44px;
  height: 24px;
  border-radius: 12px;
  border: none;
  background: var(--border);
  position: relative;
  cursor: pointer;
  transition: background var(--transition);
  flex-shrink: 0;
}

.toggle-btn.active {
  background: var(--dark);
}

.toggle-knob {
  position: absolute;
  top: 3px;
  left: 3px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: var(--surface);
  transition: transform var(--transition);
}

.toggle-btn.active .toggle-knob {
  transform: translateX(20px);
}

/* Messages */
.error-msg {
  padding: 12px 16px;
  background: var(--error-bg);
  border: 1px solid var(--error-border);
  border-radius: 4px;
  color: var(--error);
  font-size: 13px;
}

.success-msg {
  padding: 12px 16px;
  background: var(--success-bg);
  border: 1px solid var(--success-border);
  border-radius: 4px;
  color: var(--success);
  font-size: 13px;
}

.btn-primary {
  padding: 14px;
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
  cursor: pointer;
}

.btn-primary:hover:not(:disabled) { opacity: 0.85; }
.btn-primary:disabled { opacity: 0.5; cursor: not-allowed; }

.spinner {
  width: 15px;
  height: 15px;
  border: 2px solid rgba(247, 246, 243, 0.3);
  border-top-color: var(--cream);
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

@media (max-width: 768px) {
  .header { padding: 18px 24px; }
  .header-nav { display: none; }
  .main { padding: 40px 24px; }
  .profile-grid { grid-template-columns: 1fr; }
}
</style>
