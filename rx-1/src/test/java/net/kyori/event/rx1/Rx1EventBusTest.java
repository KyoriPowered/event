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
package net.kyori.event.rx1;

import org.junit.jupiter.api.Test;
import rx.Subscription;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Rx1EventBusTest {
  @Test
  void test() {
    final AtomicInteger acks = new AtomicInteger();
    final Rx1EventBus<Object> bus = new Rx1EventBus<>();
    final Subscription subscription = bus.observable(TestEvent.class)
      .subscribe(event -> acks.incrementAndGet());
    for(int i = 0; i < 3; i++) {
      bus.post((TestEvent) () -> "purple");
    }
    assertEquals(3, acks.get());
    subscription.unsubscribe();
    for(int i = 0; i < 3; i++) {
      bus.post((TestEvent) () -> "red");
    }
    assertEquals(3, acks.get());
  }

  public interface TestEvent {
    String color();
  }
}
