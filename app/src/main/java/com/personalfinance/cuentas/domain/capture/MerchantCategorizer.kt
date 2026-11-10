package com.personalfinance.cuentas.domain.capture

import com.personalfinance.cuentas.data.local.dao.CategoryDao
import com.personalfinance.cuentas.data.local.dao.MerchantLearningDao
import com.personalfinance.cuentas.data.local.entities.CategoryEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MerchantCategorizer @Inject constructor(
    private val categoryDao: CategoryDao,
    private val merchantLearningDao: MerchantLearningDao
) {

    /**
     * Identifica la categoría más adecuada para un comercio o concepto detectado en Colombia.
     */
    suspend fun categorizeMerchant(merchant: String): CategoryEntity? {
        val allCategories = categoryDao.getAllCategoriesSync()
        if (allCategories.isEmpty()) return null

        val normalized = normalize(merchant)

        // 1. Revisar si el usuario ya le había asignado una categoría a este comercio anteriormente
        val learned = merchantLearningDao.getLearningForMerchant(normalized)
        if (learned != null) {
            val matchedCategory = allCategories.firstOrNull { it.id == learned.categoryId }
            if (matchedCategory != null) {
                merchantLearningDao.incrementHitCount(normalized)
                return matchedCategory
            }
        }

        // 2. Diccionario semántico de comercios y servicios cotidianos en Colombia
        val matchedCategoryName = findCategoryByKeywords(normalized)
        if (matchedCategoryName != null) {
            val matched = allCategories.firstOrNull { it.name.contains(matchedCategoryName, ignoreCase = true) }
            if (matched != null) return matched
        }

        // 3. Fallback: primera categoría o categoría "Otros" o "Varios"
        return allCategories.firstOrNull { it.name.contains("Otros", ignoreCase = true) || it.name.contains("Varios", ignoreCase = true) }
            ?: allCategories.firstOrNull()
    }

    fun findCategoryByKeywords(normalized: String): String? {
        return when {
            // Supermercado / Mercado
            containsAny(normalized, "d1", "exito", "carulla", "jumbo", "olimpica", "ara", "colsubsidio", "zapatoca", "surtimax", "fruver", "carnes", "verduras", "supermercado", "tienda", "papelera", "panera", "cafe") -> "Alimentación"

            // Restaurantes y Comida
            containsAny(normalized, "rappi", "mcdonald", "kfc", "crepes", "frisby", "corral", "pizza", "starbucks", "juan valdez", "tostao", "panaderia", "asadero", "restaurante", "burger", "subway", "kokoriko", "dominos", "cafe", "almuerzo", "panera") -> "Alimentación"

            // Transferencias e Ingresos
            containsAny(normalized, "nequi", "daviplata", "transferencia", "abono", "intereses", "sueldo", "nomina", "suc virtual") -> "Otros"

            // Transporte
            containsAny(normalized, "uber", "didi", "indrive", "cabify", "transmilenio", "metro", "taxi", "peaje", "terpel", "primax", "texaco", "esso", "mobil", "gasolina", "combustible", "parqueadero") -> "Transporte"

            // Servicios Públicos y Facturas
            containsAny(normalized, "epm", "enel", "codensa", "acueducto", "gas", "vanti", "claro", "tigo", "movistar", "etb", "une", "celsia", "luz", "agua", "factura", "servicios") -> "Servicios"

            // Entretenimiento y Suscripciones
            containsAny(normalized, "netflix", "spotify", "amazon", "prime", "disney", "hbo", "max", "cine", "cinemark", "cine colombia", "apple", "youtube", "steam", "playstation", "xbox", "boleta") -> "Entretenimiento"

            // Salud y Farmacia
            containsAny(normalized, "drogas", "drogueria", "farmacia", "cruz verde", "pasteur", "farmatodo", "medico", "odontologia", "eps", "sura", "colsanitas", "clinica", "hospital") -> "Salud"

            // Educación
            containsAny(normalized, "universidad", "colegio", "platzi", "udemy", "coursera", "libros", "matricula", "pension", "instituto") -> "Educación"

            // Hogar y Ropa
            containsAny(normalized, "homecenter", "falabella", "zara", "bershka", "h&m", "koaj", "arturo calle", "easy", "alkosto", "ktronix", "ropa", "moda") -> "Compras"

            else -> null
        }
    }

    private fun containsAny(text: String, vararg keywords: String): Boolean {
        return keywords.any { text.contains(it) }
    }

    private fun normalize(text: String): String {
        return text.lowercase()
            .replace(Regex("[áàä]"), "a")
            .replace(Regex("[éèë]"), "e")
            .replace(Regex("[íìï]"), "i")
            .replace(Regex("[óòö]"), "o")
            .replace(Regex("[úùü]"), "u")
            .replace(Regex("[^a-z0-9\\s]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}
