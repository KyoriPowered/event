/*
 * This file is part of event, licensed under the MIT License.
 *
 * Copyright (c) 2017 KyoriPowered
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

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EventBusTestWithType {

  private final EventBus<Event, Listener> bus = new SimpleEventBus<>(new ASMEventExecutorFactory<>());

  @Test
  public void testListener() {
    final MyListener listener = new MyListener();
    this.bus.register(listener);
    // first post should set result[0] to true
    this.bus.post(new MyEvent());
    assertTrue(listener.result[0]);
    this.bus.unregister(listener);
    listener.result[0] = false;
    // second post should not, as the listener has been unregistered
    this.bus.post(new MyEvent());
    assertFalse(listener.result[0]);
  }

  public interface Event {}
  public class MyEvent implements Event {}
  public interface Listener {}

  public class MyListener implements Listener {

    final boolean[] result = new boolean[1];

    @Subscribe
    public void event(final MyEvent event) {
      this.result[0] = true;
    }
  }
}
