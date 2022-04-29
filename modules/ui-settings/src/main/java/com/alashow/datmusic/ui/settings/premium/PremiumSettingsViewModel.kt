/*
 * Copyright (C) 2021, Alashov Berkeli
 * All rights reserved.
 */
package com.alashow.datmusic.ui.settings.premium

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.alashow.base.billing.SubscriptionError
import com.alashow.base.billing.Subscriptions
import com.alashow.base.billing.SubscriptionsNotEnabledError
import com.alashow.base.util.extensions.stateInDefault

@HiltViewModel
class PremiumSettingsViewModel @Inject constructor(
    handle: SavedStateHandle,
) : ViewModel() {

    private val premiumStatusState = MutableStateFlow<PremiumStatus>(PremiumStatus.Unknown)
    val premiumStatus = premiumStatusState.stateInDefault(viewModelScope, PremiumStatus.Unknown)

    init {
        refreshPremiumStatus()
    }

    fun refreshPremiumStatus() {
        viewModelScope.launch {
            try {
                premiumStatusState.value = PremiumStatus.Subscribed(Subscriptions.checkPremiumPermission())
            } catch (error: SubscriptionError) {
                premiumStatusState.value = PremiumStatus.NotSubscribed(error)
            } catch (e: SubscriptionsNotEnabledError) {
                premiumStatusState.value = PremiumStatus.NotEnabled
            }
        }
    }

    fun fakeRefresh(delayMillis: Long = 3500L) {
        viewModelScope.launch {
            premiumStatusState.value = PremiumStatus.Unknown
            delay(delayMillis)
            refreshPremiumStatus()
        }
    }
}
