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

import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;

/**
 * Encapsulates the outcome of a {@link EventBus#post(Object)} call.
 */
public final class PostResult {
  private static final PostResult SUCCESS = new PostResult(ImmutableList.of());

  /**
   * Marks that no exceptions were thrown by subscribers.
   *
   * @return a {@link PostResult} indicating success
   */
  public static @NonNull PostResult success() {
    return SUCCESS;
  }

  /**
   * Marks that exceptions were thrown by subscribers.
   *
   * @param exceptions the exceptions that were thrown
   * @return a {@link PostResult} indicating failure
   */
  public static @NonNull PostResult failure(final @NonNull List<Throwable> exceptions) {
    checkState(!exceptions.isEmpty(), "no exceptions present");
    return new PostResult(ImmutableList.copyOf(exceptions));
  }

  private final List<Throwable> exceptions;

  private PostResult(final @NonNull List<Throwable> exceptions) {
    this.exceptions = exceptions;
  }

  /**
   * Gets if the {@link EventBus#post(Object)} call was successful.
   *
   * @return if the call was successful
   */
  public boolean wasSuccessful() {
    return this.exceptions.isEmpty();
  }

  /**
   * Gets the exceptions that were thrown whilst posting the event to subscribers.
   *
   * @return the exceptions thrown by subscribers
   */
  public @NonNull List<Throwable> exceptions() {
    return this.exceptions;
  }
}
