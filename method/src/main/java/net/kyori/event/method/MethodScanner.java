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

import net.kyori.event.PostOrder;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Method;

/**
 * Determines which methods on a listener should be registered
 * as subscribers, and what properties they should have.
 *
 * @param <L> the listener type
 */
public interface MethodScanner<L> {
  /**
   * Gets if the factory should generate a subscriber for this method.
   *
   * @param listener the listener being scanned
   * @param method the method declaration being considered
   * @return if a subscriber should be registered
   */
  boolean shouldRegister(final @NonNull L listener, final @NonNull Method method);

  /**
   * Gets the {@link PostOrder} the resultant subscriber should be called at.
   *
   * @param listener the listener
   * @param method the method
   * @return the post order of this subscriber
   */
  @NonNull PostOrder postOrder(final @NonNull L listener, final @NonNull Method method);

  /**
   * Gets if cancelled events should be posted to the resultant subscriber.
   *
   * @param listener the listener      
   * @param method the method
   * @return if cancelled events should be posted
   */
  boolean consumeCancelledEvents(final @NonNull L listener, final @NonNull Method method);
}
