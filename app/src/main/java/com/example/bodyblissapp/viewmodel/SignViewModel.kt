import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bodyblissapp.data.model.SignRequest
import com.example.bodyblissapp.data.network.RetrofitInstance
import com.example.bodyblissapp.util.SessionManager
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class SignViewModel : ViewModel() {

    var lang by mutableStateOf("")
    var cardNumber by mutableStateOf("")
    var expiryDate by mutableStateOf("")
    var cvcCvv by mutableStateOf("")
    var nameOnCard by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)

    fun signUpVip(userId: Int, context: Context) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            successMessage = null

            val cleanedCardNumber = cardNumber.replace(" ", "")
            val formattedExpiry = expiryDate.take(2) + "/" + expiryDate.takeLast(2)

            try {
                val response = RetrofitInstance.signService.signUpVip(
                    userId = userId,
                    lang = lang.ifBlank { "en" },
                    cardNumber = cleanedCardNumber,
                    expirateDate = formattedExpiry,
                    cvcCvv = cvcCvv,
                    nameOnCard = nameOnCard
                )

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    if (body.success == true) {
                        val sessionManager = SessionManager(context)
                        sessionManager.saveUserSession(
                            userId = userId,
                            name = body.name ?: "",
                            email = body.email ?: "",
                            role = body.role ?: "vip"
                        )
                        val savedRole = sessionManager.getUserRole()
                        successMessage = (body.message ?: "Signed up successfully!") + " (role: $savedRole)"
                    } else {
                        errorMessage = body.error ?: "Unexpected error occurred"
                    }
                } else {
                    errorMessage = "Server error: ${response.code()}"
                }
            } catch (e: IOException) {
                errorMessage = "Network error: ${e.localizedMessage}"
            } catch (e: HttpException) {
                errorMessage = "HTTP error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

}
