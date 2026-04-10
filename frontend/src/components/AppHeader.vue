<template>
  <header class="header">
    <RouterLink to="/dashboard" class="brand-name">StayNova</RouterLink>
    <nav class="header-nav">
      <slot name="nav" />
    </nav>
    <div class="header-right">
      <slot name="right" />
    </div>
  </header>
</template>

<script setup>
import { RouterLink } from 'vue-router'
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 22px 56px;
  background: var(--dark);
  position: sticky;
  top: 0;
  z-index: 100;
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

.brand-name:hover {
  opacity: 0.8;
}

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
</style>
