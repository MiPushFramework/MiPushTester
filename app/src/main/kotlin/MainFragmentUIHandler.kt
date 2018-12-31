package moe.yuuta.mipushtester

import android.view.View

interface MainFragmentUIHandler {
    fun handleToggleRegister (v: View)
    fun handleCreatePush (v: View)
    fun handleReset (v: View)
    fun handleSubscribeTopic (v: View)
    fun handleSetAcceptTimeStart (v: View)
    fun handleSetAcceptTimeEnd (v: View)
    fun handleSetAlias (v: View)
    fun handleSetAccount (v: View)
}
