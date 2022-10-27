package matt.model.valjson

import matt.model.valjson.ValJson.Port
import kotlin.reflect.full.memberProperties

operator fun Port.get(name: String) = Port::class.memberProperties.firstOrNull { it.name == name }?.get(Port) as? Int