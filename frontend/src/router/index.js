import {createRouter, createWebHistory} from 'vue-router'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import DashboardView from '../views/DashboardView.vue'
import ProfileView from '../views/ProfileView.vue'
import RoomsView from '../views/RoomsView.vue'
import RoomDetailView from '../views/RoomDetailView.vue'
import ReservationsView from '../views/ReservationsView.vue'
import AdminRoomsView from '../views/admin/AdminRoomsView.vue'
import AdminReservationsView from '../views/admin/AdminReservationsView.vue'
import AdminHotelsView from '../views/admin/AdminHotelsView.vue'
import NotificationsView from '../views/NotificationsView.vue'
import HomeView from '../views/HomeView.vue'
import ReservationDetail from '../views/admin/ReservationDetail.vue'
import HotelDetailView from '../views/HotelDetailView.vue'
import PaymentView from '../views/PaymentView.vue'
import ServiceUnavailableView from '../views/ServiceUnavailableView.vue'

const routes = [
    {path: '/', component: HomeView},
    {path: '/login', component: LoginView, meta: {guest: true}},
    {path: '/register', component: RegisterView, meta: {guest: true}},
    {path: '/dashboard', component: DashboardView, meta: {auth: true}},
    {path: '/profile', component: ProfileView, meta: {auth: true}},
    {path: '/hotels/:id', component: HotelDetailView, meta: {auth: true}},
    {path: '/rooms', component: RoomsView, meta: {auth: true}},
    {path: '/rooms/:id', component: RoomDetailView, meta: {auth: true}},
    {path: '/reservations', component: ReservationsView, meta: {auth: true}},
    {path: '/admin/hotels', component: AdminHotelsView, meta: {auth: true, admin: true}},
    {path: '/admin/rooms', component: AdminRoomsView, meta: {auth: true, admin: true}},
    {path: '/admin/reservations', component: AdminReservationsView, meta: {auth: true, admin: true}},
    {path: '/admin/reservations/:id', component: ReservationDetail, meta: {auth: true, admin: true}},
    {path: '/notifications', component: NotificationsView, meta: {auth: true}},
    {path: '/payment/:sessionId', component: PaymentView, meta: {auth: true}},
    {path: '/fallback/:service', component: ServiceUnavailableView},
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    if (to.meta.auth && !token) return next('/register')
    if (to.meta.guest && token) return next('/dashboard')
    if (to.meta.admin) {
        try {
            const payload = JSON.parse(atob(token.split('.')[1]))
            const roles = payload.roles || []
            const hasAdminRole = roles.includes('ROLE_ADMIN') || roles.includes('ADMIN')
            if (!hasAdminRole) return next('/dashboard')
        } catch {
            return next('/dashboard')
        }
    }
    next()
})

export default router
