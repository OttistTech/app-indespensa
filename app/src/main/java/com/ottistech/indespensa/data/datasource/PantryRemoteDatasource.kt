package com.ottistech.indespensa.data.datasource

import android.util.Log
import com.ottistech.indespensa.webclient.RetrofitInitializer
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemAddDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemCloseValidityDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemCreateDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemDetailsDTO
import com.ottistech.indespensa.webclient.dto.pantry.PantryItemPartialDTO
import com.ottistech.indespensa.webclient.dto.product.ProductItemUpdateAmountDTO
import com.ottistech.indespensa.webclient.helpers.ResultWrapper
import com.ottistech.indespensa.webclient.service.core.PantryService
import java.util.Date

class PantryRemoteDatasource {

    private val TAG = "PANTRY REMOTE DATASOURCE"
    private val service : PantryService =
        RetrofitInitializer().getCoreService(PantryService::class.java)

    suspend fun create(
        userId: Long,
        pantryItem: PantryItemCreateDTO,
        token: String
    ) : ResultWrapper<Boolean> {
        return try {
            Log.d(TAG, "[create] Trying to create with: $pantryItem")
            val response = service.createItem(userId, pantryItem, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[create] Created successfully")
                ResultWrapper.Success(true)
            } else {
                Log.e(TAG, "[create] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[create] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun list(
        userId: Long,
        token: String
    ) : ResultWrapper<List<PantryItemPartialDTO>> {
        return try {
            Log.d(TAG, "[list] Fetching pantry items")
            val response = service.listItems(userId, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[list] Found pantry items successfully")
                ResultWrapper.Success(
                    response.body() as List<PantryItemPartialDTO>
                )
            } else {
                Log.e(TAG, "[list] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[list] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun updateAmount(
        pantryItems : List<ProductItemUpdateAmountDTO>,
        token: String
    ) : ResultWrapper<Boolean> {
        return try {
            Log.d(TAG, "[updateAmount] Trying to update amount of ${pantryItems.size} items")
            val response = service.updateItemsAmount(pantryItems, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[updateAmount] Updated successfully")
                ResultWrapper.Success(true)
            } else {
                Log.e(TAG, "[updateAmount] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[updateAmount] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun getDetails(
        pantryItemId: Long,
        token: String
    ) : ResultWrapper<PantryItemDetailsDTO> {
        return try {
            Log.d(TAG, "[getDetails] Fetching pantry item details for $pantryItemId")
            val response = service.getItemDetails(pantryItemId, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[getDetails] Found successfully")
                ResultWrapper.Success(
                    response.body() as PantryItemDetailsDTO
                )
            } else {
                Log.e(TAG, "[getDetails] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[getDetails] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun addShopItem(
        userId: Long,
        shopItemId: Long,
        validityDate: Date,
        token: String
    ) : ResultWrapper<Boolean> {
        return try {
            val pantryItemAdd = PantryItemAddDTO(shopItemId, validityDate)
            Log.d(TAG, "[addShopItem] Trying to add item with: $pantryItemAdd")
            val response = service.addPantryItem(userId, pantryItemAdd, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[addShopItem] Added successfully")
                ResultWrapper.Success(true)
            } else {
                Log.e(TAG, "[addShopItem] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[addShopItem] Failed", e)
            return ResultWrapper.ConnectionError
        }
    }

    suspend fun addAllShopItems(
        userId: Long,
        token: String
    ): ResultWrapper<Boolean> {
        return try {
            Log.d(TAG, "[addAllShopItems] Trying to add all shop items")
            val response = service.addAllShopItemsToPantry(userId, token)
            if (response.isSuccessful) {
                Log.d(TAG, "[addAllShopItems] Added all successfully")
                ResultWrapper.Success(true)
            } else {
                Log.e(TAG, "[addAllShopItems] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[addAllShopItems] Failed", e)
            ResultWrapper.ConnectionError
        }
    }

    suspend fun listCloseValidityItems(
        userId: Long,
        token: String
    ) : ResultWrapper<List<PantryItemCloseValidityDTO>> {
        return try {
            Log.d(TAG, "[listCloseValidityItems] Fetching close validity items")
            val response = service.listCloseValidityItems(userId, token)
            if(response.isSuccessful) {
                Log.d(TAG, "[listCloseValidityItems] Found successfully")
                ResultWrapper.Success(
                    response.body() as List<PantryItemCloseValidityDTO>
                )
            } else {
                Log.e(TAG, "[listCloseValidityItems] Error ${response.code()} occurred: ${response.message()}")
                ResultWrapper.Error(response.code(), response.message())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[listCloseValidityItems] Failed", e)
            return ResultWrapper.ConnectionError
        }
    }
}