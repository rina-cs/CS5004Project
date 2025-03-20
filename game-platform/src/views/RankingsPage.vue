<template>
    <v-container>
      <v-card class="pa-5">
        <v-card-title class="text-h5">🏆 Game Rankings</v-card-title>
        <v-divider></v-divider>
        <v-row>
          <v-col cols="12">
            <canvas ref="chartCanvas"></canvas>
          </v-col>
        </v-row>
      </v-card>
    </v-container>
  </template>
  
  <script setup>
  import { ref, onMounted, watchEffect } from "vue";
  import { Chart, registerables } from "chart.js";
  
  Chart.register(...registerables);
  
  //Initialize Variables
  const games = ref([]);
  const chartCanvas = ref(null);
  let chartInstance = null;
  
  //Function to Load Games from localStorage
  function loadGames() {
    games.value = JSON.parse(localStorage.getItem("games")) || [];
  }
  
  //Function to Update the Chart**
  function updateChart() {
    if (!chartCanvas.value) return;
  
    if (chartInstance) chartInstance.destroy(); //Destroy previous instance
  
    const ctx = chartCanvas.value.getContext("2d");
    chartInstance = new Chart(ctx, {
      type: "bar",
      data: {
        labels: games.value.map((g) => g.name),
        datasets: [
          {
            label: "Total Likes",
            data: games.value.map((g) => g.likes),
            backgroundColor: [
              "#FF6384", "#36A2EB", "#FFCE56", "#4CAF50", "#FF5722",
              "#9C27B0", "#E91E63", "#2196F3", "#3F51B5", "#FFC107",
            ],
          },
        ],
      },
      options: {
        responsive: true,
        scales: {
          y: { beginAtZero: true },
        },
      },
    });
  }
  
  //Watch for Changes in Likes
  watchEffect(() => {
    loadGames();
    updateChart();
  });
  
  //Load Data When Page Mounts
  onMounted(() => {
    loadGames();
    updateChart();
  });
  </script>
  