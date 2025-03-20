<template>
    <v-container class="fill-height d-flex align-center justify-center">
      <v-card class="pa-5" width="400">
        <v-card-title class="text-h5 text-center">📝 Register</v-card-title>
        <v-card-text>
          <v-text-field label="Email" v-model="email" prepend-inner-icon="mdi-email"></v-text-field>
          <v-text-field label="Password" v-model="password" type="password" prepend-inner-icon="mdi-lock"></v-text-field>
          <v-text-field label="Confirm Password" v-model="confirmPassword" type="password" prepend-inner-icon="mdi-lock"></v-text-field>
          <v-alert v-if="errorMessage" type="error">{{ errorMessage }}</v-alert>
        </v-card-text>
        <v-card-actions>
          <v-btn block color="primary" @click="registerUser">✅ Register</v-btn>
        </v-card-actions>
        <v-card-actions>
          <v-btn block text to="/">Already have an account? Login</v-btn>
        </v-card-actions>
      </v-card>
    </v-container>
  </template>
  
  <script setup>
  import { ref } from "vue";
  import { useRouter } from "vue-router";
  
  const email = ref("");
  const password = ref("");
  const confirmPassword = ref("");
  const errorMessage = ref("");
  const router = useRouter();
  
  function registerUser() {
    if (!email.value || !password.value || !confirmPassword.value) {
      errorMessage.value = "All fields are required!";
      return;
    }
    if (password.value !== confirmPassword.value) {
      errorMessage.value = "Passwords do not match!";
      return;
    }
  
    const user = { email: email.value, password: password.value };
    localStorage.setItem("user", JSON.stringify(user));
  
    router.push("/");
  }
  </script>
  