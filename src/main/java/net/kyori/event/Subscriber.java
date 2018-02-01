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

import com.google.common.base.MoreObjects;
import net.kyori.blizzard.NonNull;
import net.kyori.blizzard.Nullable;
import net.kyori.lunar.reflect.Reified;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * A subscriber.
 *
 * @param <E> the event type
 */
final class Subscriber<E> implements Comparable<Subscriber<?>>, EventProcessor<E> {
  @NonNull final Class<?> event;
  @Nullable private final Type generic;
  @NonNull final EventProcessor<E> processor;
  final Subscribe.Priority priority;
  private final boolean includeCancelled;

  Subscriber(@NonNull final Method method, @NonNull final EventProcessor<E> processor, @NonNull final Subscribe.Priority priority, final boolean includeCancelled) {
    this.event = method.getParameterTypes()[0];
    this.generic = Reified.class.isAssignableFrom(this.event) ? genericType(method.getGenericParameterTypes()[0]) : null;
    this.processor = processor;
    this.priority = priority;
    this.includeCancelled = includeCancelled;
  }

  @Nullable
  private static Type genericType(final Type type) {
    if(type instanceof ParameterizedType) {
      return ((ParameterizedType) type).getActualTypeArguments()[0];
    }
    return null;
  }

  @Override
  public void invoke(@NonNull final E event) throws Throwable {
    if(event instanceof Cancellable && (((Cancellable) event).cancelled() && !this.includeCancelled)) {
      return;
    }
    // safe to cast event to generic when this.generic is not null
    if(this.generic != null && !((Reified<?>) event).type().getType().equals(this.generic)) {
      return;
    }
    this.processor.invoke(event);
  }

  @Override
  public int compareTo(final Subscriber<?> that) {
    return this.priority.compareTo(that.priority);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.event, this.generic, this.processor, this.priority, this.includeCancelled);
  }

  @Override
  public boolean equals(final Object other) {
    if(this == other) return true;
    if(other == null || !(other instanceof Subscriber<?>)) return false;
    final Subscriber<?> that = (Subscriber<?>) other;
    return Objects.equals(this.event, that.event)
      && Objects.equals(this.generic, that.generic)
      && Objects.equals(this.processor, that.processor)
      && Objects.equals(this.priority, that.priority)
      && Objects.equals(this.includeCancelled, that.includeCancelled);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("event", this.event)
      .add("generic", this.generic)
      .add("processor", this.processor)
      .add("priority", this.priority)
      .add("includeCancelled", this.includeCancelled)
      .toString();
  }
}
