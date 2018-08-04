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

import net.kyori.event.base.SimpleEventBus;
import net.kyori.event.method.executor.EventExecutor;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A simple implementation of a method event bus.
 */
public class SimpleMethodEventBus<E, L> extends SimpleEventBus<E> implements MethodEventBus<E, L> {
  private final MethodEventHandlerFactory<E, L> factory;

  public SimpleMethodEventBus(final EventExecutor.@NonNull Factory<E, L> factory) {
    this.factory = new MethodEventHandlerFactory<>(factory);
  }

  public SimpleMethodEventBus(final EventExecutor.@NonNull Factory<E, L> factory, final @NonNull SubscriberFilter<L> filter) {
    this.factory = new MethodEventHandlerFactory<>(factory, filter);
  }

  @Override
  public void register(@NonNull L listener) {
    factory.findHandlers(listener, this::register);
  }

  @Override
  public void unregister(@NonNull L listener) {
    unregisterMatching(h -> h instanceof MethodEventHandler && ((MethodEventHandler) h).getListener() == listener);
  }

}
