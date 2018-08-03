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

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.reflect.TypeToken;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A handler registry.
 *
 * @param <E> the event type
 */
final class HandlerRegistry<E> {

  /**
   * Caches class --> a set of interfaces and classes that the class is or is a subtype of
   */
  private static final LoadingCache<Class<?>, Set<Class<?>>> CLASS_HIERARCHY = Caffeine.newBuilder()
    .weakKeys()
    .build(key -> (Set<Class<?>>) TypeToken.of(key).getTypes().rawTypes());

  /**
   * A raw, unresolved multimap of class --> event handler.
   * An event handler is mapped to the class it is registered with.
   */
  // technically a Multimap<Class<T>, EventHandler<? super T>>
  private final Multimap<Class<?>, EventHandler<?>> handlers = HashMultimap.create();

  /**
   * A cache containing a link between an event class, and the EventHandlers which
   * should be passed the given type of event.
   */
  private final LoadingCache<Class<?>, ListMultimap<PostOrder, EventHandler<?>>> cache = Caffeine.newBuilder()
    .initialCapacity(85)
    .build(eventClass -> {
      final List<EventHandler<?>> all = new ArrayList<>();
      final Set<? extends Class<?>> subTypes = CLASS_HIERARCHY.get(eventClass);
      assert subTypes != null;
      synchronized(this.lock) {
        for(final Class<?> type : subTypes) {
          all.addAll(this.handlers.get(type));
        }
      }

      ListMultimap<PostOrder, EventHandler<?>> byOrder = Multimaps.newListMultimap(new EnumMap<>(PostOrder.class), ArrayList::new);
      for(final EventHandler<?> handler : all) {
        byOrder.put(handler.postOrder(), handler);
      }
      return byOrder;
    });

  private final Object lock = new Object();

  HandlerRegistry() {
  }

  <T extends E> void register(final @NonNull Class<T> clazz, final @NonNull EventHandler<? super T> handler) {
    synchronized(this.lock) {
      this.handlers.put(clazz, handler);
      this.cache.invalidateAll();
    }
  }

  void unregister(final @NonNull EventHandler<?> handler) {
    unregisterMatching(h -> h.equals(handler));
  }

  void unregisterMatching(final @NonNull Predicate<EventHandler<?>> predicate) {
    synchronized(this.lock) {
      boolean dirty = this.handlers.values().removeIf(predicate);
      if(dirty) {
        this.cache.invalidateAll();
      }
    }
  }

  @NonNull List<EventHandler<?>> handlers(final @NonNull Object event, final @NonNull PostOrder priority) {
    return this.cache.get(event.getClass()).get(priority);
  }
}
