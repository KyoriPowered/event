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
import net.kyori.event.SimpleEventBus;
import net.kyori.event.method.annotation.Subscribe;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class MethodAdapterTypeTest {
  private final EventBus<Event> bus = new SimpleEventBus<>(Event.class);
  private final MethodSubscriptionAdapter<Listener> methodAdapter = new SimpleMethodSubscriptionAdapter<>(this.bus, new MethodHandleEventExecutorFactory<>());

  @Test
  void testListener() {
    final MyListener listener = new MyListener();
    assertThrows(SimpleMethodSubscriptionAdapter.SubscriberGenerationException.class, () -> this.methodAdapter.register(listener));
  }

  interface Event {}
  interface Listener {}

  public class MyListener implements Listener {
    // string does not extend Event, so attempting
    // to register this listener should throw an error
    @Subscribe
    public void error(final String string) {
      System.out.println("hello " + string);
    }
  }
}
