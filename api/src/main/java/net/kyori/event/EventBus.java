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

import com.google.common.collect.SetMultimap;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Predicate;

/**
 * Base interface of the library, representing an object which accepts
 * {@link #register(Class, EventSubscriber) registration} of {@link EventSubscriber}s,
 * and supports {@link #post(Object) posting} events to them.
 *
 * <p>{@link EventSubscriber}s will receive all events which are applicable
 * (can be casted) to their registered type.</p>
 *
 * @param <E> the event type
 */
public interface EventBus<E> {
  /**
   * Gets the event type of the bus.
   *
   * <p>This is represented by the <code>E</code> type parameter.</p>
   *
   * @return the event type
   */
  @NonNull Class<E> eventType();

  /**
   * Posts an event to all registered subscribers.
   *
   * @param event the event
   * @return the post result of the operation
   */
  @NonNull PostResult post(final @NonNull E event);

  /**
   * Registers the given {@code subscriber} to receive events.
   *
   * @param clazz the registered type. the subscriber will only receive events which can be casted to this type.
   * @param subscriber the subscriber
   * @param <T> the event type
   */
  <T extends E> void register(final @NonNull Class<T> clazz, final @NonNull EventSubscriber<? super T> subscriber);

  /**
   * Unregisters a previously registered {@code subscriber}.
   *
   * @param subscriber the subscriber
   */
  void unregister(final @NonNull EventSubscriber<?> subscriber);

  /**
   * Unregisters all subscribers matching the {@code predicate}.
   *
   * @param predicate the predicate to test subscribers for removal
   */
  void unregister(final @NonNull Predicate<EventSubscriber<?>> predicate);

  /**
   * Unregisters all subscribers.
   */
  void unregisterAll();

  /**
   * Determines whether or not the specified event has subscribers.
   *
   * @param clazz the event clazz
   * @return whether or not the specified event has subscribers
   * @param <T> the event type
   */
  <T extends E> boolean hasSubscribers(final @NonNull Class<T> clazz);

  /**
   * Gets an immutable multimap containing all of the subscribers
   * currently registered.
   *
   * <p>Each subscriber is mapped to the type defined when it was
   * initially {@link #register(Class, EventSubscriber) registered}.</p>
   *
   * @return a multimap of the current subscribers
   */
  @NonNull SetMultimap<Class<?>, EventSubscriber<?>> subscribers();
}
