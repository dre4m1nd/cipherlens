/**
 * 本地密码强度评估（不外传明文）
 * 只用于输入时的即时提示；权威评分仍以后端 SecurityEvaluationUtil 为准。
 */

// 高频弱密码样本，命中直接判为最弱
const COMMON_PASSWORDS = [
  '123456', '12345678', '123456789', 'password', 'password1', 'passw0rd',
  'qwerty', 'qwerty123', 'abc123', 'a123456', '111111', '000000', '666666',
  '888888', '123123', 'admin', 'admin123', 'root', 'letmein', 'welcome',
  'iloveyou', 'monkey', 'dragon', 'sunshine', 'princess', 'football',
  'baseball', 'superman', 'qazwsx', 'zxcvbnm', '1qaz2wsx', 'woaini1314'
]

const LEVELS = {
  0: { score: 0, label: '', hint: '' },
  1: { score: 1, label: '很弱', hint: '试试加长到 12 位以上' },
  2: { score: 2, label: '弱', hint: '混入大写字母或符号' },
  3: { score: 3, label: '中等', hint: '再加长一些会更稳' },
  4: { score: 4, label: '强', hint: '' }
}

/** 常见连续/重复模式：abcd、4321、aaaa */
function hasSequence(pwd) {
  const lower = pwd.toLowerCase()
  for (let i = 0; i < lower.length - 2; i++) {
    const a = lower.charCodeAt(i)
    const b = lower.charCodeAt(i + 1)
    const c = lower.charCodeAt(i + 2)
    if (b - a === 1 && c - b === 1) return true
    if (a - b === 1 && b - c === 1) return true
    if (a === b && b === c) return true
  }
  return false
}

/**
 * @param {string} password
 * @returns {{score: number, label: string, hint: string}}
 */
export function evaluateLocal(password) {
  const pwd = password || ''
  if (!pwd) return LEVELS[0]

  if (COMMON_PASSWORDS.includes(pwd.toLowerCase())) return LEVELS[1]

  let points = 0
  if (pwd.length >= 8) points++
  if (pwd.length >= 12) points++
  if (pwd.length >= 16) points++
  if (/[a-z]/.test(pwd) && /[A-Z]/.test(pwd)) points++
  if (/\d/.test(pwd)) points++
  if (/[^A-Za-z0-9]/.test(pwd)) points++

  // 单一字符重复、纯数字、连续序列都要扣分
  if (/^(.)\1+$/.test(pwd)) points = 1
  else if (/^\d+$/.test(pwd)) points = Math.min(points, 2)
  if (hasSequence(pwd)) points--

  let score
  if (points <= 1) score = 1
  else if (points === 2) score = 2
  else if (points === 3 || points === 4) score = 3
  else score = 4

  // 长度不够 8 位，无论多复杂都最多「弱」
  if (pwd.length < 8 && score > 2) score = 2

  return LEVELS[score]
}

export default { evaluateLocal }
