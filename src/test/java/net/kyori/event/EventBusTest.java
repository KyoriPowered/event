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

public class EventBusTest {

  private final boolean[] result = new boolean[1];
  private final EventBus bus = new SimpleEventBus(new ASMEventExecutorFactory());

  @Test
  public void testListener() {
    final TestListener listener = new TestListener();
    this.bus.register(listener);
    // first post should set result[0] to true
    this.bus.post(new TestEvent());
    assertTrue(this.result[0]);
    this.bus.unregister(listener);
    this.result[0] = false;
    // second post should not, as the listener has been unregistered
    this.bus.post(new TestEvent());
    assertFalse(this.result[0]);
  }

  public final class TestEvent {

  }

  public class TestListener {

    @Subscribe
    public void event(final TestEvent event) {
      EventBusTest.this.result[0] = true;
    }
  }
}
