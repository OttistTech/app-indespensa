package com.ottistech.indespensa.shared

import com.ottistech.indespensa.ui.model.Filter
import com.ottistech.indespensa.ui.model.FilterType

object AppConstants {
    val AMOUNT_UNITIES: List<String> = listOf(
        "kg", "g", "l", "ml", "u"
    )

    val BRAZIL_STATES : List<String> = listOf(
        "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO",
        "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI",
        "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO"
    )

    val START_CAROUSEL_MESSAGES : List<String> = listOf(
        "Gerencie sua despensa pessoal",
        "Organização para pequenas empresas",
        "Indicação de receitas para todos os perfis",
        "Otimize seu tempo e esforço na cozinha",
        "Economize dinheiro sem desperdício"
    )

    val TERMS_AND_CONDITIONS_URL : String = "https://terms-and-conditions-webpage.onrender.com"
    val EXPOTECH_DASHBOARD_URL : String = "https://app.powerbi.com/reportEmbed?reportId=340cfa57-1256-4ba4-af5c-a6da47efcb82&autoAuth=true"

    val PREMIUM_CAROUSEL_MESSAGES: List<String> = listOf(
        "Filtros de receitas personalizados",
        "Histórico de compras",
        "Despensa compartilhada"
    )

    val PIX_KEY = "00020126360014br.gov.bcb.pix0114+5511970566577520400005303986540519.995802BR5925DAVI PIASSI BARROS DOS SA6009Sao Paulo62070503***630414C7"

    val RECIPE_LEVELS: List<String> = RecipeLevel.entries.map { it.displayName }

    val DEFAULT_RECIPE_FILTERS: List<Filter<Any>> = listOf(
        Filter(type=FilterType.LEVEL, value=RecipeLevel.EASY, text="Fácil"),
        Filter(type=FilterType.AVAILABILITY, value=IngredientState.IN_PANTRY, text="Ingredientes na despensa"),
        Filter(type=FilterType.TIME, value=Pair(0, 40), text="0 - 40min")
    )

    val LEVEL_RECIPE_FILTERS: List<Filter<RecipeLevel>> = listOf(
        Filter(type=FilterType.LEVEL, value=RecipeLevel.EASY, text="Fácil"),
        Filter(type=FilterType.LEVEL, value=RecipeLevel.INTER, text="Inter"),
        Filter(type=FilterType.LEVEL, value=RecipeLevel.ADVANCED, text="Avançado")
    )

    val AVAILABILITY_RECIPE_FILTERS: List<Filter<IngredientState>> = listOf(
        Filter(type=FilterType.AVAILABILITY, value=IngredientState.IN_PANTRY, text="Na despensa"),
        Filter(type=FilterType.AVAILABILITY, value=IngredientState.OUT_OF_PANTRY, text="Comprar")
    )

    val TIME_RECIPE_FILTERS: List<Filter<Pair<Int, Int>>> = listOf(
        Filter(type=FilterType.TIME, value=Pair(0, 40), text="0 - 40min"),
        Filter(type=FilterType.TIME, value=Pair(40, 120), text="40 - 1h20min"),
        Filter(type=FilterType.TIME, value=Pair(120, 1440), text="1h20min +"),
    )
}