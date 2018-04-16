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

import com.google.common.reflect.TypeToken;
import net.kyori.lunar.reflect.Reified;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventBusTestWithType {
  private final EventBus<Event, Listener> bus = new SimpleEventBus<>(new ASMEventExecutorFactory<>());

  @Test
  void testListener() {
    final MyListener listener = new MyListener();
    this.bus.register(listener);
    // first post should set result[0] to true
    this.bus.post(new MyEvent());
    assertTrue(listener.result[0]);
    assertFalse(listener.result[1]);
    assertFalse(listener.result[2]);
    listener.reset();
    this.bus.post(new GenericEvent<>(Foo.class));
    assertFalse(listener.result[0]);
    assertTrue(listener.result[1]);
    assertFalse(listener.result[2]);
    listener.reset();
    this.bus.post(new GenericEvent<>(Bar.class));
    assertFalse(listener.result[0]);
    assertFalse(listener.result[1]);
    assertTrue(listener.result[2]);
    this.bus.unregister(listener);
    listener.result[0] = false;
    // second post should not, as the listener has been unregistered
    this.bus.post(new MyEvent());
    assertFalse(listener.result[0]);
  }

  private interface Foo {}
  private interface Bar {}
  interface Event {}
  public class MyEvent implements Event {}
  public class GenericEvent<T> implements Event, Reified<T> {
    private final TypeToken<T> type;

    GenericEvent(final Class<T> type) {
      this.type = TypeToken.of(type);
    }

    @Override
    public @NonNull TypeToken<T> type() {
      return this.type;
    }
  }
  interface Listener {}

  public class MyListener implements Listener {
    final boolean[] result = new boolean[3];

    void reset() {
      for(int i = 0; i < this.result.length; i++) {
        this.result[i] = false;
      }
    }

    @Subscribe
    public void event(final MyEvent event) {
      this.result[0] = true;
    }

    @Subscribe
    public void foo(final GenericEvent<Foo> event) {
      this.result[1] = true;
    }

    @Subscribe
    public void bar(final GenericEvent<Bar> event) {
      this.result[2] = true;
    }
  }
}
