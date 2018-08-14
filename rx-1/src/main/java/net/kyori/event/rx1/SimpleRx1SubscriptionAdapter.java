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
package net.kyori.event.rx1;

import net.kyori.event.EventBus;
import net.kyori.event.EventSubscriber;
import org.checkerframework.checker.nullness.qual.NonNull;
import rx.Emitter;
import rx.Observable;
import rx.functions.Action1;

/**
 * A simple implementation of a RxJava 1 subscription adapter.
 *
 * @param <E> the event type
 */
public class SimpleRx1SubscriptionAdapter<E> implements Rx1SubscriptionAdapter<E> {
  private final EventBus<E> bus;

  public SimpleRx1SubscriptionAdapter(final @NonNull EventBus<E> bus) {
    this.bus = bus;
  }

  @Override
  public <T extends E> @NonNull Observable<T> observable(final @NonNull Class<T> event) {
    return this.observable(emitter -> {
      final EventSubscriber<T> subscriber = e -> {
        try {
          emitter.onNext(e);
        } catch(final Throwable t) {
          emitter.onError(t);
        }
      };
      this.bus.register(event, subscriber);
      emitter.setCancellation(() -> this.bus.unregister(subscriber));
    });
  }

  /**
   * Creates an observable for {@code event}.
   *
   * @param emitter the emitter
   * @param <T> the event type
   * @return an observable
   */
  protected <T extends E> @NonNull Observable<T> observable(final @NonNull Action1<Emitter<T>> emitter) {
    return Observable.create(emitter, Emitter.BackpressureMode.BUFFER);
  }
}
