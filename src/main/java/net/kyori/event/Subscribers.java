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

import net.kyori.blizzard.NonNull;
import net.kyori.blizzard.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

final class Subscribers<E> {
  private final List<Subscriber<E>> all;
  private final Map<Subscribe.Priority, List<Subscriber<E>>> priorities = new EnumMap<>(Subscribe.Priority.class);

  Subscribers(@NonNull final List<Subscriber<E>> subscribers) {
    this.all = subscribers;

    for(final Subscribe.Priority priority : Subscribe.Priority.values()) {
      this.priorities.put(priority, new ArrayList<>());
    }

    for(final Subscriber<E> subscriber : subscribers) {
      this.priorities.get(subscriber.priority).add(subscriber);
    }
  }

  @NonNull
  List<Subscriber<E>> get(@Nullable final Subscribe.Priority priority) {
    if(priority == null) {
      return this.all;
    }
    return this.priorities.get(priority);
  }
}
