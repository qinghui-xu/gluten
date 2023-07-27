/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.glutenproject.memory.alloc;

/**
 * This along with {@link NativeMemoryAllocators}, as built-in toolkit for managing native memory
 * allocations.
 */
public class NativeMemoryAllocator {
  enum Type {
    DEFAULT,
  }

  private final long nativeInstanceId;
  private final ReservationListener listener;

  public NativeMemoryAllocator(long nativeInstanceId, ReservationListener listener) {
    this.nativeInstanceId = nativeInstanceId;
    this.listener = listener;
  }

  public static NativeMemoryAllocator create(Type type) {
    return new NativeMemoryAllocator(getAllocator(type.name()), ReservationListener.NOOP);
  }

  public static NativeMemoryAllocator createListenable(
      ReservationListener listener, NativeMemoryAllocator delegated) {
    return new NativeMemoryAllocator(
        createListenableAllocator(listener, delegated.nativeInstanceId), listener);
  }

  public ReservationListener listener() {
    return listener;
  }

  public long getNativeInstanceId() {
    return this.nativeInstanceId;
  }

  public long getBytesAllocated() {
    return bytesAllocated(this.nativeInstanceId);
  }

  public void close() {
    releaseAllocator(this.nativeInstanceId);
  }

  private static native long getAllocator(String typeName);

  private static native long createListenableAllocator(
      ReservationListener listener, long delegatedAllocatorId);

  private static native void releaseAllocator(long allocatorId);

  private static native long bytesAllocated(long allocatorId);
}