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
package net.kyori.event.rx2;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.disposables.Disposable;
import net.kyori.event.EventSubscriber;
import net.kyori.event.SimpleEventBus;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An event bus that can be used with RxJava 2.
 *
 * @param <E> the event type
 */
public class Rx2EventBus<E> extends SimpleEventBus<E> {
  /**
   * Creates a flowable for {@code event}.
   *
   * @param event the event
   * @param <T> the event type
   * @return a flowable
   */
  public <T extends E> @NonNull Flowable<T> flowable(final @NonNull Class<T> event) {
    return this.flowable(emitter -> {
      final EventSubscriber<T> subscriber = e -> {
        try {
          emitter.onNext(e);
        } catch(final Throwable t) {
          emitter.onError(t);
        }
      };
      this.register(event, subscriber);
      emitter.setDisposable(new Disposable() {
        private AtomicBoolean disposed = new AtomicBoolean();

        @Override
        public void dispose() {
          if(!this.disposed.getAndSet(true)) {
            Rx2EventBus.this.unregister(subscriber);
          }
        }

        @Override
        public boolean isDisposed() {
          return this.disposed.get();
        }
      });

    });
  }

  /**
   * Creates a flowable for {@code event}.
   *
   * @param emitter the emitter
   * @param <T> the event type
   * @return a flowable
   */
  protected <T extends E> @NonNull Flowable<T> flowable(final @NonNull FlowableOnSubscribe<T> emitter) {
    return Flowable.create(emitter, BackpressureStrategy.BUFFER);
  }
}
