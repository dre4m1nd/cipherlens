import { defineConfig } from 'windicss/helpers'

export default defineConfig({
  theme: {
    extend: {
      colors: {
        primary: '#409eff',
        'primary-text': '#fff',
        logo: '#f1f1f1',
        'logo-background': '#263445',
        'menu-text': '#bfcbd9',
        'menu-background': '#181f31',
        'menu-children-background': '#1f2d3d',
        'menu-submenu-active': '#fff',
        'menu-hover-background': '#203448',
        'header-background': '#fff',
        'header-text': '#303133',
        'header-breadcrumb-text': '#606266',
        'header-item-hover': 'rgba(0,0,0,.06)',
        'header-border': '#d8dce5',
        'header-tab-background': '#fff',
        'container-background': '#f0f2f5',
        'container-main-background': '#fff',
        'page-background': '#fff',
        'page-color': '#303133',
        'page-tip-color': 'rgba(0, 0, 0, 0.45)',
        'page-border-color': '#ebeef5',
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