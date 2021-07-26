import Vue from 'vue'
import Router from 'vue-router'
import LoginPage from '@/views/LoginPage'
import RegisterPage from '@/views/RegisterPage'
import BoardPage from '@/views/BoardPage'
import MainPage from '@/views/MainPage'
import mainpanel from '@/views/mainpanel/MainPanel'
import settings from '@/views/mainpanel/settings/SettingsPanel'
import report from '@/views/mainpanel/ReportPanel'

Vue.use(Router)

export default new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [{
    path: '',
    name: 'mainpage',
    component: MainPage,
    children: [
      {
        path: '',
        name: 'default',
        component: mainpanel
      },
      {
        path: 'settings',
        name: 'settings',
        component: settings
      },
      {
        path: 'report',
        name: 'report',
        component: report
      }
    ]
  }, {
    path: '/login',
    name: 'login',
    component: LoginPage
  }, {
    path: '/register',
    name: 'register',
    component: RegisterPage
  }, {
    path: '/board/:boardId',
    name: 'board',
    component: BoardPage
  }
  ]
})
