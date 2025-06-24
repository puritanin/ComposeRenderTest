package com.app.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Content(
    modifier: Modifier = Modifier,
    userScrollEnabled: Boolean = true,
) {
    LazyVerticalGrid(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.White),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 64.dp),
        userScrollEnabled = userScrollEnabled,
    ) {
        repeat(5) {
            items(mockProducts) { product ->
                ProductCard(product)
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = product.color.copy(alpha = 0.05f))
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = product.icon,
                contentDescription = product.name,
                tint = Color.Gray,
                modifier = Modifier.size(64.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = product.name, fontWeight = FontWeight.Bold)
            Text(text = "${product.price} ₽", color = MaterialTheme.colorScheme.primary)
        }
    }
}

private data class Product(val name: String, val price: Int, val icon: ImageVector, val color: Color)

private val mockProducts = listOf(
    Product("Смартфон XYZ", 29999, Icons.Default.Phone, Color.Green),
    Product("Ноутбук Pro", 89999, Icons.Default.Laptop, Color.Yellow),
    Product("Наушники Elite", 12999, Icons.Default.Headphones, Color.Blue),
    Product("Умные часы V2", 15999, Icons.Default.Watch, Color.Red),
    Product("Планшет Max", 45999, Icons.Default.Tablet, Color.Green),
    Product("Фотоаппарат 4K", 34999, Icons.Default.Camera, Color.Yellow),
)
