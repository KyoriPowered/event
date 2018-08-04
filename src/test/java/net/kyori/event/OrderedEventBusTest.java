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

import net.kyori.event.base.PostOrder;
import net.kyori.event.method.executor.ASMEventExecutorFactory;
import net.kyori.event.method.MethodEventBus;
import net.kyori.event.method.SimpleMethodEventBus;
import net.kyori.event.method.annotation.Subscribe;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderedEventBusTest {
  private final AtomicInteger earlyResult = new AtomicInteger();
  private final AtomicInteger normalResult = new AtomicInteger();
  private final AtomicInteger lateResult = new AtomicInteger();
  private final MethodEventBus<Object, Object> bus = new SimpleMethodEventBus<>(new ASMEventExecutorFactory<>());
  private final MethodEventBus<Object, Object> filteredBus = new SimpleMethodEventBus<>(new ASMEventExecutorFactory<>(), (listener, method) -> method.isAnnotationPresent(FilteredEventBusTest.SomeFilter.class));

  @Test
  void testListener() {
    this.bus.register(new TestListener());
    this.filteredBus.register(new TestListener());
    this.bus.post(new TestEvent());
    this.filteredBus.post(new TestEvent());
    assertEquals(1, this.earlyResult.get());
    assertEquals(2, this.normalResult.get());
    assertEquals(2, this.lateResult.get());
  }

  @Retention(RetentionPolicy.RUNTIME)
  @interface SomeFilter {}
  public final class TestEvent {}

  public class TestListener {
    @Subscribe(postOrder = PostOrder.EARLY)
    public void early(final TestEvent event) {
      OrderedEventBusTest.this.earlyResult.getAndIncrement();
    }

    @Subscribe(postOrder = PostOrder.NORMAL)
    public void normal(final TestEvent event) {
      OrderedEventBusTest.this.normalResult.getAndIncrement();
    }

    @SomeFilter
    @Subscribe(postOrder = PostOrder.NORMAL)
    public void filteredNormal(final TestEvent event) {
      OrderedEventBusTest.this.normalResult.getAndIncrement();
    }

    @Subscribe(postOrder = PostOrder.LATE)
    public void late(final TestEvent event) {
      OrderedEventBusTest.this.lateResult.getAndIncrement();
    }

    @SomeFilter
    @Subscribe(postOrder = PostOrder.LATE)
    public void filteredLate(final TestEvent event) {
      OrderedEventBusTest.this.lateResult.getAndIncrement();
    }
  }
}
