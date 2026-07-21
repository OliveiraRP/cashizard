package com.houseofrafa.cashizard.domain.session

import com.houseofrafa.cashizard.domain.model.Space
import com.houseofrafa.cashizard.domain.repository.SpaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * The signed-in user's active space, plus a change signal for everything derived
 * from it.
 *
 * Every screen is scoped to one space and every balance is computed server-side,
 * so a write anywhere invalidates reads everywhere. Holding both facts in one
 * app-scoped object means screens observe them directly instead of having them
 * threaded down through the component tree.
 */
class SpaceSession(
    private val spaceRepository: SpaceRepository,
) {
    private val _activeSpace = MutableStateFlow<Space?>(null)

    /** Until the space switcher lands this is simply the user's first space. */
    val activeSpace: StateFlow<Space?> = _activeSpace.asStateFlow()

    private val _activeSpaceId = MutableStateFlow<String?>(null)
    val activeSpaceId: StateFlow<String?> = _activeSpaceId.asStateFlow()

    private val _dataVersion = MutableStateFlow(0)

    /** Bumped after a write so screens reading computed balances reload. */
    val dataVersion: StateFlow<Int> = _dataVersion.asStateFlow()

    private val loadLock = Mutex()

    /** Resolves the active space once. Safe to call from every screen that needs one. */
    suspend fun ensureLoaded() {
        loadLock.withLock {
            if (_activeSpace.value != null) return
            val space = runCatching { spaceRepository.getSpaces() }
                .getOrNull()
                ?.firstOrNull()
            _activeSpace.value = space
            _activeSpaceId.value = space?.id
        }
    }

    fun notifyDataChanged() = _dataVersion.update { version -> version + 1 }

    /** Sheets are only opened once a space exists, so this cannot fail in practice. */
    fun requireSpaceId(): String = requireNotNull(_activeSpaceId.value) {
        "A sheet cannot open before a space is resolved."
    }
}
