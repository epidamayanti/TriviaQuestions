package id.mncplay.triviaquestions.commons


import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable

open class RxBaseFragment : Fragment() {

    protected var subscriptions = CompositeDisposable()

    override fun onResume() {
        super.onResume()
        subscriptions = CompositeDisposable()
    }

    override fun onPause() {
        super.onPause()
        subscriptions.clear()
    }

}