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

import com.google.common.base.MoreObjects;
import net.kyori.event.EventBus;
import net.kyori.event.EventSubscriber;
import net.kyori.event.ReifiedEvent;
import net.kyori.event.method.annotation.DefaultMethodScanner;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * A simple implementation of a method subscription adapter.
 *
 * @param <E> the event type
 * @param <L> the listener type
 */
public class SimpleMethodSubscriptionAdapter<E, L> implements MethodSubscriptionAdapter<L> {
  private final EventBus<E> bus;
  private final EventExecutor.Factory<E, L> factory;
  private final MethodScanner<L> methodScanner;

  public SimpleMethodSubscriptionAdapter(final @NonNull EventBus<E> bus, final EventExecutor.@NonNull Factory<E, L> factory) {
    this(bus, factory, DefaultMethodScanner.get());
  }

  public SimpleMethodSubscriptionAdapter(final @NonNull EventBus<E> bus, final EventExecutor.@NonNull Factory<E, L> factory, final @NonNull MethodScanner<L> methodScanner) {
    this.bus = bus;
    this.factory = factory;
    this.methodScanner = methodScanner;
  }

  @Override
  public void register(final @NonNull L listener) {
    this.findSubscribers(listener, this.bus::register);
  }

  @Override
  public void unregister(final @NonNull L listener) {
    this.bus.unregister(h -> h instanceof MethodEventSubscriber && ((MethodEventSubscriber) h).listener() == listener);
  }

  @SuppressWarnings("unchecked")
  private void findSubscribers(final @NonNull L listener, final BiConsumer<@NonNull Class<? extends E>, @NonNull EventSubscriber<E>> consumer) {
    for(final Method method : listener.getClass().getDeclaredMethods()) {
      if(!this.methodScanner.shouldRegister(listener, method)) {
        continue;
      }
      if (method.getParameterCount() != 1) {
        throw new SubscriberGenerationException("Unable to create an event subscriber for method '" + method + "'. Method must have only one parameter.");
      }
      final Class<?> methodParameterType = method.getParameterTypes()[0];
      if (!this.bus.eventType().isAssignableFrom(methodParameterType)) {
        throw new SubscriberGenerationException("Unable to create an event subscriber for method '" + method + "'. " +
          "Method parameter type '" + methodParameterType + "' does not extend event type '" + this.bus.eventType() + '\'');
      }
      final EventExecutor<E, L> executor;
      try {
        executor = this.factory.create(listener, method);
      } catch(final Exception e) {
        throw new SubscriberGenerationException("Encountered an exception while creating an event subscriber for method '" + method + '\'', e);
      }

      final Class<? extends E> eventClass = (Class<? extends E>) methodParameterType;
      final int postOrder = this.methodScanner.postOrder(listener, method);
      final boolean consumeCancelled = this.methodScanner.consumeCancelledEvents(listener, method);
      consumer.accept(eventClass, new MethodEventSubscriber<>(eventClass, method, executor, listener, postOrder, consumeCancelled));
    }
  }

  /**
   * Exception thrown when a {@link EventSubscriber} cannot be generated for a
   * {@link Method} at runtime.
   */
  public static final class SubscriberGenerationException extends RuntimeException {
    SubscriberGenerationException(final String message) {
      super(message);
    }
    SubscriberGenerationException(final String message, final Throwable cause) {
      super(message, cause);
    }
  }

  /**
   * Implements {@link EventSubscriber} for a given {@link Method}.
   *
   * @param <E> the event type
   * @param <L> the listener type
   */
  private static final class MethodEventSubscriber<E, L> implements EventSubscriber<E> {
    private final Class<? extends E> event;
    private final @Nullable Type generic;
    private final EventExecutor<E, L> executor;
    private final L listener;
    private final int postOrder;
    private final boolean includeCancelled;

    MethodEventSubscriber(final Class<? extends E> eventClass, final @NonNull Method method, final @NonNull EventExecutor<E, L> executor, final @NonNull L listener, final int postOrder, final boolean includeCancelled) {
      this.event = eventClass;
      this.generic = ReifiedEvent.class.isAssignableFrom(this.event) ? genericType(method.getGenericParameterTypes()[0]) : null;
      this.executor = executor;
      this.listener = listener;
      this.postOrder = postOrder;
      this.includeCancelled = includeCancelled;
    }

    private static @Nullable Type genericType(final Type type) {
      if(type instanceof ParameterizedType) {
        return ((ParameterizedType) type).getActualTypeArguments()[0];
      }
      return null;
    }

    @NonNull L listener() {
      return this.listener;
    }

    @Override
    public void invoke(final @NonNull E event) throws Throwable {
      this.executor.invoke(this.listener, event);
    }

    @Override
    public int postOrder() {
      return this.postOrder;
    }

    @Override
    public boolean consumeCancelledEvents() {
      return this.includeCancelled;
    }

    @Override
    public @Nullable Type genericType() {
      return this.generic;
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.event, this.generic, this.executor, this.listener, this.postOrder, this.includeCancelled);
    }

    @Override
    public boolean equals(final Object other) {
      if(this == other) return true;
      if(other == null || !(other instanceof MethodEventSubscriber<?, ?>)) return false;
      final MethodEventSubscriber<?, ?> that = (MethodEventSubscriber<?, ?>) other;
      return Objects.equals(this.event, that.event)
        && Objects.equals(this.generic, that.generic)
        && Objects.equals(this.executor, that.executor)
        && Objects.equals(this.listener, that.listener)
        && Objects.equals(this.postOrder, that.postOrder)
        && Objects.equals(this.includeCancelled, that.includeCancelled);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
        .add("event", this.event)
        .add("generic", this.generic)
        .add("executor", this.executor)
        .add("listener", this.listener)
        .add("priority", this.postOrder)
        .add("includeCancelled", this.includeCancelled)
        .toString();
    }
  }
}
