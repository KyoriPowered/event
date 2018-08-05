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
package net.kyori.event.method;

import net.kyori.event.PostOrder;
import net.kyori.event.method.annotation.DefaultMethodScanner;
import net.kyori.event.method.annotation.Subscribe;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MethodEventBusPostOrderTest {
  private final AtomicInteger earlyResult = new AtomicInteger();
  private final AtomicInteger normalResult = new AtomicInteger();
  private final AtomicInteger lateResult = new AtomicInteger();
  private final MethodEventBus<Object, Object> bus = new SimpleMethodEventBus<>(new MethodHandleEventExecutorFactory<>());
  private final MethodEventBus<Object, Object> filteredBus = new SimpleMethodEventBus<>(new MethodHandleEventExecutorFactory<>(), new FilteredMethodScanner<>());

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

  public final class FilteredMethodScanner<L> extends DefaultMethodScanner<L> {
    @Override
    public boolean shouldRegister(final @NonNull L listener, final @NonNull Method method) {
      return super.shouldRegister(listener, method) && method.isAnnotationPresent(MethodEventBusPostOrderTest.SomeFilter.class);
    }
  }

  public class TestListener {
    @Subscribe(value = PostOrder.EARLY)
    public void early(final TestEvent event) {
      MethodEventBusPostOrderTest.this.earlyResult.getAndIncrement();
    }

    @Subscribe(value = PostOrder.NORMAL)
    public void normal(final TestEvent event) {
      MethodEventBusPostOrderTest.this.normalResult.getAndIncrement();
    }

    @SomeFilter
    @Subscribe(value = PostOrder.NORMAL)
    public void filteredNormal(final TestEvent event) {
      MethodEventBusPostOrderTest.this.normalResult.getAndIncrement();
    }

    @Subscribe(value = PostOrder.LATE)
    public void late(final TestEvent event) {
      MethodEventBusPostOrderTest.this.lateResult.getAndIncrement();
    }

    @SomeFilter
    @Subscribe(value = PostOrder.LATE)
    public void filteredLate(final TestEvent event) {
      MethodEventBusPostOrderTest.this.lateResult.getAndIncrement();
    }
  }
}
