<template>
  <div class="user-list">
    <h2>用户列表</h2>
    <div v-if="loading">加载中...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <ul v-else>
      <li v-for="user in users" :key="user.id">
        {{ user.name }} - {{ user.email }}
      </li>
    </ul>

    <div class="add-user">
      <h3>添加用户</h3>
      <input v-model="newUser.name" placeholder="姓名" />
      <input v-model="newUser.email" placeholder="邮箱" />
      <button @click="addUser">添加</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'UserList',
  data() {
    return {
      users: [],
      loading: true,
      error: null,
      newUser: { name: '', email: '' }
    }
  },
  mounted() {
    this.fetchUsers()
  },
  methods: {
    async fetchUsers() {
      try {
        const response = await axios.get('/api/users')
        this.users = response.data
      } catch (err) {
        this.error = '获取用户列表失败: ' + err.message
      } finally {
        this.loading = false
      }
    },
    async addUser() {
      if (!this.newUser.name || !this.newUser.email) return
      try {
        const response = await axios.post('/api/users', this.newUser)
        this.users.push(response.data)
        this.newUser = { name: '', email: '' }
      } catch (err) {
        this.error = '添加用户失败: ' + err.message
      }
    }
  }
}
</script>

<style scoped>
.user-list {
  max-width: 500px;
  margin: 0 auto;
}
ul {
  list-style: none;
  padding: 0;
}
li {
  padding: 10px;
  border-bottom: 1px solid #eee;
}
.add-user {
  margin-top: 20px;
}
input {
  margin: 5px;
  padding: 8px;
  width: 150px;
}
button {
  padding: 8px 16px;
  background: #42b983;
  color: white;
  border: none;
  cursor: pointer;
}
button:hover {
  background: #3aa876;
}
.error {
  color: red;
}
</style>
