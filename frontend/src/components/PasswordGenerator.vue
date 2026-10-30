<template>
  <el-dialog
      :model-value="modelValue"
      title="生成随机密码"
      width="540px"
      @update:model-value="(v) => emit('update:modelValue', v)"
  >
    <div class="gen-output">
      <el-input
          :model-value="password"
          readonly
          class="gen-input"
          aria-label="生成的密码"
          @focus="(e) => e.target.select()"
      />
      <el-button :disabled="!password" @click="copy">复制</el-button>
      <el-button :aria-label="'重新生成'" @click="generate">重新生成</el-button>
    </div>

    <PasswordStrength :password="password" />

    <!-- 只播报「已生成」，不把密码念出来（旁人在场时会被听到） -->
    <p class="sr-only" role="status" aria-live="polite">{{ liveMessage }}</p>

    <el-form label-position="top" class="gen-options" @submit.prevent>
      <el-form-item :label="`长度：${length}`" for="gen-length">
        <el-slider
            id="gen-length"
            v-model="length"
            :min="8"
            :max="64"
            :step="1"
            :show-tooltip="false"
            aria-label="密码长度"
            :aria-valuetext="length + ' 位'"
            @change="generate"
        />
      </el-form-item>
      <el-form-item label="包含字符">
        <div class="gen-checks">
          <el-checkbox v-model="useUpper" @change="generate">大写 A-Z</el-checkbox>
          <el-checkbox v-model="useLower" @change="generate">小写 a-z</el-checkbox>
          <el-checkbox v-model="useDigit" @change="generate">数字 0-9</el-checkbox>
          <el-checkbox v-model="useSymbol" @change="generate">符号 !@#$%</el-checkbox>
        </div>
        <p v-if="!canGenerate" class="gen-warn" role="alert">至少选择一种字符类型</p>
      </el-form-item>
      <el-form-item>
        <el-checkbox v-model="avoidAmbiguous" @change="generate">排除易混字符 0 O 1 l I</el-checkbox>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="emit('update:modelValue', false)">取消</el-button>
      <el-button type="primary" :disabled="!password" @click="use">使用此密码</el-button>
    </template>
  </el-dialog>
</template>

<script>
import { defineComponent, ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import PasswordStrength from '@/components/PasswordStrength.vue'

const UPPER = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ'
const LOWER = 'abcdefghijklmnopqrstuvwxyz'
const DIGIT = '0123456789'
const SYMBOL = '!@#$%^&*()-_=+[]{};:,.?'
const AMBIGUOUS = '0O1lI'

/** 无模偏的随机整数（rejection sampling） */
function randomInt(max) {
  const limit = Math.floor(0x100000000 / max) * max
  const buf = new Uint32Array(1)
  let value = 0
  do {
    crypto.getRandomValues(buf)
    value = buf[0]
  } while (value >= limit)
  return value % max
}

export default defineComponent({
  name: 'PasswordGenerator',
  components: { PasswordStrength },
  props: {
    modelValue: { type: Boolean, default: false }
  },
  emits: ['update:modelValue', 'use'],
  setup(props, { emit }) {
    const length = ref(16)
    const useUpper = ref(true)
    const useLower = ref(true)
    const useDigit = ref(true)
    const useSymbol = ref(true)
    const avoidAmbiguous = ref(true)
    const password = ref('')
    const liveMessage = ref('')

    const pools = computed(() => {
      const list = []
      if (useUpper.value) list.push(UPPER)
      if (useLower.value) list.push(LOWER)
      if (useDigit.value) list.push(DIGIT)
      if (useSymbol.value) list.push(SYMBOL)
      return list
    })

    const canGenerate = computed(() => pools.value.length > 0)

    const pick = (pool) => {
      const chars = avoidAmbiguous.value
          ? [...pool].filter((c) => !AMBIGUOUS.includes(c))
          : [...pool]
      return chars[randomInt(chars.length)]
    }

    const generate = () => {
      if (!canGenerate.value) {
        password.value = ''
        return
      }
      // 每种选中的字符集先各取一个，保证都出现；其余随机补足后打乱
      const chars = pools.value.map(pick)
      const all = pools.value.join('')
      while (chars.length < length.value) chars.push(pick(all))
      for (let i = chars.length - 1; i > 0; i--) {
        const j = randomInt(i + 1)
        const tmp = chars[i]
        chars[i] = chars[j]
        chars[j] = tmp
      }
      password.value = chars.slice(0, length.value).join('')
      liveMessage.value = '已生成 ' + length.value + ' 位新密码'
    }

    const copy = async () => {
      try {
        await navigator.clipboard.writeText(password.value)
        ElMessage.success('已复制到剪贴板')
      } catch (e) {
        ElMessage.warning('浏览器不允许自动复制，请手动选中复制')
      }
    }

    const use = () => {
      emit('use', password.value)
      emit('update:modelValue', false)
    }

    watch(
        () => props.modelValue,
        (visible) => {
          if (visible) generate()
        }
    )

    return {
      length, useUpper, useLower, useDigit, useSymbol, avoidAmbiguous,
      password, liveMessage, canGenerate, generate, copy, use
    }
  }
})
</script>

<style scoped>
.gen-output {
  display: flex;
  gap: 8px;
  align-items: center;
}

.gen-input {
  flex: 1 1 auto;
  min-width: 0;
}

.gen-output :deep(.el-button) {
  flex: none;
}

.gen-input :deep(input) {
  font-family: ui-monospace, 'SFMono-Regular', Consolas, monospace;
  letter-spacing: 0.04em;
}

.gen-options {
  margin-top: 8px;
}

.gen-checks {
  display: flex;
  flex-wrap: wrap;
  gap: 4px 20px;
}

.gen-warn {
  margin: 4px 0 0;
  font-size: 12px;
  color: var(--app-danger);
}
</style>
