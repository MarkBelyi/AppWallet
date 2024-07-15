package com.example.walletapp.DataBase.ViewModel

/*
class SignerViewModel(
    private val dao: SignerDao
): ViewModel() {

    private val _sortType = MutableStateFlow(SortType.NAME)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _signers = _sortType
        .flatMapLatest { sortType ->
            when(sortType){
                SortType.NAME -> dao.getSignersByName()
                SortType.ADDRESS -> dao.getSignersByAddress()
                SortType.TELEPHONE -> dao.getSignersByTelephone()
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    private val _state = MutableStateFlow(SignerState())

    val state = combine(_state, _sortType, _signers) {state, sortType, signers ->
        state.copy(
            signers = signers,
            sortType = sortType
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SignerState())

    fun onEvent(event: SignerEvent){
        when(event){
            is SignerEvent.DeleteSigner -> {
                viewModelScope.launch {
                    dao.deleteSigner(event.signer)
                }
            }
            SignerEvent.HideDialog -> {
                _state.update { it.copy(
                    isAddingSigner = false
                ) }
            }
            SignerEvent.SaveSigner -> {
                val address = state.value.address
                val name = state.value.name
                val telephone = state.value.telephone
                val email = state.value.email
                val type = 1

                if(address.isBlank() || name.isBlank() || telephone.isBlank() || email.isBlank()){
                    return
                }

                val signer = Signer(
                    address = address,
                    name = name,
                    email = email,
                    telephone = telephone,
                    type = type
                )

                viewModelScope.launch {
                    dao.upsertSigner(signer)
                }

                _state.update { it.copy(
                    isAddingSigner = false,
                    address = "",
                    name = "",
                    email = "",
                    telephone = "",
                    type = 1
                ) }
            }
            is SignerEvent.SetEmail -> {
                _state.update{it.copy(
                    email = event.email
                )}
            }
            is SignerEvent.SetName -> {
                _state.update{it.copy(
                    name = event.name
                )}
            }
            is SignerEvent.SetTelephone -> {
                _state.update{it.copy(
                    telephone = event.telephone
                )}
            }
            is SignerEvent.SetType -> {
                _state.update{it.copy(
                    type = event.type
                )}
            }
            SignerEvent.ShowDialog -> {
                _state.update { it.copy(
                    isAddingSigner = true
                ) }
            }
            is SignerEvent.SortSigners -> {
                _sortType.value = event.sortType
            }

            is SignerEvent.SetAddress -> {
                _state.update { it.copy(
                    address = event.address
                ) }
            }
        }
    }
}*/
