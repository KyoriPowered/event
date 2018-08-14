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

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class SubTypeEventBusTest {
  private final EventBus<Number> bus = new SimpleEventBus<>(Number.class);

  @Test
  void testSubTypes() throws PostResult.CompositeException {
    final AtomicReference<boolean[]> calls = new AtomicReference<>();

    this.bus.register(Integer.class, event -> calls.get()[0] = true);
    this.bus.register(Number.class, event -> calls.get()[1] = true);
    this.bus.register(Double.class, event -> calls.get()[2] = true);

    calls.set(new boolean[3]);
    this.bus.post(1.34f).raise();
    assertArrayEquals(calls.get(), new boolean[]{false, true, false});

    calls.set(new boolean[3]);
    this.bus.post(13).raise();
    assertArrayEquals(calls.get(), new boolean[]{true, true, false});

    calls.set(new boolean[3]);
    this.bus.post(3.14d).raise();
    assertArrayEquals(calls.get(), new boolean[]{false, true, true});
  }
}
