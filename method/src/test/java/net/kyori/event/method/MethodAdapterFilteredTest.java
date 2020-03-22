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
package net.kyori.event.method;

import net.kyori.event.EventBus;
import net.kyori.event.PostResult;
import net.kyori.event.SimpleEventBus;
import net.kyori.event.method.annotation.DefaultMethodScanner;
import net.kyori.event.method.annotation.Subscribe;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MethodAdapterFilteredTest {
  private final AtomicInteger result = new AtomicInteger();
  private final EventBus<Object> bus = new SimpleEventBus<>(Object.class);
  private final MethodSubscriptionAdapter<Object> methodAdapter = new SimpleMethodSubscriptionAdapter<>(this.bus, new MethodHandleEventExecutorFactory<>(), new FilteredMethodScanner<>());

  @Test
  void testListener() throws PostResult.CompositeException {
    this.methodAdapter.register(new TestListener());
    this.bus.post(new TestEvent()).raise();
    assertEquals(1, this.result.get());
  }

  @Retention(RetentionPolicy.RUNTIME)
  @interface SomeFilter {}

  public final class TestEvent {}

  public final class FilteredMethodScanner<L> extends DefaultMethodScanner<L> {
    @Override
    public boolean shouldRegister(final @NonNull L listener, final @NonNull Method method) {
      return super.shouldRegister(listener, method) && method.isAnnotationPresent(MethodAdapterFilteredTest.SomeFilter.class);
    }
  }

  public class TestListener {
    @Subscribe
    public void withoutFilter(final TestEvent event) {
      MethodAdapterFilteredTest.this.result.getAndIncrement();
    }

    @SomeFilter
    @Subscribe
    public void withFilter(final TestEvent event) {
      MethodAdapterFilteredTest.this.result.getAndIncrement();
    }
  }
}
