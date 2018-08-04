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

/**
 * Represents the order, relative to other posts, that a given {@link EventSubscriber} should receive events.
 */
public enum PostOrder {
  /**
   * Marks that the subscriber should be called first, before all other subscribers.
   */
  FIRST,
  /**
   * Marks that the subscriber should be called before {@link #NORMAL normal} subscribers.
   */
  EARLY,
  /**
   * Marks that the subscriber should be called with no special priority.
   */
  NORMAL,
  /**
   * Marks that the subscriber should be called after {@link #NORMAL normal} subscribers.
   */
  LATE,
  /**
   * Marks that the subscriber should be called last, after all other subscribers.
   */
  LAST;
}
