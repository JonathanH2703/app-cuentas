package com.personalfinance.cuentas.domain.usecases

import com.personalfinance.cuentas.data.local.dao.AllocationDao
import com.personalfinance.cuentas.data.local.entities.AllocationBlockEntity
import com.personalfinance.cuentas.data.local.entities.AllocationModelEntity
import com.personalfinance.cuentas.data.local.entities.BlockType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InitializeDefaultAllocationModelUseCase @Inject constructor(
    private val allocationDao: AllocationDao
) {
    suspend operator fun invoke() {
        if (allocationDao.getModelCount() == 0) {
            // Modelo 1: 50/30/20 Clásico (Activo por defecto)
            val model503020Id = allocationDao.insertModel(
                AllocationModelEntity(
                    name = "50/30/20 Clásico",
                    isActive = true
                )
            )

            val blocks503020 = listOf(
                AllocationBlockEntity(
                    modelId = model503020Id,
                    name = "Necesidades Básicas",
                    percentage = 50,
                    type = BlockType.NEEDS,
                    colorHex = "#1976D2", // Azul
                    orderIndex = 1,
                    absorbRemainder = true // Absorbe residuos de redondeo
                ),
                AllocationBlockEntity(
                    modelId = model503020Id,
                    name = "Deseos y Ocio",
                    percentage = 30,
                    type = BlockType.WANTS,
                    colorHex = "#FF9800", // Naranja
                    orderIndex = 2,
                    absorbRemainder = false
                ),
                AllocationBlockEntity(
                    modelId = model503020Id,
                    name = "Ahorro e Inversión",
                    percentage = 20,
                    type = BlockType.SAVINGS,
                    colorHex = "#2E7D32", // Verde
                    orderIndex = 3,
                    absorbRemainder = false
                )
            )
            allocationDao.insertBlocks(blocks503020)

            // Modelo 2: 70/20/10 (Enfoque en Pago de Deudas Agresivo)
            val model702010Id = allocationDao.insertModel(
                AllocationModelEntity(
                    name = "70/20/10 Agresivo para Deudas",
                    isActive = false
                )
            )
            val blocks702010 = listOf(
                AllocationBlockEntity(
                    modelId = model702010Id,
                    name = "Gastos de Vida y Deudas",
                    percentage = 70,
                    type = BlockType.NEEDS,
                    colorHex = "#C62828", // Rojo
                    orderIndex = 1,
                    absorbRemainder = true
                ),
                AllocationBlockEntity(
                    modelId = model702010Id,
                    name = "Fondo de Emergencia",
                    percentage = 20,
                    type = BlockType.SAVINGS,
                    colorHex = "#2E7D32",
                    orderIndex = 2,
                    absorbRemainder = false
                ),
                AllocationBlockEntity(
                    modelId = model702010Id,
                    name = "Gastos Personales Mínimos",
                    percentage = 10,
                    type = BlockType.WANTS,
                    colorHex = "#FFB300",
                    orderIndex = 3,
                    absorbRemainder = false
                )
            )
            allocationDao.insertBlocks(blocks702010)

            // Modelo 3: 40/30/20/10 (Estilo 4 Bloques Cuadrantes)
            val model4BlockId = allocationDao.insertModel(
                AllocationModelEntity(
                    name = "40/30/20/10 Equilibrado",
                    isActive = false
                )
            )
            val blocks4 = listOf(
                AllocationBlockEntity(
                    modelId = model4BlockId,
                    name = "Necesidades",
                    percentage = 40,
                    type = BlockType.NEEDS,
                    colorHex = "#1565C0",
                    orderIndex = 1,
                    absorbRemainder = true
                ),
                AllocationBlockEntity(
                    modelId = model4BlockId,
                    name = "Deseos",
                    percentage = 30,
                    type = BlockType.WANTS,
                    colorHex = "#FB8C00",
                    orderIndex = 2,
                    absorbRemainder = false
                ),
                AllocationBlockEntity(
                    modelId = model4BlockId,
                    name = "Ahorro",
                    percentage = 20,
                    type = BlockType.SAVINGS,
                    colorHex = "#388E3C",
                    orderIndex = 3,
                    absorbRemainder = false
                ),
                AllocationBlockEntity(
                    modelId = model4BlockId,
                    name = "Educación y Crecimiento",
                    percentage = 10,
                    type = BlockType.CUSTOM,
                    colorHex = "#8E24AA",
                    orderIndex = 4,
                    absorbRemainder = false
                )
            )
            allocationDao.insertBlocks(blocks4)
        }
    }
}
