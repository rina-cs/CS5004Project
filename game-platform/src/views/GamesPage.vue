<template>
    <v-container>
      <v-card class="pa-5">
        <v-card-title class="gradient-text">
          🎮 Games List
        </v-card-title>
        <v-text-field v-model="searchQuery" label="🔍 Search for a game..." class="mb-3"></v-text-field>
        <v-divider></v-divider>

        <v-row>
          <v-col v-for="game in filteredGames" :key="game.id" cols="12" md="4">
            <v-card class="game-card" elevation="6">
              <v-img :src="game.image" height="200px" cover contain></v-img>
              <v-card-title class="game-title">{{ game.name }}</v-card-title>
              <v-card-subtitle class="likes-text">🔥 {{ game.likes }} Likes</v-card-subtitle>
              <v-card-actions class="d-flex justify-center">
                <v-btn color="green" @click="addLike(game)">👍 Like</v-btn>
                <v-btn color="red" @click="removeLike(game)">👎 Unlike</v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-card>
    </v-container>
  </template>
  
  <script setup>
  import { ref, computed, onMounted } from "vue";
  
  const searchQuery = ref("");
  const user = ref(JSON.parse(localStorage.getItem("loggedInUser")) || { email: "Guest" });
  
  const games = ref([]);
  const userLikes = ref(JSON.parse(localStorage.getItem(`likes_${user.value.email}`)) || []);
  
  function loadGames() {
    const storedGames = JSON.parse(localStorage.getItem("games"));
    if (storedGames) {
      games.value = storedGames;
    } else {
      games.value = [
        { id: 1, name: "The Legend of Zelda", likes: 100, image: "/images/zelda.jpg" },
        { id: 2, name: "Super Mario Odyssey", likes: 120, image: "/images/mario.jpg" },
        { id: 3, name: "Minecraft", likes: 200, image: "/images/minecraft.jpg" },
        { id: 4, name: "Cyberpunk 2077", likes: 90, image: "/images/cyberpunk.jpg" },
        { id: 5, name: "Final Fantasy XIV", likes: 150, image: "/images/final-fantasy.jpg" },
        { id: 6, name: "Call of Duty", likes: 85, image: "/images/cod.jpg" },
        { id: 7, name: "Grand Theft Auto V", likes: 300, image: "/images/gta.jpg" },
        { id: 8, name: "Elden Ring", likes: 250, image: "/images/elden-ring.jpg" },
        { id: 9, name: "Hollow Knight", likes: 170, image: "/images/hollow-knight.jpg" },
        { id: 10, name: "Stardew Valley", likes: 190, image: "/images/stardew.jpg" },
      ];
      saveGames();
    }
  }
  
  function saveGames() {
    localStorage.setItem("games", JSON.stringify(games.value));
    localStorage.setItem(`likes_${user.value.email}`, JSON.stringify(userLikes.value));
  }
  
  function addLike(game) {
    game.likes++;
    if (!userLikes.value.includes(game.id)) {
      userLikes.value.push(game.id);
    }
    saveGames();
  }
  
  function removeLike(game) {
    if (game.likes > 0) {
      game.likes--;
      if (game.likes === 0) {
        userLikes.value = userLikes.value.filter((id) => id !== game.id);
      }
      saveGames();
    }
  }
  
  const filteredGames = computed(() =>
    games.value.filter((game) =>
      game.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  );
  
  onMounted(loadGames);
  </script>
  
  <style scoped>
  .gradient-text {
    font-size: 28px;
    font-weight: bold;
    display: inline-block;
    background-image: linear-gradient(45deg, #ff512f, #dd2476, #ff9a44);
    color: white;
    padding: 8px 15px;
    border-radius: 10px;
    box-shadow: 0 0 10px rgba(255, 81, 47, 0.6);
    text-transform: uppercase;
    text-align: center;
    letter-spacing: 2px;
    width: 100%;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
  }
  
  .game-title {
    font-family: "Orbitron", sans-serif;
    font-size: 20px;
    font-weight: bold;
    text-align: center;
    color: #ffeb3b;
  }
  
  .likes-text {
    font-family: "Orbitron", sans-serif;
    font-size: 14px;
    font-weight: bold;
    color: #ff9800;
    text-align: center;
  }
  
  .game-card {
    transition: transform 0.3s;
  }
  
  .game-card:hover {
    transform: scale(1.05);
    box-shadow: 0 8px 16px rgba(255, 87, 34, 0.5);
  }
  </style>
