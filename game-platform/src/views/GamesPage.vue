<template>
    <v-container>
      <v-card class="pa-5">
        <v-card-title class="gradient-text">
          🎮 Games List
        </v-card-title>
        <v-row>
          <v-col cols="12" md="8">
            <v-text-field v-model="searchQuery" label="🔍 Search for a game..." class="mb-3"></v-text-field>
          </v-col>
          <v-col cols="12" md="4" class="d-flex align-center justify-end">
          </v-col>
        </v-row>
        <v-divider></v-divider>

        <v-row>
          <v-col v-for="game in filteredGames" :key="game.id" cols="12" md="4">
            <v-card class="game-card" elevation="6">
              <v-img :src="game.image" height="200px" cover contain></v-img>
              <v-card-title class="game-title">{{ game.name }}</v-card-title>
              <v-card-subtitle class="price-text">${{ game.price }}</v-card-subtitle>
              <v-card-subtitle class="likes-text">🔥 {{ game.likes }} Likes</v-card-subtitle>
              <v-card-actions class="d-flex justify-center">
                <v-btn color="green" @click="addLike(game)">👍 Like</v-btn>
                <v-btn color="red" @click="removeLike(game)">👎 Unlike</v-btn>
              </v-card-actions>
              <v-card-actions class="d-flex justify-center">
                <v-btn color="blue" block @click="addToCart(game)">
                  🛒 Add to Cart
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-card>
    </v-container>
  </template>
  
  <script setup>
  import { ref, computed, onMounted } from "vue";
  import { useRouter } from "vue-router";

  const searchQuery = ref("");
  const user = ref(JSON.parse(localStorage.getItem("loggedInUser")) || { email: "Guest" });
  const router = useRouter();
  const games = ref([]);
  const userLikes = ref(JSON.parse(localStorage.getItem(`likes_${user.value.email}`)) || []);


  onMounted(() => {
    loadGames();
  });

  async function loadGames() {
    try {
      const response = await fetch('/api/games');
      if (response.ok) {
        const data = await response.json();
        games.value = data;
      } else {
        games.value = [
          { id: 1, name: "The Legend of Zelda", price: 30.0, likes: 100, image: require("@/assets/images/zelda.jpg") },
          { id: 2, name: "Super Mario Odyssey", price: 20.0, likes: 120, image: require("@/assets/images/mario.jpg") },
          { id: 3, name: "Minecraft", price: 30.0, likes: 200, image: require("@/assets/images/minecraft.jpg") },
          { id: 4, name: "Cyberpunk 2077", price: 30.0, likes: 90, image: require("@/assets/images/cyberpunk.jpg") },
          { id: 5, name: "Final Fantasy XIV", price: 30.0, likes: 150, image: require("@/assets/images/final-fantasy.jpg") },
          { id: 6, name: "Call of Duty", price: 30.0, likes: 85, image: require("@/assets/images/cod.jpg") },
          { id: 7, name: "Grand Theft Auto V", price: 50.0, likes: 300, image: require("@/assets/images/gta.jpg") },
          { id: 8, name: "Elden Ring", price: 30.0, likes: 250, image: require("@/assets/images/elden-ring.jpg") },
          { id: 9, name: "Hollow Knight", price: 20.0, likes: 170, image: require("@/assets/images/hollow-knight.jpg") },
          { id: 10, name: "Stardew Valley", price: 15.0, likes: 190, image: require("@/assets/images/stardew.jpg") }
        ];
      }
    } catch (error) {
      console.error(error);
    }
  }
  async function addLike(game) {
    try {
      const response = await fetch(`/api/games/${game.id}/like`, {
        method: 'PUT'
      });
      if (response.ok) {
        const updatedGame = await response.json();
        const index = games.value.findIndex(g => g.id === game.id);
        if (index !== -1) {
          games.value[index] = updatedGame;
        }
      }
    } catch (error) {
      console.error('Error liking game:', error);
    }
  }

  function removeLike(game) {
    if (game.likes > 0) {
      game.likes--;
      if (game.likes === 0) {
        userLikes.value = userLikes.value.filter((id) => id !== game.id);
      }
    }
  }

  async function addToCart(game) {
    try {
      const userId = user.value.id;
      if (!userId) {
        alert("Please log in.");
        router.push("/login");
        return;
      }

      const response = await fetch(`/api/carts/${userId}/items`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          id: game.id,
          name: game.name,
          price: game.price,
          image: game.image
        })
      });


      if (response.ok) {
        alert('Game is added.');
      } else if (response.status === 400) {
        alert('You has already added this game.');
      } else {
        alert('Please try again.');
      }
    } catch (error) {
      alert('Connection failure.Please try again.');
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

  .price-text {
    font-family: "Orbitron", sans-serif;
    font-size: 16px;
    font-weight: bold;
    color: #4caf50;
    text-align: center;
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
