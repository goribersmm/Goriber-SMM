package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.OrderEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.SmmPlatform
import com.example.data.model.SmmService
import com.example.data.repository.SmmRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
    data class OpenUrl(val url: String) : UiEvent()
}

class SmmViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SmmRepository(application)

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfileFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val orders: StateFlow<List<OrderEntity>> = repository.ordersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<TransactionEntity>> = repository.transactionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _services = MutableStateFlow<List<SmmService>>(emptyList())
    val services: StateFlow<List<SmmService>> = _services.asStateFlow()

    private val _isLoadingServices = MutableStateFlow(false)
    val isLoadingServices: StateFlow<Boolean> = _isLoadingServices.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedPlatform = MutableStateFlow<SmmPlatform?>(null)
    val selectedPlatform: StateFlow<SmmPlatform?> = _selectedPlatform.asStateFlow()

    // Filtered services
    val filteredServices: StateFlow<List<SmmService>> = combine(
        _services,
        _searchQuery,
        _selectedPlatform
    ) { serviceList, query, platform ->
        serviceList.filter { service ->
            val matchQuery = query.isBlank() ||
                    service.name.contains(query, ignoreCase = true) ||
                    service.category.contains(query, ignoreCase = true) ||
                    service.serviceId.toString().contains(query)
            val matchPlatform = platform == null || service.platform == platform
            matchQuery && matchPlatform
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // New Order Form state
    private val _selectedService = MutableStateFlow<SmmService?>(null)
    val selectedService: StateFlow<SmmService?> = _selectedService.asStateFlow()

    private val _targetLink = MutableStateFlow("")
    val targetLink: StateFlow<String> = _targetLink.asStateFlow()

    private val _quantity = MutableStateFlow("")
    val quantity: StateFlow<String> = _quantity.asStateFlow()

    private val _isPlacingOrder = MutableStateFlow(false)
    val isPlacingOrder: StateFlow<Boolean> = _isPlacingOrder.asStateFlow()

    // Admin & Config state
    private val _usdToBdt = MutableStateFlow(125.0)
    val usdToBdt: StateFlow<Double> = _usdToBdt.asStateFlow()

    private val _profitMargin = MutableStateFlow(25.0)
    val profitMargin: StateFlow<Double> = _profitMargin.asStateFlow()

    private val _motherPanelBalance = MutableStateFlow("Loading...")
    val motherPanelBalance: StateFlow<String> = _motherPanelBalance.asStateFlow()

    private val _noticeText = MutableStateFlow("স্বাগতম Goriber SMM এ! bKash, Nagad এবং Rocket দিয়ে অটোমেটিক ব্যালেন্স এড করুন। ইনস্ট্যান্ট ফাস্ট ডেলিভারি!")
    val noticeText: StateFlow<String> = _noticeText.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.initUserIfNeeded()
            loadServices()
            checkMotherPanelBalance()
        }
    }

    fun loadServices() {
        viewModelScope.launch {
            _isLoadingServices.value = true
            val list = repository.getServices()
            _services.value = list
            if (_selectedService.value == null && list.isNotEmpty()) {
                _selectedService.value = list.first()
            }
            _isLoadingServices.value = false
        }
    }

    fun checkMotherPanelBalance() {
        viewModelScope.launch {
            _motherPanelBalance.value = repository.checkMotherPanelBalance()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedPlatform(platform: SmmPlatform?) {
        _selectedPlatform.value = platform
    }

    fun selectService(service: SmmService) {
        _selectedService.value = service
    }

    fun setTargetLink(link: String) {
        _targetLink.value = link
    }

    fun setQuantity(qty: String) {
        _quantity.value = qty
    }

    fun calculateCurrentCharge(): Double {
        val s = _selectedService.value ?: return 0.0
        val q = _quantity.value.toIntOrNull() ?: 0
        return (s.rateBdt * q) / 1000.0
    }

    fun placeOrder() {
        val service = _selectedService.value
        val link = _targetLink.value.trim()
        val qty = _quantity.value.toIntOrNull() ?: 0

        if (service == null) {
            emitToast("অনুগ্রহ করে একটি সার্ভিস নির্বাচন করুন!")
            return
        }
        if (link.isBlank()) {
            emitToast("সোশ্যাল মিডিয়া প্রোফাইল বা পোস্টের সঠিক লিংক দিন!")
            return
        }
        if (qty < service.min) {
            emitToast("সর্বনিম্ন পরিমাণ: ${service.min}")
            return
        }
        if (qty > service.max) {
            emitToast("সর্বোচ্চ পরিমাণ: ${service.max}")
            return
        }

        val totalCharge = calculateCurrentCharge()

        viewModelScope.launch {
            _isPlacingOrder.value = true
            val result = repository.placeOrder(service, link, qty, totalCharge)
            _isPlacingOrder.value = false

            result.onSuccess {
                emitToast("অর্ডার সফলভাবে সাবমিট হয়েছে! Order ID: #$it")
                _targetLink.value = ""
                _quantity.value = ""
            }.onFailure { err ->
                emitToast(err.message ?: "অর্ডার প্লেস ব্যর্থ হয়েছে!")
            }
        }
    }

    fun processAddFunds(amount: Double, method: String, trxId: String) {
        if (amount < 10) {
            emitToast("সর্বনিম্ন ডিপোজিট ৳১০ টাকা")
            return
        }
        viewModelScope.launch {
            val validTrxId = if (trxId.isBlank()) "TRX${System.currentTimeMillis().toString().takeLast(8)}" else trxId
            repository.recordPaymentSuccess(validTrxId, method, amount)
            emitToast("৳${String.format("%.2f", amount)} সফলভাবে আপনার একাউন্টে যোগ হয়েছে!")
        }
    }

    fun launchUddoktaPay(amount: Double) {
        if (amount < 10) {
            emitToast("সর্বনিম্ন ডিপোজিট ৳১০ টাকা")
            return
        }
        viewModelScope.launch {
            val user = userProfile.value
            val name = user?.userName ?: "Goriber SMM User"
            val email = user?.email ?: "goribersmm@gmail.com"
            val result = repository.addFundsViaPaymently(amount, name, email)
            result.onSuccess { url ->
                _eventFlow.emit(UiEvent.OpenUrl(url))
                emitToast("UddoktaPay পেমেন্ট গেটওয়ে ওপেন হচ্ছে...")
            }.onFailure { err ->
                emitToast(err.message ?: "পেমেন্ট গেটওয়ে ওপেন করা যায়নি")
            }
        }
    }

    fun adminSetUsdToBdt(rate: Double) {
        _usdToBdt.value = rate
        repository.usdToBdtRate = rate
        loadServices()
    }

    fun adminSetMargin(margin: Double) {
        _profitMargin.value = margin
        repository.profitMarginPercent = margin
        loadServices()
    }

    fun adminUpdateUserBalance(newBalance: Double) {
        viewModelScope.launch {
            repository.adminUpdateBalance(newBalance)
            emitToast("ইউজার ব্যালেন্স আপডেট হয়েছে: ৳$newBalance")
        }
    }

    fun setNotice(text: String) {
        _noticeText.value = text
        emitToast("নোটিশ আপডেট করা হয়েছে!")
    }

    fun syncOrder(order: OrderEntity) {
        viewModelScope.launch {
            repository.syncOrderStatus(order)
            emitToast("অর্ডারের লাইভ স্ট্যাটাস আপডেট হয়েছে!")
        }
    }

    private fun emitToast(msg: String) {
        viewModelScope.launch {
            _eventFlow.emit(UiEvent.ShowToast(msg))
        }
    }
}
