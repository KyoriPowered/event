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
package net.kyori.event.method.annotation;

import net.kyori.event.PostOrder;
import net.kyori.event.method.MethodScanner;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Method;

/**
 * Implementation of {@link MethodScanner} using the built-in
 * {@link Subscribe} and {@link IgnoreCancelled} annotations.
 *
 * @param <L> the listener type
 */
public class DefaultMethodScanner<L> implements MethodScanner<L> {
  private static final DefaultMethodScanner INSTANCE = new DefaultMethodScanner();

  @SuppressWarnings("unchecked")
  public static <L> MethodScanner<L> get() {
    return (MethodScanner<L>) INSTANCE;
  }

  // Allow subclasses
  protected DefaultMethodScanner() {
  }

  @Override
  public boolean shouldRegister(final @NonNull L listener, final @NonNull Method method) {
    return method.getAnnotation(Subscribe.class) != null;
  }

  @Override
  public @NonNull PostOrder postOrder(final @NonNull L listener, final @NonNull Method method) {
    return method.getAnnotation(Subscribe.class).value();
  }

  @Override
  public boolean consumeCancelledEvents(final @NonNull L listener, final @NonNull Method method) {
    return !method.isAnnotationPresent(IgnoreCancelled.class);
  }
}
