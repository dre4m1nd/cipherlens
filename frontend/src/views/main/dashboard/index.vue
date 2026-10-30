<template>
  <div class="dash">
    <header class="dash-head">
      <h1 class="dash-title">密鉴</h1>
      <p class="dash-sub">密码管理与安全评估平台</p>
    </header>

    <nav class="dash-rows" aria-label="功能入口">
      <router-link
          v-for="(item, i) in entries"
          :key="item.to"
          :to="item.to"
          class="dash-row"
      >
        <span class="dash-idx" aria-hidden="true">{{ String(i + 1).padStart(2, '0') }}</span>
        <span class="dash-body">
          <span class="dash-name">{{ item.title }}</span>
          <span class="dash-desc">{{ item.desc }}</span>
        </span>
        <span class="dash-go" aria-hidden="true">→</span>
      </router-link>
    </nav>
  </div>
</template>

<script setup>
const entries = [
  { to: '/password', title: '密码管理', desc: '集中管理各应用的账号密码，支持查看、编辑与删除' },
  { to: '/password-evaluate', title: '密码评估', desc: '实时评估密码强度，给出可执行的加固建议' },
  { to: '/log', title: '日志查询', desc: '记录每一次密码增删改查的操作轨迹' },
]
</script>

<style scoped>
.dash {
  text-align: left;
  max-width: 880px;
  margin: 0 auto;
  padding: 48px 32px 32px;
}

/* 页头：一条主张 + 一句说明，不加大色块 */
.dash-head {
  padding-bottom: 32px;
}

.dash-title {
  font-family: 'Songti SC', 'SimSun', serif;
  font-size: 40px;
  font-weight: 700;
  line-height: 1.1;
  letter-spacing: 0.04em;
  color: var(--app-ink);
  margin: 0;
}

.dash-sub {
  margin: 12px 0 0;
  font-size: 15px;
  letter-spacing: 0.06em;
  color: var(--app-ink-soft);
}

/* 行，而不是三张一样的卡片：固定列宽，分隔线承担结构 */
.dash-rows {
  border-top: 1px solid var(--app-line);
}

.dash-row {
  display: grid;
  grid-template-columns: 48px 1fr 24px;
  align-items: center;
  gap: 20px;
  padding: 22px 8px;
  border-bottom: 1px solid var(--app-line);
  border-radius: 4px;
  text-decoration: none;
  transition: background-color 0.15s ease, padding-left 0.15s ease;
}

.dash-row:hover {
  background-color: var(--app-row-hover);
  padding-left: 16px;
}

.dash-row:focus-visible {
  outline: 2px solid var(--app-accent);
  outline-offset: -2px;
}

/* 序号是排版对象，不是列表符号 */
.dash-idx {
  font-variant-numeric: tabular-nums;
  font-size: 13px;
  letter-spacing: 0.08em;
  color: var(--app-ink-soft);
}

.dash-body {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.dash-name {
  font-size: 19px;
  font-weight: 600;
  color: var(--app-ink);
}

.dash-desc {
  font-size: 13px;
  line-height: 1.6;
  color: var(--app-ink-soft);
}

.dash-go {
  font-size: 16px;
  color: var(--app-ink-mute);
  transition: color 0.15s ease, transform 0.15s ease;
}

.dash-row:hover .dash-go {
  color: var(--app-accent);
  transform: translateX(2px);
}

@media (max-width: 640px) {
  .dash {
    padding: 28px 16px 16px;
  }

  .dash-title {
    font-size: 30px;
  }

  .dash-row {
    grid-template-columns: 32px 1fr 20px;
    gap: 12px;
    padding: 18px 4px;
  }

  .dash-row:hover {
    padding-left: 8px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .dash-row,
  .dash-go {
    transition: none;
  }

  .dash-row:hover .dash-go {
    transform: none;
  }
}
</style>
