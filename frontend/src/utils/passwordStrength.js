/**
 * 本地密码强度评估（不外传明文）
 * 只用于输入时的即时提示与可视化；权威评分仍以后端 SecurityEvaluationUtil 为准。
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

const SEQUENCE_RE = /(abc|bcd|cde|def|efg|fgh|ghi|hij|ijk|jkl|klm|lmn|mno|nop|opq|pqr|qrs|rst|stu|tuv|uvw|vwx|wxy|xyz|012|123|234|345|456|567|678|789)/i
const REVERSE_SEQUENCE_RE = /(cba|dcb|edc|fed|gfe|hgf|ihg|jih|kji|lkj|mlk|nml|onm|pon|qpo|rqp|srq|tsr|uts|vut|wvu|xwv|yxw|zyx|210|321|432|543|654|765|876|987)/i
const TRIPLE_REPEAT_RE = /(.)\1{2,}/

/** 常见连续/重复模式：abcd、4321、aaaa */
function hasSequence(pwd) {
  return SEQUENCE_RE.test(pwd) || REVERSE_SEQUENCE_RE.test(pwd) || TRIPLE_REPEAT_RE.test(pwd)
}

/**
 * 强度等级
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

/**
 * 五个维度的本地拆解，用于评估结果雷达图。
 * 与后端 SecurityEvaluationUtil 的口径不完全相同，仅作可视化参考。
 * @param {string} password
 * @returns {{name: string, value: number}[]}
 */
export function evaluateDimensions(password) {
  const pwd = password || ''
  const empty = [
    { name: '长度', value: 0 },
    { name: '大小写', value: 0 },
    { name: '数字', value: 0 },
    { name: '符号', value: 0 },
    { name: '无规律', value: 0 }
  ]
  if (!pwd) return empty

  const lenScore = Math.min(100, Math.round((pwd.length / 16) * 100))

  const hasLower = /[a-z]/.test(pwd)
  const hasUpper = /[A-Z]/.test(pwd)
  let caseScore = 0
  if (hasLower && hasUpper) caseScore = 100
  else if (hasLower || hasUpper) caseScore = 45

  const digits = (pwd.match(/\d/g) || []).length
  let digitScore = 0
  if (digits >= 2) digitScore = 100
  else if (digits === 1) digitScore = 65
  if (/^\d+$/.test(pwd)) digitScore = Math.min(digitScore, 30)

  const symbolScore = /[^A-Za-z0-9]/.test(pwd) ? 100 : 0

  let orderScore = 100
  if (hasSequence(pwd)) orderScore -= 45
  if (TRIPLE_REPEAT_RE.test(pwd)) orderScore -= 35
  if (COMMON_PASSWORDS.includes(pwd.toLowerCase())) orderScore = 0
  orderScore = Math.max(0, orderScore)

  return [
    { name: '长度', value: lenScore },
    { name: '大小写', value: caseScore },
    { name: '数字', value: digitScore },
    { name: '符号', value: symbolScore },
    { name: '无规律', value: orderScore }
  ]
}

export default { evaluateLocal, evaluateDimensions }
