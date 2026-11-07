package com.houseofrafa.cashizard.presentation.feature.settings.editprofile

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.houseofrafa.cashizard.presentation.arch.koinViewModel

/**
 * Hosts the "Edit Name" sheet. No internal stack, so the only navigation it owns
 * is turning a back press into a close request.
 */
class EditProfileSheetComponent(
    componentContext: ComponentContext,
    private val onDismiss: () -> Unit,
) : ComponentContext by componentContext {

    val viewModel: EditProfileViewModel = koinViewModel()

    init {
        backHandler.register(BackCallback { viewModel.onRequestClose() })
    }

    fun onDismissed() = onDismiss()
}
