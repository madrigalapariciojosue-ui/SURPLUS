package com.example.thesuerplus

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.core.content.ContextCompat

class RestaurantDetailActivity : AppCompatActivity() {

    private lateinit var restaurant: Restaurant
    private val cartItemCount = ShoppingCart.getItemCount()
    private val cartTotalPrice = ShoppingCart.getTotalPrice()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)

        // 1. Obtener el restaurante desde HomeActivity
        restaurant = intent.getSerializableExtra("RESTAURANT") as Restaurant

        // 2. Configurar la interfaz
        setupToolbar()
        displayRestaurantInfo()
        displayPacks()
        setupCartButton()
        updateCartSummary()
    }

    private fun setupToolbar() {
        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish()  // Regresa a HomeActivity
        }
    }

    private fun displayRestaurantInfo() {
        // Nombre del restaurante
        findViewById<TextView>(R.id.restaurantName).text = restaurant.name

        // Rating, distancia y tipo
        val infoText = "⭐ ${restaurant.rating} (${restaurant.reviewCount}) • 🛵 ${restaurant.distance} • 🍽️ ${restaurant.type}"
        findViewById<TextView>(R.id.restaurantInfo).text = infoText

        // Dirección
        findViewById<TextView>(R.id.restaurantAddress).text = restaurant.address

        // Imagen del restaurante
        val restaurantImage = findViewById<ImageView>(R.id.restaurantImage)
        restaurantImage.setImageResource(restaurant.imageRes)

        // Nota: El horario general ya está en el XML, puedes actualizarlo si quieres:
        // findViewById<TextView>(R.id.generalPickupTime).text = "Horario recogida general: ${restaurant.generalPickupWindow}"
    }

    private fun displayPacks() {
        val packsContainer = findViewById<LinearLayout>(R.id.packsContainer)
        packsContainer.removeAllViews()  // Limpiar por si acaso

        // Crear una vista para cada pack
        restaurant.packs.forEach { pack ->
            val packView = LayoutInflater.from(this).inflate(R.layout.item_pack, packsContainer, false)

            // Obtener referencias a los elementos del layout
            val packImage = packView.findViewById<ImageView>(R.id.packImage)
            val packName = packView.findViewById<TextView>(R.id.packName)
            val packDescription = packView.findViewById<TextView>(R.id.packDescription)
            val packContents = packView.findViewById<TextView>(R.id.packContents)
            val packPrice = packView.findViewById<TextView>(R.id.packPrice)
            val packAvailability = packView.findViewById<TextView>(R.id.packAvailability)
            val packPickupTime = packView.findViewById<TextView>(R.id.packPickupTime)
            val quantityText = packView.findViewById<TextView>(R.id.quantityText)
            val decreaseButton = packView.findViewById<TextView>(R.id.decreaseButton)
            val increaseButton = packView.findViewById<TextView>(R.id.increaseButton)
            val addToCartButton = packView.findViewById<TextView>(R.id.addToCartButton)

            // Asignar los datos del pack
            packImage.setImageResource(pack.imageRes)
            packName.text = pack.name
            packDescription.text = pack.description

            // Formatear la lista de contenidos
            val contentsText = "Contiene: " + pack.contents.joinToString(", ")
            packContents.text = contentsText

            // Mostrar precio con descuento
            packPrice.text = "~~$${pack.originalPrice.toInt()}~~ $${pack.discountPrice.toInt()} (${pack.discountPercentage}% OFF)"

            // Disponibilidad
            packAvailability.text = "🎁 ${pack.availableQuantity} disponibles"

            // Horario de recogida específico
            packPickupTime.text = "⏰ ${pack.pickupWindow}"

            // Lógica del selector de cantidad
            var quantity = 1
            quantityText.text = quantity.toString()

            decreaseButton.setOnClickListener {
                if (quantity > 1) {
                    quantity--
                    quantityText.text = quantity.toString()
                }
            }

            increaseButton.setOnClickListener {
                // No permitir más de la cantidad disponible
                if (quantity < pack.availableQuantity) {
                    quantity++
                    quantityText.text = quantity.toString()
                } else {
                    Toast.makeText(this, "No hay más unidades disponibles", Toast.LENGTH_SHORT).show()
                }
            }

            // Agregar al carrito
            addToCartButton.setOnClickListener {
                // Crear el item del carrito
                val cartItem = CartItem(
                    restaurantId = restaurant.id,
                    restaurantName = restaurant.name,
                    packId = pack.id,
                    packName = pack.name,
                    quantity = quantity,
                    unitPrice = pack.discountPrice,
                    pickupWindow = pack.pickupWindow,
                    packContents = pack.contents
                )

                // Agregar al carrito global
                ShoppingCart.addItem(cartItem)

                // Actualizar el resumen del carrito
                updateCartSummary()

                // Mostrar confirmación
                Toast.makeText(this, "${quantity}x ${pack.name} agregado al carrito", Toast.LENGTH_SHORT).show()

                // Resetear cantidad a 1
                quantity = 1
                quantityText.text = "1"
            }

            // Agregar la vista al contenedor
            packsContainer.addView(packView)
        }
    }

    private fun setupCartButton() {
        val viewCartButton = findViewById<TextView>(R.id.viewCartButton)
        viewCartButton.setOnClickListener {
            // Por ahora mostramos un mensaje, luego crearemos CartActivity
            Toast.makeText(this, "Funcionalidad de carrito en desarrollo", Toast.LENGTH_SHORT).show()


        }
    }

    private fun updateCartSummary() {
        val cartSummary = findViewById<TextView>(R.id.cartSummary)
        val cartTotal = findViewById<TextView>(R.id.cartTotal)

        val itemCount = ShoppingCart.getItemCount()
        val total = ShoppingCart.getTotalPrice()

        cartSummary.text = "Carrito: $itemCount items"
        cartTotal.text = "Total: $${String.format("%.2f", total)}"
    }

    override fun onResume() {
        super.onResume()
        // Actualizar el carrito cada vez que la actividad se vuelve visible
        updateCartSummary()
    }
}