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
package net.kyori.event.base;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Base interface of the library, representing an object which accepts
 * {@link #register(Class, EventHandler) registration} of {@link EventHandler}s,
 * and supports {@link #post(Object) posting} events to them.
 *
 * <p>{@link EventHandler}s will receive all events which are applicable
 * (can be casted) to their registered type.</p>
 *
 * @param <E> the event type
 */
public interface EventBus<E> {

  /**
   * Registers the given {@code handler} to receive events.
   *
   * @param clazz the registered type. the handler will only receive events
   *              which can be casted to this type.
   * @param handler the handler
   */
  <T extends E> void register(final @NonNull Class<T> clazz, final @NonNull EventHandler<? super T> handler);

  /**
   * Unregisters a previously registered {@code listener}.
   *
   * @param handler the handler
   */
  void unregister(final @NonNull EventHandler<?> handler);

  /**
   * Posts an event to all registered handlers.
   *
   * @param event the event
   * @return the post result of the operation
   */
  PostResult post(final @NonNull E event);

}
