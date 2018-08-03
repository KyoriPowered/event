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

import com.google.common.collect.ImmutableList;
import net.kyori.lunar.reflect.Reified;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Predicate;

/**
 * A simple implementation of an event bus.
 */
public class SimpleEventBus<E> implements EventBus<E> {
  private final HandlerRegistry<E> registry;

  public SimpleEventBus() {
    this.registry = new HandlerRegistry<>();
  }

  @Override
  public <T extends E> void register(final @NonNull Class<T> clazz, final @NonNull EventHandler<? super T> handler) {
    this.registry.register(clazz, handler);
  }

  @Override
  public void unregister(final @NonNull EventHandler<?> handler) {
    this.registry.unregister(handler);
  }

  protected void unregisterMatching(final @NonNull Predicate<EventHandler<?>> predicate) {
    this.registry.unregisterMatching(predicate);
  }

  @Override
  public PostResult post(final @NonNull E event) {
    ImmutableList.Builder<Throwable> exceptions = null; // save on an allocation
    for(final PostOrder priority : PostOrder.values()) {
      for(final EventHandler subscriber : this.registry.handlers(event, priority)) {
        if(event instanceof Cancellable && (((Cancellable) event).cancelled() && !subscriber.postCancelledEvents())) {
          continue;
        }
        if(event instanceof Reified<?> && !((Reified<?>) event).type().getType().equals(subscriber.genericType())) {
          continue;
        }
        try {
          //noinspection unchecked
          subscriber.invoke(event);
        } catch(final Throwable e) {
          if (exceptions == null) {
            exceptions = ImmutableList.builder();
          }
          exceptions.add(e);
        }
      }
    }
    if(exceptions == null) {
      return PostResult.success();
    } else {
      return PostResult.failure(exceptions.build());
    }
  }
}
