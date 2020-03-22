/*
 * This file is part of event, licensed under the MIT License.
 *
 * Copyright (c) 2017-2020 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.kyori.event.method.asm;

import net.kyori.event.Cancellable;
import net.kyori.event.EventBus;
import net.kyori.event.PostResult;
import net.kyori.event.SimpleEventBus;
import net.kyori.event.method.MethodSubscriptionAdapter;
import net.kyori.event.method.SimpleMethodSubscriptionAdapter;
import net.kyori.event.method.annotation.IgnoreCancelled;
import net.kyori.event.method.annotation.Subscribe;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MethodAdapterASMTest {
  private final EventBus<Object> bus = new SimpleEventBus<>(Object.class);
  private final MethodSubscriptionAdapter<Object> methodAdapter = new SimpleMethodSubscriptionAdapter<>(this.bus, new ASMEventExecutorFactory<>());

  @Test
  void testListener() throws PostResult.CompositeException {
    final TestListener listener = new TestListener();
    this.methodAdapter.register(listener);
    final TestEvent event = new TestEvent();
    event.cancelled(true);
    this.bus.post(event).raise();
    assertEquals(0, event.count.get());
    event.cancelled(false);
    this.bus.post(event).raise();
    final TestListenerWithCancelled listenerWithCancelled = new TestListenerWithCancelled();
    this.methodAdapter.register(listenerWithCancelled);
    event.cancelled(false);
    this.bus.post(event).raise();
    assertEquals(3, event.count.get());
    this.methodAdapter.unregister(listener);
    this.methodAdapter.unregister(listenerWithCancelled);
    event.cancelled(false);
    this.bus.post(event).raise();
    assertEquals(0, event.count.get());
  }

  public final class TestEvent extends Cancellable.Impl {
    final AtomicInteger count = new AtomicInteger(0);

    @Override
    public void cancelled(final boolean cancelled) {
      this.cancelled = cancelled;
      this.count.set(0);
    }
  }

  public class TestListener {
    @IgnoreCancelled
    @Subscribe
    public void event(final TestEvent event) {
      event.count.incrementAndGet();
    }

    @IgnoreCancelled
    @Subscribe
    public void cancelledExcluded(final TestEvent event) {
      event.count.incrementAndGet();
    }
  }

  public class TestListenerWithCancelled {
    @Subscribe
    public void cancelledIncluded(final TestEvent event) {
      event.count.incrementAndGet();
    }
  }
}
