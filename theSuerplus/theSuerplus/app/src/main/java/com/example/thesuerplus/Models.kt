package com.example.thesuerplus


import java.io.Serializable

data class Restaurant(
    val id: String,
    val name: String,
    val type: String,
    val rating: Double,
    val reviewCount: Int,
    val distance: String,
    val imageRes: Int,
    val address: String,
    val generalPickupWindow: String = "8:00 PM - 10:00 PM",
    val packs: List<Pack> = emptyList()
) : Serializable

data class Pack(
    val id: String,
    val name: String,
    val description: String,
    val contents: List<String>,
    val originalPrice: Double,
    val discountPrice: Double,
    val availableQuantity: Int,
    val category: String,
    val pickupWindow: String,
    val imageRes: Int
) : Serializable {
    val discountPercentage: Int
        get() = ((originalPrice - discountPrice) / originalPrice * 100).toInt()
}

data class CartItem(
    val restaurantId: String,
    val restaurantName: String,
    val packId: String,
    val packName: String,
    val quantity: Int,
    val unitPrice: Double,
    val pickupWindow: String,
    val packContents: List<String>
) : Serializable {
    val totalPrice: Double
        get() = unitPrice * quantity
}

object ShoppingCart {
    private val items = mutableListOf<CartItem>()

    fun addItem(item: CartItem) {
        val existingIndex = items.indexOfFirst {
            it.restaurantId == item.restaurantId && it.packId == item.packId
        }

        if (existingIndex != -1) {
            val existingItem = items[existingIndex]
            items[existingIndex] = existingItem.copy(
                quantity = existingItem.quantity + item.quantity
            )
        } else {
            items.add(item)
        }
    }

    fun removeItem(restaurantId: String, packId: String) {
        items.removeAll { it.restaurantId == restaurantId && it.packId == packId }
    }

    fun updateQuantity(restaurantId: String, packId: String, newQuantity: Int) {
        val index = items.indexOfFirst {
            it.restaurantId == restaurantId && it.packId == packId
        }
        if (index != -1 && newQuantity > 0) {
            items[index] = items[index].copy(quantity = newQuantity)
        } else if (newQuantity <= 0) {
            removeItem(restaurantId, packId)
        }
    }

    fun getItems(): List<CartItem> = items.toList()
    fun getTotalPrice(): Double = items.sumOf { it.totalPrice }
    fun clearCart() = items.clear()
    fun getItemCount(): Int = items.size
}