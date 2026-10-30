import { defineConfig } from 'windicss/helpers'

export default defineConfig({
  theme: {
    extend: {
      colors: {
        primary: '#0f766e',
        'primary-text': '#fff',
        logo: '#eaf3f1',
        'logo-background': '#0f3b42',
        'menu-text': '#d7e4e2',
        'menu-background': '#0f3b42',
        'menu-children-background': '#14474f',
        'menu-submenu-active': '#fff',
        'menu-hover-background': '#1a5560',
        'header-background': '#fff',
        'header-text': '#303133',
        'header-breadcrumb-text': '#606266',
        'header-item-hover': 'rgba(0,0,0,.06)',
        'header-border': '#dde5e3',
        'header-tab-background': '#fff',
        'container-background': '#f1f5f4',
        'container-main-background': '#fff',
        'page-background': '#fff',
        'page-color': '#303133',
        'page-tip-color': 'rgba(0, 0, 0, 0.45)',
        'page-border-color': '#e3e9e7',
      },
      scrollbar: {
        thin: {
          '&::-webkit-scrollbar': {
            width: '6px',
          },
          '&::-webkit-scrollbar-thumb': {
            borderRadius: '10px',
            backgroundColor: 'rgba(144, 147, 153, 0.3)',
          },
        },
      },
    },
  },
  plugins: [
    require('@windicss/plugin-scrollbar'),
  ],
})