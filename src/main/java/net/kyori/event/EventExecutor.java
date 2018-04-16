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

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Method;

/**
 * An event executor.
 *
 * @param <E> the event type
 * @param <L> the listener type
 */
@FunctionalInterface
public interface EventExecutor<E, L> {
  /**
   * Executes an event.
   *
   * @param listener the listener
   * @param event the event
   * @throws Throwable if an exception occurred
   */
  void execute(final @NonNull L listener, final @NonNull E event) throws Throwable;

  /**
   * An event executor factory.
   *
   * @param <E> the event type
   * @param <L> the listener type
   */
  @FunctionalInterface
  interface Factory<E, L> {
    /**
     * Creates an event executor.
     *
     * @param object the object
     * @param method the method
     * @return an event executor
     * @throws Exception if an exception occurred while creating an executor
     */
    @NonNull EventExecutor<E, L> create(final @NonNull Object object, final @NonNull Method method) throws Exception;
  }
}
