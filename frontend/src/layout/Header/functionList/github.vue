<template>
  <a
      href="https://github.com/cmdparkour/vue-admin-box"
      target="_blank"
      rel="noopener noreferrer"
      title="访问 GitHub 仓库"
      aria-label="访问 GitHub 仓库（新窗口打开）"
      class="icon-link"
  >
    <i class="sfont system-github text-[18px]" aria-hidden="true"></i>
  </a>
</template>

<script lang="js">
import { defineComponent } from 'vue'
export default defineComponent({

})
</script>

<style scoped>
.icon-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: inherit;
}

.icon-link:focus-visible {
  outline: 2px solid var(--system-primary-color);
  outline-offset: 2px;
  border-radius: 2px;
}
</style>
