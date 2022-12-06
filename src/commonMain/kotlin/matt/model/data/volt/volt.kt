package matt.model.data.volt

import kotlinx.serialization.Serializable
import matt.model.data.mathable.DoubleWrapper

/*temporarily using normal class because of bug*/
/*see https://youtrack.jetbrains.com/issue/KT-54513/javalangNoSuchMethodError-with-value-class-implementing-an-interface*/
@Serializable /*@JvmInline value */ data class MicroVolt(override val asDouble: Double): DoubleWrapper<MicroVolt> {

  companion object {
	val ZERO = MicroVolt(0.0)
  }

  override fun fromDouble(d: Double): MicroVolt {
	return MicroVolt(d)
  }


}

