package com.perelandrax.coinspace.presentation.ribs.coins

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.perelandrax.coinspace.R
import com.perelandrax.coinspace.data.CoinRepository
import com.perelandrax.coinspace.interactors.GetCoins
import com.perelandrax.coinspace.presentation.ribs.coindetail.CoinDetailBuilder
import com.perelandrax.coinspace.presentation.ribs.coindetail.CoinDetailScreen
import com.perelandrax.coinspace.presentation.ribs.splash.masterstream.CoinMasterStreamSource
import com.perelandrax.coinspace.presentation.screenstack.ScreenStack
import com.uber.rib.core.InteractorBaseComponent
import com.uber.rib.core.ViewBuilder
import dagger.Binds
import dagger.BindsInstance
import dagger.Provides
import javax.inject.Qualifier
import javax.inject.Scope
import kotlin.annotation.AnnotationRetention.BINARY

/**
 * Builder for the {@link CoinsScope}.
 *
 * TODO describe this scope's responsibility as a whole.
 */
class CoinsBuilder(dependency: ParentComponent) :
  ViewBuilder<CoinsView, CoinsRouter, CoinsBuilder.ParentComponent>(dependency) {

  /**
   * Builds a new [CoinsRouter].
   *
   * @param parentViewGroup parent view group that this router's view will be added to.
   * @return a new [CoinsRouter].
   */
  fun build(parentViewGroup: ViewGroup): CoinsRouter {
    val view = createView(parentViewGroup)
    val interactor = CoinsInteractor()
    val component = DaggerCoinsBuilder_Component.builder()
      .parentComponent(dependency)
      .view(view)
      .interactor(interactor)
      .build()
    return component.coinsRouter()
  }

  override fun inflateView(inflater: LayoutInflater, parentViewGroup: ViewGroup): CoinsView? {
    return inflater.inflate(R.layout.layout_coins_rib, parentViewGroup, false) as CoinsView
  }

  interface ParentComponent {
    fun context(): Context
    fun screenStack(): ScreenStack
    fun coinRepository(): CoinRepository
    fun coinMasterStreamSource(): CoinMasterStreamSource
  }

  @dagger.Module
  abstract class Module {

    @CoinsScope
    @Binds
    internal abstract fun presenter(view: CoinsView): CoinsInteractor.CoinsPresenter

    @dagger.Module
    companion object {

      @CoinsScope
      @Provides
      @JvmStatic
      internal fun router(component: Component, view: CoinsView, interactor: CoinsInteractor, screenStack: ScreenStack): CoinsRouter {
        return CoinsRouter(view, interactor, component, screenStack, CoinDetailScreen(CoinDetailBuilder(component)))
      }

      @CoinsScope
      @Provides
      @JvmStatic
      fun getCoins(coinRepository: CoinRepository): GetCoins {
        return GetCoins(coinRepository)
      }
    }
  }

  @CoinsScope
  @dagger.Component(modules = arrayOf(Module::class), dependencies = arrayOf(ParentComponent::class))
  interface Component : InteractorBaseComponent<CoinsInteractor>, BuilderComponent,
    CoinDetailBuilder.ParentComponent {

    @dagger.Component.Builder
    interface Builder {

      @BindsInstance
      fun interactor(interactor: CoinsInteractor): Builder

      @BindsInstance
      fun view(view: CoinsView): Builder

      fun parentComponent(component: ParentComponent): Builder

      fun build(): Component
    }
  }

  interface BuilderComponent {
    fun coinsRouter(): CoinsRouter
  }

  @Scope
  @Retention(BINARY)
  internal annotation class CoinsScope

  @Qualifier
  @Retention(BINARY)
  internal annotation class CoinsInternal
}
