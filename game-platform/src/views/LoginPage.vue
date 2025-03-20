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
  
  function loginUser() {
    const user = JSON.parse(localStorage.getItem("user"));
    if (!user || user.email !== email.value || user.password !== password.value) {
      errorMessage.value = "Invalid email or password!";
      return;
    }
    localStorage.setItem("loggedInUser", JSON.stringify(user));
    router.push("/games");
  }
  </script>
  