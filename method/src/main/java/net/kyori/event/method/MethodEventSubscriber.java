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
import net.kyori.event.EventSubscriber;
import net.kyori.event.PostOrder;
import net.kyori.event.ReifiedEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Implements {@link EventSubscriber} for a given {@link Method}.
 *
 * @param <E> the event type
 * @param <L> the listener type
 */
final class MethodEventSubscriber<E, L> implements EventSubscriber<E> {
  private final Class<? extends E> event;
  private final @Nullable Type generic;
  private final EventExecutor<E, L> executor;
  private final L listener;
  private final PostOrder priority;
  private final boolean includeCancelled;

  MethodEventSubscriber(final Class<? extends E> eventClass, final @NonNull Method method, final @NonNull EventExecutor<E, L> executor, final @NonNull L listener, final @NonNull PostOrder priority, final boolean includeCancelled) {
    this.event = eventClass;
    this.generic = ReifiedEvent.class.isAssignableFrom(this.event) ? genericType(method.getGenericParameterTypes()[0]) : null;
    this.executor = executor;
    this.listener = listener;
    this.priority = priority;
    this.includeCancelled = includeCancelled;
  }

  private static @Nullable Type genericType(final Type type) {
    if(type instanceof ParameterizedType) {
      return ((ParameterizedType) type).getActualTypeArguments()[0];
    }
    return null;
  }

  L listener() {
    return this.listener;
  }

  @Override
  public void invoke(final @NonNull E event) throws Throwable {
    this.executor.invoke(this.listener, event);
  }

  @Override
  public @NonNull PostOrder postOrder() {
    return this.priority;
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
    return Objects.hash(this.event, this.generic, this.executor, this.listener, this.priority, this.includeCancelled);
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
      && Objects.equals(this.priority, that.priority)
      && Objects.equals(this.includeCancelled, that.includeCancelled);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("event", this.event)
      .add("generic", this.generic)
      .add("executor", this.executor)
      .add("listener", this.listener)
      .add("priority", this.priority)
      .add("includeCancelled", this.includeCancelled)
      .toString();
  }
}
