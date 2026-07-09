import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    user: JSON.parse(localStorage.getItem('userInfo') || '{}'),
    roles: [],
    permissions: [],
    menus: []
  }),
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },
    setUser(user) {
      this.user = user
      localStorage.setItem('userInfo', JSON.stringify(user))
    },
    setRoles(roles) {
      this.roles = roles
    },
    setPermissions(permissions) {
      this.permissions = permissions
    },
    setMenus(menus) {
      this.menus = menus
    },
    logout() {
      this.token = ''
      this.user = {}
      this.roles = []
      this.permissions = []
      this.menus = []
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
})
