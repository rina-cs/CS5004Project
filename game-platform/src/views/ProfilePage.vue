<template>
    <v-container>
      <v-card class="pa-5">
        <v-card-title class="text-h6">👤 User Profile</v-card-title>
        <v-divider></v-divider>
  
        <v-list v-if="user.email">
          <v-list-item>
            <v-list-item-title><strong>Email:</strong> {{ user.email }}</v-list-item-title>
          </v-list-item>
        </v-list>
  
        <v-card-title class="mt-4">❤️ Liked Games</v-card-title>
        <v-alert v-if="likedGames.length === 0" type="info">You haven't liked any games yet.</v-alert>
  
        <v-row>
          <v-col v-for="game in likedGames" :key="game.id" cols="12" md="4">
            <v-card class="liked-game-card" elevation="6">
              <v-img :src="game.image" height="150px" cover></v-img>
              <v-card-title>{{ game.name }}</v-card-title>
              <v-card-subtitle>🔥 {{ game.likes }} Likes</v-card-subtitle>
            </v-card>
          </v-col>
        </v-row>
      </v-card>
    </v-container>
  </template>
  
  <script setup>
  import { ref, watchEffect } from "vue";
  
  const user = ref(JSON.parse(localStorage.getItem("loggedInUser")) || { email: "Guest" });
  
  //Only Show Games the User Has Liked (AND Likes > 0)
  const likedGames = ref([]);
  
  watchEffect(() => {
    const allGames = JSON.parse(localStorage.getItem("games")) || [];
    const userLikes = JSON.parse(localStorage.getItem(`likes_${user.value.email}`)) || [];
  
    //Filter: Only show games the user has liked AND likes > 0
    likedGames.value = allGames.filter((g) => userLikes.includes(g.id) && g.likes > 0);
  });
  </script>
  