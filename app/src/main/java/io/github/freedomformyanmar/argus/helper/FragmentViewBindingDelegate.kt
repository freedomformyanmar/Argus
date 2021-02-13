package io.github.freedomformyanmar.argus.helper

import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Fragment delegate that make it easy to use ViewBinding
 * See these blog posts for details
 * https://medium.com/@Zhuinden/simple-one-liner-viewbinding-in-fragments-and-activities-with-kotlin-961430c6c07c
 * https://proandroiddev.com/fast-migration-from-kotlin-synthetics-to-view-binding-tips-and-tricks-66346d34ec0a
 */
class FragmentViewBindingDelegate<T : ViewBinding> (
  val fragment: Fragment,
  val viewBindingFactory: (View) -> T
) : ReadOnlyProperty<Fragment, T> {

  private var binding: T? = null

  init {
    fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
      val viewLifecycleOwnerLiveDataObserver =
        Observer<LifecycleOwner?> {
          val viewLifecycleOwner = it ?: return@Observer

          viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
              binding = null
            }
          })
        }

      override fun onCreate(owner: LifecycleOwner) {
        fragment.viewLifecycleOwnerLiveData.observeForever(viewLifecycleOwnerLiveDataObserver)
      }

      override fun onDestroy(owner: LifecycleOwner) {
        fragment.viewLifecycleOwnerLiveData.removeObserver(viewLifecycleOwnerLiveDataObserver)
      }
    })
  }

  override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
    val binding = binding
    if (binding != null) {
      return binding
    }

    val lifecycle = fragment.viewLifecycleOwner.lifecycle
    if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
      throw IllegalStateException("Should not attempt to get bindings when Fragment views are destroyed.")
    }

    return viewBindingFactory(thisRef.requireView()).also { this.binding = it }
  }
}

/**
 * Usage as follows:
 * private val binding by viewBinding(ViewBinding::bind)
 */
fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T) =
  FragmentViewBindingDelegate(this, viewBindingFactory)


/**
 * Usage as follows:
 * private val binding by viewBinding(ViewBinding::inflate)
 */

inline fun <T : ViewBinding> AppCompatActivity.viewBinding(
  crossinline bindingInflater: (LayoutInflater) -> T) =
  lazy(LazyThreadSafetyMode.NONE) {
    bindingInflater.invoke(layoutInflater)
  }
