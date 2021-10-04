package io.trojan.common.utils

import pureconfig.ConfigReader
import pureconfig.generic.DerivedConfigReader
import pureconfig.generic.semiauto.deriveReader
import shapeless.Lazy

trait WithConfigCodec[T] {
  implicit def codec(implicit reader: Lazy[DerivedConfigReader[T]]): ConfigReader[T] = deriveReader[T]
}
