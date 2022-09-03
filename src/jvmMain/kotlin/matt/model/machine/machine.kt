package matt.model.machine

import matt.lang.shutdown.duringShutdown

abstract class Machine {

  init {
	duringShutdown {
	  shutdown()
	}
  }

  private var on: Boolean = false

  @Synchronized fun start() {
	if (!on) boot()
	on = true
  }

  @Synchronized fun shutdown() {
	if (on) unboot()
	on = false
  }

  @Synchronized fun restart() {
	shutdown()
	start()
  }

  protected abstract fun boot()
  protected abstract fun unboot()
}