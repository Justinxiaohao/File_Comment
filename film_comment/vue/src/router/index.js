import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      component: () => import("@/views/Manager.vue"),
      redirect: "/home",
      children: [
        {
          path: "person",
          component: () => import("@/views/manager/Person.vue"),
        },
        {
          path: "password",
          component: () => import("@/views/manager/Password.vue"),
        },
        { path: "home", component: () => import("@/views/manager/Home.vue") },
        { path: "admin", component: () => import("@/views/manager/Admin.vue") },
        {
          path: "notice",
          component: () => import("@/views/manager/Notice.vue"),
        },
        {
          path: "category",
          component: () => import("@/views/manager/Category.vue"),
        },
        { path: "film", component: () => import("@/views/manager/Film.vue") },
        {
          path: "comment",
          component: () => import("@/views/manager/Comment.vue"),
        },
        { path: "user", component: () => import("@/views/manager/User.vue") },
        {
          path: "filmView",
          component: () => import("@/views/manager/FilmView.vue"),
        },
        {
          path: "filmDetail",
          component: () => import("@/views/manager/FilmDetail.vue"),
        },
        { path: "hall", component: () => import("@/views/manager/Hall.vue") },
        { path: "seat", component: () => import("@/views/manager/Seat.vue") },
        {
          path: "screening",
          component: () => import("@/views/manager/Screening.vue"),
        },
        { path: "order", component: () => import("@/views/manager/Order.vue") },
        {
          path: "coupon",
          component: () => import("@/views/manager/Coupon.vue"),
        },
        {
          path: "userCoupon",
          component: () => import("@/views/manager/UserCoupon.vue"),
        },
        {
          path: "seatSelection",
          component: () => import("@/views/manager/SeatSelection.vue"),
        },
        {
          path: "payment",
          component: () => import("@/views/manager/Payment.vue"),
        },
      ],
    },
    { path: "/login", component: () => import("@/views/Login.vue") },
    { path: "/register", component: () => import("@/views/Register.vue") },
  ],
});

export default router;
