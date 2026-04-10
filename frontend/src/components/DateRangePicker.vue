<template>
  <div class="drp">
    <div class="drp-header">
      <button class="drp-nav" @click="prevMonth">&#8249;</button>
      <span class="drp-month-label">{{ monthLabel }}</span>
      <button class="drp-nav" @click="nextMonth">&#8250;</button>
    </div>

    <div class="drp-grid">
      <div v-for="name in dayNames" :key="name" class="drp-day-name">{{ name }}</div>

      <div v-for="n in startOffset" :key="'empty-' + n" class="drp-cell drp-empty"></div>

      <div
        v-for="day in daysInMonth"
        :key="day.iso"
        class="drp-cell"
        :class="getCellClass(day.iso)"
        @click="handleClick(day.iso)"
        @mouseenter="hovered = day.iso"
        @mouseleave="hovered = null"
      >
        <span class="drp-cell-inner">{{ day.d }}</span>
      </div>
    </div>

    <div class="drp-labels">
      <div class="drp-label-item">
        <span class="drp-label-title">Заезд</span>
        <span class="drp-label-value">{{ modelValue.startDate ? formatDisplay(modelValue.startDate) : '—' }}</span>
      </div>
      <div class="drp-label-sep">→</div>
      <div class="drp-label-item">
        <span class="drp-label-title">Выезд</span>
        <span class="drp-label-value">{{ modelValue.endDate ? formatDisplay(modelValue.endDate) : '—' }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({ startDate: '', endDate: '' })
  },
  disabledRanges: {
    type: Array,
    default: () => []
  },
  minDate: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue'])

const today = new Date().toISOString().split('T')[0]
const hovered = ref(null)

const now = new Date()
const viewYear = ref(now.getFullYear())
const viewMonth = ref(now.getMonth())

const dayNames = ['Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб', 'Вс']

function prevMonth() {
  if (viewMonth.value === 0) {
    viewMonth.value = 11
    viewYear.value--
  } else {
    viewMonth.value--
  }
}

function nextMonth() {
  if (viewMonth.value === 11) {
    viewMonth.value = 0
    viewYear.value++
  } else {
    viewMonth.value++
  }
}

const monthLabel = computed(() => {
  return new Date(viewYear.value, viewMonth.value, 1)
    .toLocaleDateString('ru-RU', { month: 'long', year: 'numeric' })
})

const daysInMonth = computed(() => {
  const year = viewYear.value
  const month = viewMonth.value
  const count = new Date(year, month + 1, 0).getDate()
  return Array.from({ length: count }, (_, i) => {
    const d = i + 1
    const iso = `${year}-${String(month + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`
    return { d, iso }
  })
})

// Monday-based offset: 0 = Mon, 6 = Sun
const startOffset = computed(() => {
  const day = new Date(viewYear.value, viewMonth.value, 1).getDay()
  return day === 0 ? 6 : day - 1
})

function isDisabled(iso) {
  const min = props.minDate || today
  if (iso < min) return true
  return props.disabledRanges.some(r => iso >= r.startDate && iso < r.endDate)
}

function rangeHasDisabled(from, to) {
  if (!from || !to || from >= to) return false
  return props.disabledRanges.some(r => r.startDate < to && r.endDate > from)
}

function handleClick(iso) {
  if (isDisabled(iso)) return

  const s = props.modelValue.startDate
  const e = props.modelValue.endDate

  if (!s || (s && e)) {
    emit('update:modelValue', { startDate: iso, endDate: '' })
    return
  }

  if (iso < s) {
    emit('update:modelValue', { startDate: iso, endDate: '' })
    return
  }

  if (iso === s) {
    emit('update:modelValue', { startDate: '', endDate: '' })
    return
  }

  if (rangeHasDisabled(s, iso)) {
    emit('update:modelValue', { startDate: iso, endDate: '' })
  } else {
    emit('update:modelValue', { startDate: s, endDate: iso })
  }
}

const previewEnd = computed(() => {
  const s = props.modelValue.startDate
  const e = props.modelValue.endDate
  if (e) return null
  if (!s || !hovered.value || hovered.value <= s) return null
  if (rangeHasDisabled(s, hovered.value)) return null
  return hovered.value
})

function getCellClass(iso) {
  const s = props.modelValue.startDate
  const e = props.modelValue.endDate
  const pe = previewEnd.value

  const rangeEnd = e || pe

  return {
    'drp-disabled': isDisabled(iso),
    'drp-start': iso === s,
    'drp-end': e ? iso === e : iso === pe,
    'drp-in-range': s && rangeEnd && iso > s && iso < rangeEnd,
    'drp-preview': !e && pe && iso > s && iso <= pe,
    'drp-today': iso === today
  }
}

function formatDisplay(iso) {
  return new Date(iso + 'T00:00:00').toLocaleDateString('ru-RU')
}
</script>

<style scoped>
.drp {
  background: white;
  border: 1px solid var(--border);
  border-radius: 6px;
  padding: 16px;
  user-select: none;
}

.drp-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.drp-month-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--dark);
  text-transform: capitalize;
}

.drp-nav {
  background: none;
  border: none;
  font-size: 20px;
  color: var(--muted);
  cursor: pointer;
  padding: 2px 8px;
  border-radius: 4px;
  transition: background 0.15s;
  line-height: 1;
}

.drp-nav:hover {
  background: var(--cream);
  color: var(--dark);
}

.drp-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 2px;
}

.drp-day-name {
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.5px;
  text-transform: uppercase;
  color: var(--muted);
  text-align: center;
  padding: 4px 0 8px;
}

.drp-empty {
  pointer-events: none;
}

.drp-cell {
  aspect-ratio: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 4px;
  position: relative;
  transition: background 0.1s;
}

.drp-cell-inner {
  font-size: 13px;
  color: var(--dark);
  position: relative;
  z-index: 1;
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.drp-cell:not(.drp-disabled):not(.drp-start):not(.drp-end):hover .drp-cell-inner {
  background: var(--cream);
}

/* Disabled (booked) dates */
.drp-disabled {
  cursor: default;
  pointer-events: none;
}

.drp-disabled .drp-cell-inner {
  color: #c0b9b0;
  text-decoration: line-through;
  background: #f2eeea;
  border-radius: 50%;
}

/* Start date */
.drp-start .drp-cell-inner {
  background: var(--dark);
  color: var(--cream);
  border-radius: 50%;
}

/* End date */
.drp-end .drp-cell-inner {
  background: var(--dark);
  color: var(--cream);
  border-radius: 50%;
}

/* In range (between start and end) */
.drp-in-range {
  background: #f5ede0;
}

.drp-in-range .drp-cell-inner {
  color: #6b5b45;
}

/* Preview (hover) range */
.drp-preview {
  background: #faf4ed;
}

.drp-preview .drp-cell-inner {
  color: #8a7055;
}

/* Today indicator */
.drp-today:not(.drp-start):not(.drp-end) .drp-cell-inner {
  border: 1.5px solid var(--gold, #c9a96e);
}

/* Selected range legend */
.drp-labels {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid var(--border);
}

.drp-label-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.drp-label-title {
  font-size: 10px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  color: var(--muted);
  font-weight: 500;
}

.drp-label-value {
  font-size: 13px;
  color: var(--dark);
  font-weight: 500;
}

.drp-label-sep {
  font-size: 16px;
  color: var(--muted);
  margin-top: 8px;
}
</style>
