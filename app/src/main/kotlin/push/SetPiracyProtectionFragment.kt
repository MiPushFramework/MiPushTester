package moe.yuuta.mipushtester.push

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.android.settings.widget.SwitchBar
import moe.yuuta.mipushtester.R
import moe.yuuta.mipushtester.push.internal.CoreProvider
import moe.yuuta.mipushtester.push.internal.PushSdkWrapper
import moe.yuuta.mipushtester.utils.Utils

class SetPiracyProtectionFragment : Fragment(), SwitchBar.OnSwitchChangeListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_set_piracy_protection, container, false)
        val switchBar: SwitchBar = view.findViewById(R.id.switch_bar)
        switchBar.isChecked = !PushSdkWrapper.isDisabled(requireContext())
        switchBar.addOnSwitchChangeListener(this)
        switchBar.show()
        val footerTitle: TextView = view.findViewById(android.R.id.title)
        footerTitle.text = getString(R.string.privacy_protection_summary)
        return view
    }

    override fun onSwitchChanged(switchView: Switch?, isChecked: Boolean) {
        requireContext().packageManager.setComponentEnabledSetting(ComponentName(requireContext(), CoreProvider::class.java),
                if (isChecked) PackageManager.COMPONENT_ENABLED_STATE_ENABLED else PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                0 -> {
                    Utils.restart(requireContext())
                    true
                }
                else -> {
                    super.onOptionsItemSelected(item)
                }
            }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.add(0, 0, 0, R.string.restart)
    }
}