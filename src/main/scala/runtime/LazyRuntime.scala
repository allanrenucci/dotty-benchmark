package runtime

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater

import scala.annotation.{tailrec, switch}

import LazyRuntime._

class LazyBaseline(rhs: => Int) {
  private[this] var _value: Int = _
  @volatile var flag: Boolean = _

  final def value: Int =
    if (flag) _value else computeValue()

  final def computeValue(): Int = {
    this.synchronized {
      if (!flag) {
        _value = rhs
        flag = true
      }
    }
    _value
  }
}

class LazyV1(rhs: => Int) {
  private[this] var _value: Int = _
  private[this] val flag = new AtomicInteger(InitialState)

  final def value: Int =
    if (flag.get == ComputedState) _value else computeValue()

  final def computeValue(): Int = {
    while (true) {
      val state = flag.get
      if (state == InitialState) {
        try {
          val result = rhs
          _value = result
          setFlagAndNotify(flag, ComputedState)
          return result
        } catch {
          case ex: Throwable =>
            setFlagAndNotify(flag, InitialState)
            throw ex
        }
      }
      else if (state == ComputedState)
        return _value
      else /* if (state == ComputingState || state == NotifyState) */
        waitForNotification(flag, state)
    }

    throw null
  }
}

class LazyV1Bis(rhs: => Int) extends AtomicInteger(InitialState) {
  private[this] var _value: Int = _

  final def value: Int =
    if (get == ComputedState) _value else computeValue()

  final def computeValue(): Int = {
    while (true) {
      val state = get
      if (state == InitialState) {
        try {
          val result = rhs
          _value = result
          setFlagAndNotify(this, ComputedState)
          return result
        } catch {
          case ex: Throwable =>
            setFlagAndNotify(this, InitialState)
            throw ex
        }
      }
      else if (state == ComputedState)
        return _value
      else /* if (state == ComputingState || state == NotifyState) */
        waitForNotification(this, state)
    }

    throw null
  }
}

class LazyV1AFU(rhs: => Int) extends LazySimCellWithPublicBitmap {
  private[this] var _value: Int = _

  final def value: Int =
    if (flag == ComputedState) _value else computeValue()

  final def computeValue(): Int = {
    import LazySimCellWithPublicBitmap.updater

    while (true) {
      val state = flag
      if (state == InitialState) {
        try {
          val result = rhs
          _value = result
          setFlagAndNotify(this, updater, ComputedState)
          return result
        } catch {
          case ex: Throwable =>
            setFlagAndNotify(this, updater, InitialState)
            throw ex
        }
      }
      else if (state == ComputedState)
        return _value
      else /* if (state == ComputingState || state == NotifyState) */
        waitForNotification(this, updater, state)
    }

    throw null
  }
}

class LazyV2(x: => Int) {
  private[this] var value_0: Int = _
  /*private[this]*/ var bitmap: Long = 0L

  final def value: Int = {
    while (true) {
      val flag = LazyVolatile.get(this, StaticConstants.bitmap_offset)
      val state = LazyVolatile.STATE(flag, 0)

      if (state == ComputedState) {
        return value_0
      } else if (state == InitialState) {
        if (LazyVolatile.CAS(this, StaticConstants.bitmap_offset, flag, ComputingState, 0)) {
          try {
            val result = x
            value_0 = result
            LazyVolatile.setFlag(this, StaticConstants.bitmap_offset, ComputedState, 0)
            return result
          }
          catch {
            case ex =>
              LazyVolatile.setFlag(this, StaticConstants.bitmap_offset, InitialState, 0)
              throw ex
          }
        }
      } else /* if (state == ComputingState || state == NotifyState) */ {
        LazyVolatile.wait4Notification(this, StaticConstants.bitmap_offset, flag, 0)
      }
    }
    throw null
  }
}

class LazyV2Bis(x: => Int) {
  private[this] var value_0: Int = _
  /*private[this]*/ var bitmap: Long = 0L

  final def value: Int = {
    def state = LazyVolatile.STATE(LazyVolatile.get(this, StaticConstants.bitmap_offset_bis), 0)
    if (state == ComputedState) value_0 else computeValue()
  }

  final def computeValue(): Int = {
    while (true) {
      val flag = LazyVolatile.get(this, StaticConstants.bitmap_offset_bis)
      val state = LazyVolatile.STATE(flag, 0)

      if (state == InitialState) {
        if (LazyVolatile.CAS(this, StaticConstants.bitmap_offset_bis, flag, ComputingState, 0)) {
          try {
            val result = x
            value_0 = result
            LazyVolatile.setFlag(this, StaticConstants.bitmap_offset_bis, ComputedState, 0)
            return result
          }
          catch {
            case ex =>
              LazyVolatile.setFlag(this, StaticConstants.bitmap_offset_bis, InitialState, 0)
              throw ex
          }
        }
      } else if (state == ComputedState) {
        return value_0
      } else /* if (state == ComputingState || state == NotifyState) */ {
        LazyVolatile.wait4Notification(this, StaticConstants.bitmap_offset_bis, flag, 0)
      }
    }
    throw null
  }
}

final class LazyHolder extends AtomicInteger(InitialState) {
  private[this] var _value: Int = _
  def value: Int = _value

  final def initialized: Boolean = get == ComputedState

  final def initialize(value: Int): Int = {
    _value = value
    setFlagAndNotify(ComputedState)
    value
  }

  final def setFlagAndNotify(update: Int): Unit = {
    if (getAndSet(update) == NotifyState)
      synchronized { notifyAll() }
  }

  final def waitForNotification(state: Int): Unit = {
    if (state == ComputingState)
      compareAndSet(ComputingState, NotifyState)

    synchronized {
      if (get != NotifyState) // make sure notification did not happen yet
        wait()
    }
  }
}

class LazyV3(rhs: => Int) {
  private[this] val holder = new LazyHolder

  final def value: Int =
    if (holder.initialized) holder.value else computeValue()

  final def computeValue(): Int = {
    while (true) {
      val state = holder.get
      if (state == InitialState) {
        try {
          return holder.initialize(rhs)
        } catch {
          case ex: Throwable =>
            holder.setFlagAndNotify(InitialState)
            throw ex
        }
      }
      else if (state == ComputedState)
        return holder.value
      else /* if (state == ComputingState || state == NotifyState) */
        holder.waitForNotification(state)
    }

    throw null
  }
}

object LazyRuntime {
  final val InitialState = 0
  final val ComputingState = 1
  final val NotifyState = 2
  final val ComputedState = 3


  def setFlagAndNotify(flag: AtomicInteger, update: Int): Unit = {
    if (flag.getAndSet(update) == NotifyState)
      flag.synchronized { flag.notifyAll() }
  }

  def waitForNotification(flag: AtomicInteger, state: Int): Unit = {
    if (state == ComputingState)
      flag.compareAndSet(ComputingState, NotifyState)

    flag.synchronized {
      if (flag.get != NotifyState) // make sure notification did not happen yet
        flag.wait()
    }
  }

  def setFlagAndNotify[T <: AnyRef](self: T, flag: AtomicIntegerFieldUpdater[T], update: Int): Unit = {
    if (flag.getAndSet(self, update) == NotifyState)
      self.synchronized { self.notifyAll() }
  }

  def waitForNotification[T <: AnyRef](self: T, flag: AtomicIntegerFieldUpdater[T], state: Int): Unit = {
    if (state == ComputingState)
      flag.compareAndSet(self, ComputingState, NotifyState)

    self.synchronized {
      if (flag.get(self) != NotifyState) // make sure notification did not happen yet
        self.wait()
    }
  }
}
