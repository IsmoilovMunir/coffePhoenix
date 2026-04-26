import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import './style.css'
import App from './App.vue'
import FranchiseView from './views/FranchiseView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: FranchiseView },
    { path: '/franchise', component: FranchiseView },
  ],
})

createApp(App).use(router).mount('#app')
