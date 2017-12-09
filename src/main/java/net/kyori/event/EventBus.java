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

import net.kyori.blizzard.NonNull;

/**
 * An event bus.
 *
 * @param <E> the event type
 * @param <L> the listener type
 */
public interface EventBus<E, L> {
  /**
   * Registers all subscriber methods on {@code listener} to receive events.
   *
   * @param listener the listener
   */
  void register(@NonNull final L listener);

  /**
   * Unregisters all subscriber methods on a registered {@code listener}.
   *
   * @param listener the listener
   */
  void unregister(@NonNull final L listener);

  /**
   * Posts an event to all registered subscribers.
   *
   * @param event the event
   * @param <T> the throwable type
   * @throws T if an exception was encountered
   */
  <T extends Throwable> void post(@NonNull final E event) throws T;
}
