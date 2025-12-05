package com.example.thesuerplus

import android.content.Intent
import java.io.Serializable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
class Homeactivity : AppCompatActivity() {
    private val restaurants = listOf(
        Restaurant(
            "Tacos Don José",
            "Mexicana",
            4.5,
            124,
            "1.2 km",
            "8:00 PM - 8:30 PM",
            180.0,
            65.0,
            3,
            R.drawable.restaurant_mexican
        ),
        Restaurant(
            "Pizza Napoli",
            "Italiana",
            4.3,
            89,
            "0.8 km",
            "9:00 PM - 9:30 PM",
            250.0,
            89.0,
            2,
            R.drawable.restaurant_pizza
        ),
        Restaurant(
            "Sushi Express",
            "Japonesa",
            4.7,
            156,
            "1.5 km",
            "8:30 PM - 9:00 PM",
            320.0,
            120.0,
            4,
            R.drawable.restaurant_sushi
        ),
        Restaurant(
            "Burger House",
            "Comida Rápida",
            4.2,
            67,
            "0.5 km",
            "7:00 PM - 7:45 PM",
            150.0,
            55.0,
            5,
            R.drawable.restaurant_burger
        )
    )


    private val featuredPacks = listOf(
        FeaturedPack("Pack Sorpresa Premium", "Varios restaurantes", 299.0, 99.0, R.drawable.pack_premium),
        FeaturedPack("Pack Postres", "Panadería Dulce", 120.0, 45.0, R.drawable.pack_desserts),
        FeaturedPack("Pack Familiar", "Comida para 4 personas", 450.0, 199.0, R.drawable.pack_family)
    )


    private val categories = listOf(
        Category("🍕", "Todos"),
        Category("🍣", "Asiática"),
        Category("🍔", "Rápida"),
        Category("☕", "Cafés"),
        Category("🍰", "Postres"),
        Category("+", "Más")
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homeactivity)

        initViews()
        setupBottomNavigation()
        setupClickListeners()
    }

    private fun initViews() {
        setupCategories()
        setupFeaturedPacks()
        setupRestaurants()
    }

    private fun setupCategories() {
        val categoriesLayout = findViewById<LinearLayout>(R.id.categoriesLayout)

        categories.forEach { category ->
            val categoryView = LayoutInflater.from(this).inflate(R.layout.item_category, categoriesLayout, false)

            val emojiText = categoryView.findViewById<TextView>(R.id.categoryEmoji)
            val nameText = categoryView.findViewById<TextView>(R.id.categoryName)

            emojiText.text = category.emoji
            nameText.text = category.name

            categoryView.setOnClickListener {

                Toast.makeText(this, "Categoría: ${category.name}", Toast.LENGTH_SHORT).show()
            }

            categoriesLayout.addView(categoryView)
        }
    }

    private fun setupFeaturedPacks() {
        val packsLayout = findViewById<LinearLayout>(R.id.packsLayout)

        featuredPacks.forEach { pack ->
            val packView = LayoutInflater.from(this).inflate(R.layout.item_featured_pack, packsLayout, false)

            val imageView = packView.findViewById<ImageView>(R.id.packImage)
            val titleText = packView.findViewById<TextView>(R.id.packTitle)
            val descText = packView.findViewById<TextView>(R.id.packDescription)
            val priceText = packView.findViewById<TextView>(R.id.packPrice)

            imageView.setImageResource(pack.imageRes)
            titleText.text = pack.title
            descText.text = pack.description
            priceText.text = "$${pack.discountPrice.toInt()}"

            packView.setOnClickListener {
                Toast.makeText(this, "Pack: ${pack.title}", Toast.LENGTH_SHORT).show()
            }

            packsLayout.addView(packView)
        }
    }

   private fun setupRestaurants() {
       val restaurantsLayout = findViewById<LinearLayout>(R.id.restaurantsLayout)

       restaurants.forEach { restaurant ->
           val restaurantView = LayoutInflater.from(this).inflate(R.layout.item_restaurant, restaurantsLayout, false)


            val imageView = restaurantView.findViewById<ImageView>(R.id.restaurantImage)
            val ratingText = restaurantView.findViewById<TextView>(R.id.ratingText)
            val nameText = restaurantView.findViewById<TextView>(R.id.restaurantName)
            val typeText = restaurantView.findViewById<TextView>(R.id.restaurantType)
            val timeText = restaurantView.findViewById<TextView>(R.id.pickupTime)
            val priceText = restaurantView.findViewById<TextView>(R.id.priceText)
            val packsText = restaurantView.findViewById<TextView>(R.id.availablePacks)

            imageView.setImageResource(restaurant.imageRes)
            ratingText.text = "⭐ ${restaurant.rating} (${restaurant.reviewCount}) • 🛵 ${restaurant.distance}"
            nameText.text = restaurant.name
            typeText.text = "🍽️ ${restaurant.type}"
            timeText.text = "⏰ Recoger entre: ${restaurant.pickupTime}"
            priceText.text = "💰 ~~$${restaurant.originalPrice.toInt()}~~ **$${restaurant.discountPrice.toInt()}** (${calculateDiscount(restaurant.originalPrice, restaurant.discountPrice)}% OFF)"
            packsText.text = "🎁 ${restaurant.availablePacks} packs disponibles"

            restaurantView.setOnClickListener {
                val intent = Intent(this, RestaurantDetailActivity::class.java)
                    intent.putExtra("RESTAURANT", restaurant)
                startActivity(intent)
            }

            restaurantsLayout.addView(restaurantView)
        }
    }
    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {

                    true
                }
                R.id.nav_search -> {

                    Toast.makeText(this, "Búsqueda", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_favorites -> {

                    val intent = Intent(this, FavoritesActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_orders -> {

                    val intent = Intent(this, OrdersActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_profile -> {

                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupClickListeners() {


        findViewById<ImageView>(R.id.cartButton).setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }


        findViewById<ImageView>(R.id.notificationsButton).setOnClickListener {
            Toast.makeText(this, "Notificaciones", Toast.LENGTH_SHORT).show()
        }


        findViewById<ImageView>(R.id.filterButton).setOnClickListener {
            showFilterDialog()
        }
    }

    private fun showFilterDialog() {
        val filterOptions = arrayOf("Todos", "Mexicana", "Italiana", "Japonesa", "China", "Rápida", "Postres")

        AlertDialog.Builder(this)
            .setTitle("Filtrar por tipo de comida")
            .setItems(filterOptions) { dialog, which ->
                Toast.makeText(this, "Filtro: ${filterOptions[which]}", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun calculateDiscount(original: Double, discount: Double): Int {
        return ((original - discount) / original * 100).toInt()
    }


    data class Restaurant(
        val name: String,
        val type: String,
        val rating: Double,
        val reviewCount: Int,
        val distance: String,
        val pickupTime: String,
        val originalPrice: Double,
        val discountPrice: Double,
        val availablePacks: Int,
        val imageRes: Int
    ) : Serializable

    data class FeaturedPack(
        val title: String,
        val description: String,
        val originalPrice: Double,
        val discountPrice: Double,
        val imageRes: Int
    )

    data class Category(
        val emoji: String,
        val name: String
    )

    }


object SampleData {


    val images = mapOf(
        "mexican" to R.drawable.restaurant_mexican,
        "pizza" to R.drawable.restaurant_pizza,
        "sushi" to R.drawable.restaurant_sushi,
        "burger" to R.drawable.restaurant_burger,
        "guisado_pack" to R.drawable.pack_guisado,
        "tacos_pack" to R.drawable.pack_tacos,
        "pizza_pack" to R.drawable.pack_pizza,
        "sushi_pack" to R.drawable.pack_sushi
    )

    // Lista de restaurantes de ejemplo
    fun getSampleRestaurants(): List<Restaurant> = listOf(
        Restaurant(
            id = "R001",
            name = "Tacos Don José",
            type = "Mexicana",
            rating = 4.5,
            reviewCount = 124,
            distance = "1.2 km",
            imageRes = images["mexican"] ?: R.drawable.restaurant_mexican,
            address = "Av. Revolución 456, CDMX",
            generalPickupWindow = "8:00 PM - 10:00 PM",
            packs = listOf(
                Pack(
                    id = "P001",
                    name = "Pack Guisado Tradicional",
                    description = "Guisado del día con acompañamientos",
                    contents = listOf("Pipián de pollo", "Arroz", "Frijoles refritos", "Tortillas de maíz", "Salsa verde"),
                    originalPrice = 180.0,
                    discountPrice = 65.0,
                    availableQuantity = 5,
                    category = "guisados",
                    pickupWindow = "8:00 PM - 8:30 PM",
                    imageRes = images["guisado_pack"] ?: R.drawable.arroz_frijol
                ),
                Pack(
                    id = "P002",
                    name = "Pack Tacos Variados",
                    description = "Selección de tacos del día",
                    contents = listOf("6 tacos variados (2 pastor, 2 bistec, 2 chorizo)", "Salsa roja", "Salsa verde", "Cebolla", "Cilantro", "Limones"),
                    originalPrice = 120.0,
                    discountPrice = 45.0,
                    availableQuantity = 8,
                    category = "tacos",
                    pickupWindow = "8:30 PM - 9:00 PM",
                    imageRes = images["tacos_pack"] ?: R.drawable.pack_tacos
                )
            )
        ),

        Restaurant(
            id = "R002",
            name = "Pizzería Napoli",
            type = "Italiana",
            rating = 4.3,
            reviewCount = 89,
            distance = "0.8 km",
            imageRes = images["pizza"] ?: R.drawable.restaurant_pizza,
            address = "Calle Roma 123, CDMX",
            generalPickupWindow = "8:30 PM - 10:00 PM",
            packs = listOf(
                Pack(
                    id = "P003",
                    name = "Pack Pizza del Día",
                    description = "Pizza grande con ingredientes frescos",
                    contents = listOf("Pizza grande (margherita o pepperoni)", "Refresco 500ml", "Ajo molido", "Chiles secos"),
                    originalPrice = 250.0,
                    discountPrice = 89.0,
                    availableQuantity = 3,
                    category = "pizza",
                    pickupWindow = "9:00 PM - 9:30 PM",
                    imageRes = images["pizza_pack"] ?: R.drawable.pack_pizza
                )
            )
        ),

        Restaurant(
            id = "R003",
            name = "Sushi Express",
            type = "Japonesa",
            rating = 4.7,
            reviewCount = 156,
            distance = "1.5 km",
            imageRes = images["sushi"] ?: R.drawable.restaurant_sushi,
            address = "Plaza Miyabi 89, CDMX",
            generalPickupWindow = "7:30 PM - 9:30 PM",
            packs = listOf(
                Pack(
                    id = "P004",
                    name = "Pack Sushi Variado",
                    description = "Combinado de rolls del día",
                    contents = listOf("8 piezas de sushi variado", "Salsa de soya", "Wasabi", "Jengibre", "Té verde"),
                    originalPrice = 320.0,
                    discountPrice = 120.0,
                    availableQuantity = 4,
                    category = "sushi",
                    pickupWindow = "8:00 PM - 8:45 PM",
                    imageRes = images["sushi_pack"] ?: R.drawable.sushi_ppack
                )
            )
        ),

        Restaurant(
            id = "R004",
            name = "Burger House",
            type = "Comida Rápida",
            rating = 4.2,
            reviewCount = 67,
            distance = "0.5 km",
            imageRes = images["burger"] ?: R.drawable.restaurant_burger,
            address = "Av. Insurgentes 789, CDMX",
            generalPickupWindow = "7:00 PM - 9:00 PM",
            packs = listOf(
                Pack(
                    id = "P005",
                    name = "Pack Burger Clásica",
                    description = "Hamburguesa con papas y refresco",
                    contents = listOf("Hamburguesa con queso", "Papas a la francesa", "Refresco 500ml", "Salsa especial"),
                    originalPrice = 150.0,
                    discountPrice = 55.0,
                    availableQuantity = 7,
                    category = "hamburguesas",
                    pickupWindow = "7:00 PM - 7:45 PM",
                    imageRes = R.drawable.hambuguesas_papas
                )
            )
        )
    )
}