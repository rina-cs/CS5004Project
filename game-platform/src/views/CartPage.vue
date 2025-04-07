<template>
  <v-container>
    <v-card class="pa-5">
      <v-card-title class="gradient-text">
        🛒 Shopping Cart
      </v-card-title>
      <v-row class="mb-4">
        <v-col cols="12" class="d-flex align-center">
          <v-btn color="orange" @click="goToGames" class="mr-2">
            <v-icon>mdi-arrow-left</v-icon> Back to Games
          </v-btn>
        </v-col>
      </v-row>
      <v-divider></v-divider>

      <div v-if="cart.length === 0" class="text-center py-8">
        <v-icon size="64" color="grey">mdi-cart-off</v-icon>
        <h2 class="empty-cart-text mt-4">Your cart is empty</h2>
        <p class="mt-2">Explore our game collection and add some awesome games!</p>
        <v-btn color="primary" @click="goToGames" class="mt-4">Browse Games</v-btn>
      </div>

      <template v-else>
        <!-- Cart Items with Checkboxes -->
        <v-row>
          <v-col cols="12">
            <v-data-table
                v-model="selectedItems"
                :headers="headers"
                :items="cart"
                item-value="id"
                show-select
                class="cart-table"
            >
              <!-- Game Image and Name -->
              <template #[`item.game`]="{ item }">
                <div class="d-flex align-center">
                  <v-img :src="item.image" width="50" height="50" class="mr-3" contain></v-img>
                  <div class="game-title">{{ item.name }}</div>
                </div>
              </template>

              <!-- Price Column (still solving price == 0 problem so a safety check is added) -->
              <template #[`item.price`]="{ item }">
                <div class="price-text">${{ (item.price !== undefined ? item.price : 0).toFixed(2) }}</div>
              </template>

              <!-- Subtotal Column (still solving price == 0 problem so a safety check is added)  -->
              <template #[`item.subtotal`]="{ item }">
                <div class="subtotal-text">${{ ((item.price !== undefined ? item.price : 0) * item.quantity).toFixed(2) }}</div>
              </template>

              <!-- Actions Column -->
              <template #[`item.actions`]="{ item }">
                <v-btn color="error" size="small" @click="removeFromCart(item)">
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
              </template>
            </v-data-table>
          </v-col>
        </v-row>

        <!-- Order Summary Card -->
        <v-row class="mt-4">
          <v-col cols="12" md="6" offset-md="6">
            <v-card class="summary-card" elevation="4">
              <v-card-title class="summary-title">Order Summary</v-card-title>
              <v-divider></v-divider>
              <v-card-text>
                <div class="d-flex justify-space-between mb-2">
                  <span>Selected Items:</span>
                  <span>{{ selectedItems.length }}</span>
                </div>
                <div class="d-flex justify-space-between mb-2">
                  <span>Subtotal:</span>
                  <span>${{ selectedSubtotal.toFixed(2) }}</span>
                </div>
                <v-divider class="my-3"></v-divider>
                <div class="d-flex justify-space-between total-row">
                  <span class="font-weight-bold">Total:</span>
                  <span class="font-weight-bold">${{ selectedTotal.toFixed(2) }}</span>
                </div>
              </v-card-text>
              <v-card-actions>
                <v-btn
                    color="green"
                    block
                    @click="checkout"
                    :disabled="selectedItems.length === 0"
                    class="checkout-btn"
                >
                  Checkout Selected Items
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>

        <!-- Cart Action Buttons -->
        <v-row class="mt-4">
          <v-col cols="12" class="d-flex">
            <v-btn color="blue" @click="selectAll" class="mr-2">
              Select All
            </v-btn>
            <v-btn color="orange" @click="deselectAll" class="mr-2">
              Deselect All
            </v-btn>
            <v-btn color="red" @click="clearCart">
              Clear Cart
            </v-btn>
            <v-spacer></v-spacer>
            <v-btn color="primary" @click="goToGames">
              Continue Shopping
            </v-btn>
          </v-col>
        </v-row>
      </template>
    </v-card>
  </v-container>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const user = ref(JSON.parse(localStorage.getItem("loggedInUser")) || { email: "Guest" });
const cart = ref([]);
const selectedItems = ref([]);
const loading = ref(false);

const headers = [
  { title: 'Game', key: 'game', align: 'start' },
  { title: 'Price', key: 'price', align: 'center' },
  { title: 'Delete', key: 'actions', align: 'center', sortable: false },
];

async function loadCart() {
  loading.value = true;
  try {
    if (!user.value.id) {
      cart.value = [];
      return;
    }

    const response = await fetch(`/api/carts/${user.value.id}`);

    if (response.ok) {
      const responseText = await response.text();

      try {
        console.log("Raw responseText:", responseText);
        const cartData = JSON.parse(responseText);

        if (cartData && Array.isArray(cartData.items)) {
          cart.value = cartData.items.map(item => ({
            id: item.id,
            gameId: item.gameId,
            name: item.name || item.gameName,
            price: item.price || 0,
            quantity: item.quantity,
            image: item.image || "/images/default.jpg"
          }));
        } else {
          cart.value = [];
        }
      } catch (parseError) {
        cart.value = [];
      }
    } else {
      cart.value = [];
    }
  } catch (error) {
    cart.value = [];
  } finally {
    loading.value = false;
  }
}


function saveCart() {
  localStorage.setItem(`cart_${user.value.email}`, JSON.stringify(cart.value));
}

function selectAll() {
  selectedItems.value = [...cart.value];
}

function deselectAll() {
  selectedItems.value = [];
}

async function removeFromCart(item) {
  try {
    const userId = user.value.id;
    if (!userId) {
      alert("Please log in to remove items from your cart.");
      return;
    }


    const gameId = item.gameId;

    const response = await fetch(`/api/carts/${userId}/items/${gameId}`, {
      method: 'DELETE'
    });

    if (response.ok) {
      await loadCart();
      selectedItems.value = selectedItems.value.filter(selected => {
        const selectedId = typeof selected === 'object' ? selected.gameId : selected;
        return selectedId !== item.gameId;
      });
      alert(`"${item.name}" has been removed from your cart.`);
    } else {
      const errorData = await response.json().catch(() => null);
      const errorMessage = errorData?.message || 'Failed to remove item from cart.';
      alert(errorMessage);
    }
  } catch (error) {
    console.error('Error removing item from cart:', error);
  }finally {
    loading.value = false;
  }
}

async function clearCart() {
  if (confirm("Are you sure you want to clear your cart?")) {
    try {
      const userId = user.value.id;
      const response = await fetch(`/api/carts/${userId}`, {
        method: 'DELETE'
      });

      if (response.ok) {
        cart.value = [];
        selectedItems.value = [];
      }
    } catch (error) {
      console.error('Error clearing cart:', error);
    }
  }
}

function checkout() {
  if (selectedItems.value.length === 0) {
    alert("Please select at least one item to checkout");
    return;
  }

  alert(`Thank you for your purchase! Total: $${selectedTotal.value.toFixed(2)}`);

  const checkoutItemIds = selectedItems.value.map(item => item.id);
  cart.value = cart.value.filter(item => !checkoutItemIds.includes(item.id));
  selectedItems.value = [];
  saveCart();
}
const selectedSubtotal = computed(() => {
  let total = 0;
  for (const selectedItem of selectedItems.value) {
    const itemId = typeof selectedItem === 'object' ? selectedItem.id : selectedItem;

    const item = cart.value.find(cartItem => cartItem.id === itemId);
    if (item && typeof item.price === 'number' && typeof item.quantity === 'number') {
      total += item.price * item.quantity;
    }
  }
  return total;
});


const selectedTotal = computed(() => {
  return selectedSubtotal.value;
});

function goToGames() {
  router.push('/games');
}

onMounted(() => {
  loadCart();
});
</script>

<style scoped>
.gradient-text {
  font-size: 28px;
  font-weight: bold;
  display: inline-block;
  background-image: linear-gradient(45deg, #7b4397, #dc2430);
  color: white;
  padding: 8px 15px;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(123, 67, 151, 0.6);
  text-transform: uppercase;
  text-align: center;
  letter-spacing: 2px;
  width: 100%;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.game-title {
  font-family: "Orbitron", sans-serif;
  font-size: 16px;
  font-weight: bold;
  color: #673ab7;
}

.price-text {
  font-family: "Orbitron", sans-serif;
  font-size: 16px;
  font-weight: bold;
  color: #4caf50;
}

.subtotal-text {
  font-family: "Orbitron", sans-serif;
  font-size: 16px;
  font-weight: bold;
  color: #ff9800;
}

.cart-table {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.summary-card {
  background-color: #f8f9fa;
  border-radius: 10px;
  overflow: hidden;
}

.summary-title {
  font-family: "Orbitron", sans-serif;
  background-color: #673ab7;
  color: white;
  text-align: center;
}

.total-row {
  font-size: 1.2rem;
  color: #673ab7;
}

.checkout-btn {
  font-family: "Orbitron", sans-serif;
  font-weight: bold;
  background-color: #673ab7;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.empty-cart-text {
  font-family: "Orbitron", sans-serif;
  color: #9e9e9e;
}
</style>