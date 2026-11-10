package com.personalfinance.cuentas.domain.usecases

import com.personalfinance.cuentas.data.local.dao.CategoryDao
import com.personalfinance.cuentas.data.local.entities.BlockType
import com.personalfinance.cuentas.data.local.entities.CategoryEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrepopulateCategoriesUseCase @Inject constructor(
    private val categoryDao: CategoryDao
) {
    suspend operator fun invoke() {
        if (categoryDao.getCategoryCount() == 0) {
            val defaults = listOf(
                CategoryEntity(name = "Alimentación y Mercado", iconName = "restaurant", colorHex = "#FF5722", defaultBlockType = BlockType.NEEDS, isSystem = true),
                CategoryEntity(name = "Vivienda y Servicios", iconName = "home", colorHex = "#2196F3", defaultBlockType = BlockType.NEEDS, isSystem = true),
                CategoryEntity(name = "Transporte", iconName = "directions_car", colorHex = "#607D8B", defaultBlockType = BlockType.NEEDS, isSystem = true),
                CategoryEntity(name = "Salud y Farmacia", iconName = "medical_services", colorHex = "#E91E63", defaultBlockType = BlockType.NEEDS, isSystem = true),
                CategoryEntity(name = "Educación", iconName = "school", colorHex = "#009688", defaultBlockType = BlockType.NEEDS, isSystem = true),
                CategoryEntity(name = "Restaurantes y Salidas", iconName = "local_cafe", colorHex = "#FF9800", defaultBlockType = BlockType.WANTS, isSystem = true),
                CategoryEntity(name = "Ocio y Diversión", iconName = "sports_esports", colorHex = "#9C27B0", defaultBlockType = BlockType.WANTS, isSystem = true),
                CategoryEntity(name = "Suscripciones (Streaming)", iconName = "tv", colorHex = "#673AB7", defaultBlockType = BlockType.WANTS, isSystem = true),
                CategoryEntity(name = "Compras Personales", iconName = "shopping_bag", colorHex = "#3F51B5", defaultBlockType = BlockType.WANTS, isSystem = true),
                CategoryEntity(name = "Pago de Deudas y Tarjetas", iconName = "credit_card", colorHex = "#C62828", defaultBlockType = BlockType.DEBTS, isSystem = true),
                CategoryEntity(name = "Ahorro e Inversión", iconName = "savings", colorHex = "#2E7D32", defaultBlockType = BlockType.SAVINGS, isSystem = true),
                CategoryEntity(name = "Otros Gastos", iconName = "category", colorHex = "#795548", defaultBlockType = BlockType.NEEDS, isSystem = true)
            )
            categoryDao.insertCategories(defaults)
        }
    }
}
