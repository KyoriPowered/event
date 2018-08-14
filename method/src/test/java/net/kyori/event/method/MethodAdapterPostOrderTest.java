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

import com.google.common.collect.Lists;
import net.kyori.event.EventBus;
import net.kyori.event.PostOrder;
import net.kyori.event.SimpleEventBus;
import net.kyori.event.method.annotation.Subscribe;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MethodAdapterPostOrderTest {
  private final List<Integer> results = new ArrayList<>();
  private final EventBus<Object> bus = new SimpleEventBus<>();
  private final MethodSubscriptionAdapter<Object> methodAdapter = new SimpleMethodSubscriptionAdapter<>(this.bus, new MethodHandleEventExecutorFactory<>());

  @Test
  void testListener() {
    this.methodAdapter.register(new TestListener());
    this.bus.post(new TestEvent());
    assertEquals(Lists.newArrayList(1, 2, 3), this.results);
  }

  public final class TestEvent {}

  public class TestListener {
    @Subscribe(value = PostOrder.EARLY)
    public void early(final TestEvent event) {
      MethodAdapterPostOrderTest.this.results.add(1);
    }

    @Subscribe(value = PostOrder.NORMAL)
    public void normal(final TestEvent event) {
      MethodAdapterPostOrderTest.this.results.add(2);
    }

    @Subscribe(value = PostOrder.LATE)
    public void late(final TestEvent event) {
      MethodAdapterPostOrderTest.this.results.add(3);
    }
  }
}
