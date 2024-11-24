import { createRouter, createWebHistory } from "vue-router";
import Login from "../pages/Login.vue";
import Dashboard from "../pages/Dashboard.vue";
import Attendance from "../pages/Attendance.vue";
import Statistics from "../pages/Statistics.vue";
import Profile from "../pages/Profile.vue";
import Logout from '../pages/Logout.vue';

const routes = [
  {
    path: "/login",
    name: "Login",
    component: Login
  },
  {
    path: "/",
    //原来 path: "/",
    name: "Dashboard",
    component: Dashboard,
    children: [
      {
        path: "attendance",
        name: "Attendance",
        component: Attendance
      },
      {
        path: "statistics",
        name: "Statistics",
        component: Statistics
      },
      {
        path: "profile",
        name: "Profile",
        component: Profile
      },
      {
        path: "logout",
        name: "Logout",
        component: Logout
      }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')// 原来 next('/')
  } else {
    next()
  }
})

export default router;


