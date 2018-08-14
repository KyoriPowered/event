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
import com.google.common.collect.ImmutableMap;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

/**
 * Encapsulates the outcome of a {@link EventBus#post(Object)} call.
 */
public final class PostResult {
  private static final PostResult SUCCESS = new PostResult(Collections.emptyMap());

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
  public static @NonNull PostResult failure(final @NonNull Map<EventSubscriber<?>, Throwable> exceptions) {
    checkState(!exceptions.isEmpty(), "no exceptions present");
    return new PostResult(ImmutableMap.copyOf(exceptions));
  }

  private final Map<EventSubscriber<?>, Throwable> exceptions;

  private PostResult(final @NonNull Map<EventSubscriber<?>, Throwable> exceptions) {
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
  public @NonNull Map<EventSubscriber<?>, Throwable> exceptions() {
    return this.exceptions;
  }

  /**
   * Raises a {@link CompositeException} if the posting was not
   * {@link #wasSuccessful() successful}.
   *
   * @throws CompositeException if posting was not successful
   */
  public void raise() throws CompositeException {
    if(!this.wasSuccessful()) {
      throw new CompositeException(this);
    }
  }

  @Override
  public String toString() {
    if(this.wasSuccessful()) {
      return MoreObjects.toStringHelper(this)
        .add("type", "success")
        .toString();
    } else {
      return MoreObjects.toStringHelper(this)
        .add("type", "failure")
        .add("exceptions", this.exceptions().values())
        .toString();
    }
  }

  /**
   * Exception encapsulating a combined {@link #failure(Map) failure}.
   */
  public static final class CompositeException extends Exception {
    private final PostResult result;

    CompositeException(final @NonNull PostResult result) {
      super("Exceptions occurred whilst posting to subscribers");
      this.result = result;
    }

    /**
     * Gets the result that created this composite exception.
     *
     * @return the result
     */
    public @NonNull PostResult result() {
      return this.result;
    }

    /**
     * Prints all of the stack traces involved in the composite exception.
     *
     * @see Exception#printStackTrace()
     */
    public void printAllStackTraces() {
      this.printStackTrace();
      for(final Throwable exception : this.result.exceptions().values()) {
        exception.printStackTrace();
      }
    }
  }
}
