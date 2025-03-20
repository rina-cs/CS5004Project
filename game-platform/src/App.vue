<template>
  <v-app>
    <v-container>
      <v-app-bar flat>
        <v-toolbar-title class="app-title">Play, Vote, Dominate: The Best Game</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn text to="/">Login</v-btn>
        <v-btn text to="/games">Games</v-btn>
        <v-btn text to="/profile">Profile</v-btn>
        <v-btn text to="/rankings">Rankings</v-btn>
        <v-switch v-model="darkMode" label="🌙 Dark Mode" @change="toggleTheme"></v-switch>
      </v-app-bar>
      <router-view />
    </v-container>
  </v-app>
</template>

<script setup>
import { ref } from "vue";
import { useTheme } from "vuetify";

const darkMode = ref(false);
const theme = useTheme();

function toggleTheme() {
  theme.global.name.value = darkMode.value ? "dark" : "light";
}
</script>

<style scoped>
.app-title {
  font-family: "Orbitron", sans-serif;
  font-size: 18px;
  font-weight: bold;
  text-transform: uppercase;
}

/*Dark Mode*/
.v-switch {
  margin-left: 10px;
}
</style>
