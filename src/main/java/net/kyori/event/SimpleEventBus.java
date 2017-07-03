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

import javax.annotation.Nonnull;

/**
 * A simple implementation of an event bus.
 */
public class SimpleEventBus<E, L> implements EventBus<E, L> {

  private final SubscriberRegistry<E, L> registry;

  public SimpleEventBus(final EventExecutor.Factory<E, L> factory) {
    this.registry = new SubscriberRegistry<>(factory);
  }

  @Override
  public void register(@Nonnull final L listener) {
    this.registry.register(listener);
  }

  @Override
  public void unregister(@Nonnull final L listener) {
    this.registry.unregister(listener);
  }

  @Override
  public <T extends Throwable> void post(@Nonnull final E event) throws T {
    for(final Subscriber<E> subscriber : this.registry.subscribers(event)) {
      try {
        subscriber.invoke(event);
      } catch(final EventException e) {
        throw (T) e;
      } catch(final Throwable t) {
        throw (T) new EventException(event, t);
      }
    }
  }
}
