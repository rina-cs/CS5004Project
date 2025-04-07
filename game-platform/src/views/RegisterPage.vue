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

  async function registerUser() {
    if (!email.value || !password.value || !confirmPassword.value) {
      errorMessage.value = "All fields are required!";
      return;
    }
    if (password.value !== confirmPassword.value) {
      errorMessage.value = "Passwords do not match!";
      return;
    }

    try {
      const response = await fetch('/api/users', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          email: email.value,
          username: email.value,
          password: password.value
        })
      });

    console.log("响应状态:", response.status);

    if (response.ok) {
      // 检查响应是否有内容
      const contentType = response.headers.get("content-type");
      if (contentType && contentType.includes("application/json")) {
        try {
          const data = await response.json();
          console.log("注册成功，返回数据:", data);
        } catch (e) {
          console.log("注册成功，但响应不是有效JSON");
        }
      } else {
        console.log("注册成功，无JSON响应");
      }

      // 无论如何都导航到登录页面
      router.push("/");
    } else {
      // 尝试读取错误信息
      try {
        const text = await response.text();
        console.error("错误响应内容:", text);

        if (text && text.trim().startsWith('{')) {
          const errorData = JSON.parse(text);
          errorMessage.value = errorData.message || "Registration failed";
        } else {
          errorMessage.value = "Registration failed: " + (text || response.statusText);
        }
      } catch (e) {
        errorMessage.value = "Registration failed: " + response.statusText;
      }
    }
  } catch (error) {
    errorMessage.value = "Connection error. Please try again.";
    console.error('Registration error:', error);
  }
  }
  </script>
  