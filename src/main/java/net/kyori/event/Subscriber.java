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

import com.google.common.base.MoreObjects;

import java.util.Objects;

import javax.annotation.Nonnull;

/**
 * A subscriber.
 *
 * @param <E> the event type
 */
final class Subscriber<E> implements EventProcessor<E> {

  @Nonnull final Class<?> event;
  @Nonnull final EventProcessor<E> processor;

  Subscriber(@Nonnull final Class<?> event, @Nonnull final EventProcessor<E> processor) {
    this.event = event;
    this.processor = processor;
  }

  @Override
  public void invoke(@Nonnull final E event) throws Throwable {
    this.processor.invoke(event);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.event, this.processor);
  }

  @Override
  public boolean equals(final Object other) {
    if(this == other) return true;
    if(other == null || !(other instanceof Subscriber<?>)) return false;
    final Subscriber<?> that = (Subscriber<?>) other;
    return Objects.equals(this.event, that.event)
      && Objects.equals(this.processor, that.processor);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("event", this.event)
      .add("processor", this.processor)
      .toString();
  }
}
