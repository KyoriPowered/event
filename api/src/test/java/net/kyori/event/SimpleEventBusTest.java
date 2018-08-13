/*
 * This file is part of event, licensed under the MIT License.
 *
 * Copyright (c) 2017-2018 KyoriPowered
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
package net.kyori.event;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleEventBusTest {
  private final EventBus<Object> bus = new SimpleEventBus<>();

  @Test
  void testListener() {
    final TestEvent event = new TestEvent();

    assertFalse(this.bus.hasSubscribers(TestEvent.class));

    this.bus.register(TestEvent.class, new EventSubscriber<TestEvent>() {
      @Override
      public void invoke(final @NonNull TestEvent event) {
        event.count.incrementAndGet();
      }

      @Override
      public boolean consumeCancelledEvents() {
        return false;
      }
    });

    assertTrue(this.bus.hasSubscribers(TestEvent.class));

    event.cancelled(true);
    this.bus.post(event);
    assertEquals(0, event.count.get());

    this.bus.register(TestEvent.class, e -> e.count.incrementAndGet());

    this.bus.post(event);
    assertEquals(1, event.count.get());

    event.cancelled(false);
    this.bus.post(event);
    assertEquals(3, event.count.get());
  }

  public final class TestEvent extends Cancellable.Impl {
    final AtomicInteger count = new AtomicInteger(0);
  }
}
