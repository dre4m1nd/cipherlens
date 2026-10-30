<template>
  <div v-if="password" class="pwd-strength" :class="'lv-' + level.score">
    <div class="pwd-bars" aria-hidden="true">
      <span v-for="i in 4" :key="i" :class="{ on: i <= level.score }"></span>
    </div>
    <p class="pwd-meta" role="status" aria-live="polite">
      <span class="pwd-label">密码强度：{{ level.label }}</span>
      <span v-if="level.hint" class="pwd-hint">{{ level.hint }}</span>
    </p>
  </div>
</template>

<script>
import { defineComponent, computed } from 'vue'
import { evaluateLocal } from '@/utils/passwordStrength'

export default defineComponent({
  name: 'PasswordStrength',
  props: {
    password: { type: String, default: '' }
  },
  setup(props) {
    const level = computed(() => evaluateLocal(props.password))
    return { level }
  }
})
</script>

<style scoped>
.pwd-strength {
  margin: -8px 0 14px;
}

/* 四段条：颜色只表达强度，不做装饰 */
.pwd-bars {
  display: flex;
  gap: 4px;
}

.pwd-bars span {
  flex: 1;
  height: 3px;
  border-radius: 2px;
  background-color: var(--app-line);
  transition: background-color 0.15s ease;
}

.pwd-meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin: 6px 0 0;
  font-size: 12px;
  line-height: 1.5;
}

.pwd-label {
  color: var(--app-ink-soft);
}

.pwd-hint {
  color: var(--app-ink-soft);
}

.lv-1 .pwd-bars span.on,
.lv-2 .pwd-bars span.on {
  background-color: var(--app-danger);
}

.lv-3 .pwd-bars span.on {
  background-color: var(--app-warning);
}

.lv-4 .pwd-bars span.on {
  background-color: var(--app-success);
}

.lv-1 .pwd-label,
.lv-2 .pwd-label {
  color: var(--el-color-danger);
  font-weight: 500;
}

.lv-4 .pwd-label {
  color: var(--el-color-success);
  font-weight: 500;
}

@media (prefers-reduced-motion: reduce) {
  .pwd-bars span {
    transition: none;
  }
}
</style>
