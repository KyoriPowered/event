package net.kyori.event.method.executor;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

/**
 * An executor factory which uses {@link MethodHandle}s to create event executors.
 *
 * @param <E> the event type
 * @param <L> the listener type
 */
public final class MethodHandleEventExecutorFactory<E, L> implements EventExecutor.Factory<E, L> {
  @Override
  public @NonNull EventExecutor<E, L> create(final @NonNull Object object, final @NonNull Method method) throws Exception {
    MethodHandle methodHandle = MethodHandles.publicLookup()
      .unreflect(method)
      .bindTo(object);

    return (listener, event) -> methodHandle.invokeWithArguments(event);
  }
}
