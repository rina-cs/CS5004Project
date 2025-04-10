import { createRouter, createWebHistory } from "vue-router";
import LoginPage from "../views/LoginPage.vue";
import RegisterPage from "../views/RegisterPage.vue";
import GamesPage from "../views/GamesPage.vue";
import ProfilePage from "../views/ProfilePage.vue";
import RankingsPage from "../views/RankingsPage.vue";
import CartPage from "@/views/CartPage.vue";

const routes = [
  { path: "/", component: LoginPage },
  { path: "/register", component: RegisterPage },
  { path: "/game", component: GamesPage},
  { path: "/profile", component: ProfilePage },
  { path: "/rankings", component: RankingsPage },
  { path: "/cart",component: CartPage}
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
