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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A subscriber registry.
 *
 * @param <E> the event type
 */
final class SubscriberRegistry<E> {
  /**
   * A cache of class --> a set of interfaces and classes that the class is or is a subtype of.
   */
  private static final LoadingCache<Class<?>, Set<Class<?>>> CLASS_HIERARCHY = CacheBuilder.newBuilder()
    .weakKeys()
    .build(CacheLoader.from(key -> (Set<Class<?>>) TypeToken.of(key).getTypes().rawTypes()));
  /**
   * A raw, unresolved multimap of class --> event subscriber.
   *
   * <p>An event subscriber is mapped to the class it is registered with.</p>
   */
  // technically a Multimap<Class<T>, EventSubscriber<? super T>>
  private final SetMultimap<Class<?>, EventSubscriber<?>> subscribers = HashMultimap.create();
  /**
   * A cache containing a link between an event class, and the eventsubscribers which
   * should be passed the given type of event.
   */
  private final LoadingCache<Class<?>, List<EventSubscriber<?>>> cache = CacheBuilder.newBuilder()
    .initialCapacity(85)
    .build(CacheLoader.from(eventClass -> {
      final List<EventSubscriber<?>> subscribers = new ArrayList<>();
      final Set<? extends Class<?>> types = CLASS_HIERARCHY.getUnchecked(eventClass);
      assert types != null;
      synchronized(this.lock) {
        for(final Class<?> type : types) {
          subscribers.addAll(this.subscribers.get(type));
        }
      }

      subscribers.sort(Comparator.comparing(EventSubscriber::postOrder));
      return subscribers;
    }));
  private final Object lock = new Object();

  SubscriberRegistry() {
  }

  <T extends E> void register(final @NonNull Class<T> clazz, final @NonNull EventSubscriber<? super T> subscriber) {
    synchronized(this.lock) {
      this.subscribers.put(clazz, subscriber);
      this.cache.invalidateAll();
    }
  }

  void unregister(final @NonNull EventSubscriber<?> subscriber) {
    this.unregisterMatching(h -> h.equals(subscriber));
  }

  void unregisterMatching(final @NonNull Predicate<EventSubscriber<?>> predicate) {
    synchronized(this.lock) {
      final boolean dirty = this.subscribers.values().removeIf(predicate);
      if(dirty) {
        this.cache.invalidateAll();
      }
    }
  }

  void unregisterAll() {
    synchronized(this.lock) {
      this.subscribers.clear();
      this.cache.invalidateAll();
    }
  }

  @NonNull SetMultimap<Class<?>, EventSubscriber<?>> subscribers() {
    synchronized(this.lock) {
      return ImmutableSetMultimap.copyOf(this.subscribers);
    }
  }

  @NonNull List<EventSubscriber<?>> subscribers(final @NonNull Object event) {
    return this.cache.getUnchecked(event.getClass());
  }

  @NonNull List<EventSubscriber<?>> subscribers(final @NonNull Class<?> clazz) {
    return this.cache.getUnchecked(clazz);
  }
}
