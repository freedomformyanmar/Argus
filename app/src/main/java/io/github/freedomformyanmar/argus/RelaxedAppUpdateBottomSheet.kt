package io.github.freedomformyanmar.argus

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.github.freedomformyanmar.argus.databinding.BottomSheetRelaxedAppUpdateBinding
import io.github.freedomformyanmar.argus.helper.viewBinding

class RelaxedAppUpdateBottomSheet : BottomSheetDialogFragment() {

  private val binding by viewBinding(BottomSheetRelaxedAppUpdateBinding::bind)

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    return inflater.inflate(R.layout.bottom_sheet_relaxed_app_update, container, false)
  }

  var onOkayClick: (() -> Unit)? = null
  var onCancelClick: (() -> Unit)? = null

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding.buttonOkay.setOnClickListener {
      onOkayClick?.invoke()
    }

    binding.buttonCancel.setOnClickListener {
      onCancelClick?.invoke()
    }


  }

  override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    onOkayClick = null
    onCancelClick = null
  }
}