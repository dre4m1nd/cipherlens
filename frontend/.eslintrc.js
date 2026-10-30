module.exports = {
  root: true,
  env: {
    node: true,
    browser: true,
    es2021: true
  },
  plugins: ['vue'],
  extends: ['eslint:recommended'],
  parser: 'vue-eslint-parser',
  parserOptions: {
    parser: '@babel/eslint-parser',
    ecmaVersion: 2021,
    sourceType: 'module',
    requireConfigFile: false
  },
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'vue/valid-v-model': 'off'
  }
}