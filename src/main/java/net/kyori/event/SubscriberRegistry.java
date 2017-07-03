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

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.base.MoreObjects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

/**
 * A subscriber registry.
 *
 * @param <E> the event type
 * @param <L> the listener type
 */
final class SubscriberRegistry<E, L> {

  private static final Logger LOGGER = LoggerFactory.getLogger(SubscriberRegistry.class);
  private final Object lock = new Object();
  private final EventExecutor.Factory<E, L> factory;
  private final SubscriberFilter<L> filter;
  private final Multimap<Class<?>, Subscriber<E>> subscribers = HashMultimap.create();
  private final LoadingCache<Class<?>, List<Subscriber<E>>> cache = Caffeine.newBuilder()
    .initialCapacity(85)
    .build(eventClass -> {
      final List<Subscriber<E>> subscribers = new ArrayList<>();
      final Set<? extends Class<?>> types = TypeToken.of(eventClass).getTypes().rawTypes();
      synchronized(SubscriberRegistry.this.lock) {
        for(final Class<?> type : types) {
          subscribers.addAll(SubscriberRegistry.this.subscribers.get(type));
        }
      }
      return subscribers;
    });

  SubscriberRegistry(@Nonnull final EventExecutor.Factory<E, L> factory, @Nonnull final SubscriberFilter<L> filter) {
    this.factory = factory;
    this.filter = filter;
  }

  void register(@Nonnull final L listener) {
    final List<Subscriber<E>> subscribers = new ArrayList<>();
    for(final Method method : listener.getClass().getDeclaredMethods()) {
      final Subscribe definition = method.getAnnotation(Subscribe.class);
      if(definition == null || !this.filter.test(listener, method)) {
        continue;
      }
      final EventExecutor<E, L> executor;
      try {
        executor = this.factory.create(listener, method);
      } catch(final Exception e) {
        LOGGER.error("Encountered an exception while creating an event executor for method '" + method + '\'', e);
        continue;
      }
      subscribers.add(new Subscriber<>(method.getParameterTypes()[0], new EventProcessorImpl<>(executor, listener)));
    }
    if(subscribers.isEmpty()) {
      return;
    }
    synchronized(this.lock) {
      subscribers.forEach(subscriber -> this.subscribers.put(subscriber.event, subscriber));
      this.cache.invalidateAll();
    }
  }

  void unregister(@Nonnull final L listener) {
    synchronized(this.lock) {
      boolean dirty = false;
      final Iterator<Subscriber<E>> it = this.subscribers.values().iterator();
      while(it.hasNext()) {
        final Subscriber subscriber = it.next();
        if(subscriber.processor instanceof EventProcessorImpl && ((EventProcessorImpl) subscriber.processor).listener.equals(listener)) {
          it.remove();
          dirty = true;
        }
      }
      if(dirty) {
        this.cache.invalidateAll();
      }
    }
  }

  @Nonnull
  List<Subscriber<E>> subscribers(@Nonnull final Object event) {
    return this.cache.get(event.getClass());
  }

  static final class EventProcessorImpl<E, L> implements EventProcessor<E> {

    @Nonnull private final EventExecutor<E, L> executor;
    @Nonnull private final L listener;

    EventProcessorImpl(@Nonnull final EventExecutor<E, L> executor, @Nonnull final L listener) {
      this.executor = executor;
      this.listener = listener;
    }

    @Override
    public void invoke(@Nonnull final E event) throws Throwable {
      this.executor.execute(this.listener, event);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
        .add("executor", this.executor)
        .add("listener", this.listener)
        .toString();
    }
  }
}
