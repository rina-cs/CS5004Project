<template>
    <v-container class="fill-height d-flex align-center justify-center">
      <v-card class="pa-5" width="400">
        <v-card-title class="text-h5 text-center">🔑 Login</v-card-title>
        <v-card-text>
          <v-text-field label="Email" v-model="email" prepend-inner-icon="mdi-email"></v-text-field>
          <v-text-field label="Password" v-model="password" type="password" prepend-inner-icon="mdi-lock"></v-text-field>
          <v-alert v-if="errorMessage" type="error">{{ errorMessage }}</v-alert>
        </v-card-text>
        <v-card-actions>
          <v-btn block color="primary" @click="loginUser">🚀 Login</v-btn>
        </v-card-actions>
        <v-card-actions>
          <v-btn block text to="/register">Don't have an account? Register</v-btn>
        </v-card-actions>
      </v-card>
    </v-container>
  </template>
  
  <script setup>
  import { ref } from "vue";
  import { useRouter } from "vue-router";
  
  const email = ref("");
  const password = ref("");
  const errorMessage = ref("");
  const router = useRouter();

  async function loginUser() {
    try {
      const response = await fetch('api/users/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          email: email.value,
          password: password.value
        })
      });

      if (response.ok) {
        const userData = await response.json();
        localStorage.setItem("loggedInUser", JSON.stringify(userData));
        router.push("/games");
      } else {
        errorMessage.value = "Invalid email or password!";
      }
    } catch (error) {
      errorMessage.value = "Login failed. Please try again.";
      console.error('Login error:', error);
    }
  }
  </script>
  