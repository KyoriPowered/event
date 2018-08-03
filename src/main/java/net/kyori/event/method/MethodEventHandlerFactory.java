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
package net.kyori.event.method;

import net.kyori.event.base.EventHandler;
import net.kyori.event.method.annotation.IncludeCancelled;
import net.kyori.event.method.annotation.Subscribe;
import net.kyori.event.method.executor.EventExecutor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.function.BiConsumer;

class MethodEventHandlerFactory<E, L> {
  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleMethodEventBus.class);

  private final EventExecutor.Factory<E, L> factory;
  private final SubscriberFilter<L> filter;

  MethodEventHandlerFactory(final EventExecutor.@NonNull Factory<E, L> factory) {
    this(factory, (SubscriberFilter<L>) SubscriberFilter.TRUE);
  }

  MethodEventHandlerFactory(final EventExecutor.@NonNull Factory<E, L> factory, final @NonNull SubscriberFilter<L> filter) {
    this.factory = factory;
    this.filter = filter;
  }

  void findHandlers(@NonNull L listener, BiConsumer<@NonNull Class<? extends E>, @NonNull EventHandler<E>> consumer) {
    for(final Method method : listener.getClass().getDeclaredMethods()) {
      final Subscribe definition = method.getAnnotation(Subscribe.class);
      if(definition == null || !this.filter.test(listener, method)) {
        continue;
      }
      final EventExecutor<E, L> executor;
      try {
        executor = this.factory.create(listener, method);
      } catch(final Exception e) {
        LOGGER.error("Encountered an exception while creating an event handler for method '" + method + '\'', e);
        continue;
      }

      Class<? extends E> eventClass = (Class<E>) method.getParameterTypes()[0];
      consumer.accept(eventClass, new MethodEventHandler<>(eventClass, method, executor, listener, definition.postOrder(), method.isAnnotationPresent(IncludeCancelled.class)));
    }
  }
}
