<template>
  <div class="dashboard">
    <header class="header">
      <span class="brand-name">StayNova</span>
      <div class="header-right">
        <span class="user-login">{{ auth.login }}</span>
        <span v-if="auth.isAdmin" class="admin-badge">Администратор</span>
        <button class="btn-logout" @click="handleLogout" :disabled="auth.loading">
          {{ auth.loading ? '...' : 'Выйти' }}
        </button>
      </div>
    </header>

    <!-- GUEST VIEW -->
    <template v-if="!auth.isAdmin">
      <div class="hero">
        <div class="hero-inner">
          <div class="hero-label">Личный кабинет</div>
          <h1 class="hero-title">{{ auth.login }}</h1>
          <p class="hero-sub">Добро пожаловать в StayNova</p>
          <RouterLink to="/" class="hero-btn">Найти жильё</RouterLink>
        </div>
        <div class="hero-geo">
          <div class="geo-line"></div>
          <div class="geo-rect"></div>
        </div>
      </div>

      <div class="guest-main">
        <div class="quick-links">
          <RouterLink to="/" class="quick-card">
            <div class="quick-num">01</div>
            <div class="quick-info">
              <div class="quick-title">Найти жильё</div>
              <div class="quick-desc">Отели, апартаменты, виллы по всему миру</div>
            </div>
            <span class="quick-arrow">&#8594;</span>
          </RouterLink>

          <RouterLink to="/reservations" class="quick-card">
            <div class="quick-num">02</div>
            <div class="quick-info">
              <div class="quick-title">Мои бронирования</div>
              <div class="quick-desc">История и активные брони</div>
            </div>
            <span class="quick-arrow">&#8594;</span>
          </RouterLink>

          <RouterLink to="/profile" class="quick-card">
            <div class="quick-num">03</div>
            <div class="quick-info">
              <div class="quick-title">Профиль</div>
              <div class="quick-desc">Настройки аккаунта и уведомлений</div>
            </div>
            <span class="quick-arrow">&#8594;</span>
          </RouterLink>

          <RouterLink to="/notifications" class="quick-card">
            <div class="quick-num">04</div>
            <div class="quick-info">
              <div class="quick-title">Уведомления</div>
              <div class="quick-desc">События по вашему аккаунту</div>
            </div>
            <span class="quick-arrow">&#8594;</span>
          </RouterLink>
        </div>

        <div class="feature-strip">
          <div class="feature-item">
            <div class="feature-mark"></div>
            <div class="feature-title">Премиум сервис</div>
            <div class="feature-text">Круглосуточное обслуживание и забота о каждом госте</div>
          </div>
          <div class="feature-item">
            <div class="feature-mark"></div>
            <div class="feature-title">Безопасное бронирование</div>
            <div class="feature-text">Ваши данные под надёжной защитой</div>
          </div>
          <div class="feature-item">
            <div class="feature-mark"></div>
            <div class="feature-title">Email уведомления</div>
            <div class="feature-text">Подпишитесь в профиле и получайте обновления</div>
          </div>
        </div>
      </div>
    </template>

    <!-- ADMIN VIEW -->
    <template v-else>
      <main class="admin-main">
        <div class="admin-welcome">
          <div class="admin-label">Панель администратора</div>
          <h1 class="admin-title">{{ auth.login }}</h1>
        </div>

        <div class="admin-section">
          <div class="section-heading">Для гостей</div>
          <div class="admin-grid">
            <RouterLink to="/" class="admin-card">
              <div class="admin-card-num">01</div>
              <div class="admin-card-body">
                <div class="admin-card-title">Каталог жилья</div>
                <div class="admin-card-sub">Открыть каталог</div>
              </div>
              <span class="admin-card-arrow">&#8594;</span>
            </RouterLink>
            <RouterLink to="/reservations" class="admin-card">
              <div class="admin-card-num">02</div>
              <div class="admin-card-body">
                <div class="admin-card-title">Мои бронирования</div>
                <div class="admin-card-sub">Личная история</div>
              </div>
              <span class="admin-card-arrow">&#8594;</span>
            </RouterLink>
            <RouterLink to="/profile" class="admin-card">
              <div class="admin-card-num">03</div>
              <div class="admin-card-body">
                <div class="admin-card-title">Профиль</div>
                <div class="admin-card-sub">Настройки аккаунта</div>
              </div>
              <span class="admin-card-arrow">&#8594;</span>
            </RouterLink>
            <RouterLink to="/notifications" class="admin-card">
              <div class="admin-card-num">04</div>
              <div class="admin-card-body">
                <div class="admin-card-title">Уведомления</div>
                <div class="admin-card-sub">События аккаунта</div>
              </div>
              <span class="admin-card-arrow">&#8594;</span>
            </RouterLink>
          </div>
        </div>

        <div class="admin-section">
          <div class="section-heading section-heading--admin">Администрирование</div>
          <div class="admin-grid">
            <RouterLink to="/admin/hotels" class="admin-card admin-card--accent">
              <div class="admin-card-num">A1</div>
              <div class="admin-card-body">
                <div class="admin-card-title">Управление отелями</div>
                <div class="admin-card-sub">Создать / Редактировать</div>
              </div>
              <span class="admin-card-arrow">&#8594;</span>
            </RouterLink>
            <RouterLink to="/admin/rooms" class="admin-card admin-card--accent">
              <div class="admin-card-num">A2</div>
              <div class="admin-card-body">
                <div class="admin-card-title">Управление номерами</div>
                <div class="admin-card-sub">Создать / Удалить</div>
              </div>
              <span class="admin-card-arrow">&#8594;</span>
            </RouterLink>
            <RouterLink to="/admin/reservations" class="admin-card admin-card--accent">
              <div class="admin-card-num">A3</div>
              <div class="admin-card-body">
                <div class="admin-card-title">Все бронирования</div>
                <div class="admin-card-sub">Подтвердить / Отменить</div>
              </div>
              <span class="admin-card-arrow">&#8594;</span>
            </RouterLink>
            <div class="admin-card admin-card--disabled">
              <div class="admin-card-num">A4</div>
              <div class="admin-card-body">
                <div class="admin-card-title">Аналитика</div>
                <div class="admin-card-sub">Скоро</div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </template>
  </div>
</template>

<script setup>
import { useRouter, RouterLink } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'

const auth = useAuthStore()
const router = useRouter()

async function handleLogout() {
  await auth.doLogout()
  router.push('/login')
}
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
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-login {
  font-size: 13px;
  color: rgba(247, 246, 243, 0.45);
}

.admin-badge {
  padding: 3px 12px;
  background: transparent;
  color: var(--gold);
  border: 1px solid rgba(201, 168, 76, 0.4);
  border-radius: 20px;
  font-size: 10px;
  font-weight: 400;
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
  transition: all var(--transition);
  cursor: pointer;
}

.btn-logout:hover:not(:disabled) {
  border-color: var(--gold);
  color: var(--gold);
}

/* ── Guest Hero ── */
.hero {
  background: var(--dark);
  padding: 72px 56px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  overflow: hidden;
  position: relative;
}

.hero-inner { z-index: 1; }

.hero-label {
  font-size: 10px;
  letter-spacing: 3px;
  text-transform: uppercase;
  color: var(--gold);
  margin-bottom: 20px;
}

.hero-title {
  font-family: var(--font-display);
  font-size: 60px;
  font-weight: 300;
  color: var(--cream);
  line-height: 1;
  letter-spacing: -0.02em;
  margin-bottom: 14px;
}

.hero-sub {
  font-size: 14px;
  color: rgba(247, 246, 243, 0.4);
  margin-bottom: 36px;
}

.hero-btn {
  display: inline-block;
  padding: 13px 32px;
  background: var(--gold);
  color: var(--dark);
  border-radius: 4px;
  font-size: 11px;
  letter-spacing: 2px;
  text-transform: uppercase;
  font-weight: 500;
  transition: opacity var(--transition);
  text-decoration: none;
}

.hero-btn:hover {
  opacity: 0.88;
}

/* Geometric decoration */
.hero-geo {
  position: relative;
  width: 220px;
  height: 220px;
  flex-shrink: 0;
}

.geo-line {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border: 1px solid rgba(201, 168, 76, 0.12);
  border-radius: 50%;
}

.geo-rect {
  position: absolute;
  top: 20%;
  left: 20%;
  width: 60%;
  height: 60%;
  border: 1px solid rgba(201, 168, 76, 0.08);
  transform: rotate(45deg);
}

/* ── Guest main ── */
.guest-main {
  max-width: 900px;
  margin: 0 auto;
  padding: 56px 48px;
}

.quick-links {
  display: flex;
  flex-direction: column;
  gap: 2px;
  margin-bottom: 48px;
}

.quick-card {
  display: flex;
  align-items: center;
  gap: 24px;
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 22px 28px;
  text-decoration: none;
  color: inherit;
  transition: all 0.25s var(--ease-out);
}

.quick-card:hover {
  border-color: var(--dark);
  transform: translateX(4px);
  opacity: 1;
}

.quick-num {
  font-family: var(--font-display);
  font-size: 13px;
  color: var(--muted);
  width: 28px;
  flex-shrink: 0;
  letter-spacing: 1px;
}

.quick-info { flex: 1; }

.quick-title {
  font-size: 15px;
  font-weight: 500;
  color: var(--dark);
  margin-bottom: 3px;
}

.quick-desc {
  font-size: 12px;
  color: var(--muted);
  letter-spacing: 0.3px;
}

.quick-arrow {
  font-size: 16px;
  color: var(--muted);
  transition: color var(--transition), transform var(--transition);
}

.quick-card:hover .quick-arrow {
  color: var(--dark);
  transform: translateX(3px);
}

/* Feature strip */
.feature-strip {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1px;
  background: var(--border);
  border: 1px solid var(--border);
  border-radius: 6px;
  overflow: hidden;
}

.feature-item {
  background: var(--surface);
  padding: 28px 24px;
}

.feature-mark {
  width: 28px;
  height: 2px;
  background: var(--gold);
  margin-bottom: 16px;
  opacity: 0.7;
}

.feature-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--dark);
  margin-bottom: 8px;
}

.feature-text {
  font-size: 13px;
  color: var(--muted);
  line-height: 1.6;
}

/* ── Admin main ── */
.admin-main {
  max-width: 1100px;
  margin: 0 auto;
  padding: 60px 56px;
}

.admin-welcome {
  margin-bottom: 52px;
  padding-bottom: 40px;
  border-bottom: 1px solid var(--border);
}

.admin-label {
  font-size: 10px;
  letter-spacing: 3px;
  text-transform: uppercase;
  color: var(--muted);
  margin-bottom: 12px;
}

.admin-title {
  font-family: var(--font-display);
  font-size: 48px;
  font-weight: 300;
  color: var(--dark);
  letter-spacing: -0.02em;
}

.admin-section {
  margin-bottom: 48px;
}

.section-heading {
  font-size: 10px;
  letter-spacing: 2.5px;
  text-transform: uppercase;
  color: var(--muted);
  font-weight: 500;
  margin-bottom: 20px;
}

.section-heading--admin {
  color: var(--gold);
}

.admin-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.admin-card {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 24px 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  text-decoration: none;
  color: inherit;
  transition: all 0.25s var(--ease-out);
  position: relative;
  overflow: hidden;
}

.admin-card:not(.admin-card--disabled):hover {
  border-color: var(--dark);
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(17, 17, 17, 0.06);
  opacity: 1;
}

.admin-card--accent {
  border-color: rgba(201, 168, 76, 0.25);
}

.admin-card--accent:hover {
  border-color: var(--gold) !important;
}

.admin-card--disabled {
  opacity: 0.4;
  cursor: default;
}

.admin-card-num {
  font-family: var(--font-display);
  font-size: 13px;
  color: var(--muted);
  letter-spacing: 1px;
}

.admin-card-body { flex: 1; }

.admin-card-title {
  font-size: 14px;
  font-weight: 500;
  color: var(--dark);
  margin-bottom: 5px;
  line-height: 1.3;
}

.admin-card-sub {
  font-size: 11px;
  letter-spacing: 0.5px;
  color: var(--muted);
}

.admin-card-arrow {
  font-size: 16px;
  color: var(--muted);
  align-self: flex-end;
  transition: color var(--transition);
}

.admin-card:hover .admin-card-arrow {
  color: var(--dark);
}

@media (max-width: 900px) {
  .header { padding: 18px 24px; }
  .hero { padding: 48px 24px; flex-direction: column; align-items: flex-start; }
  .hero-geo { display: none; }
  .guest-main { padding: 36px 24px; }
  .feature-strip { grid-template-columns: 1fr; }
  .admin-main { padding: 40px 24px; }
  .admin-grid { grid-template-columns: repeat(2, 1fr); }
}

@media (max-width: 540px) {
  .admin-grid { grid-template-columns: 1fr; }
}
</style>
