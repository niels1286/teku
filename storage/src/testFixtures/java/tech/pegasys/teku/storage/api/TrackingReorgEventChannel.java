/*
 * Copyright 2020 ConsenSys AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package tech.pegasys.teku.storage.api;

import com.google.common.base.MoreObjects;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.tuweni.bytes.Bytes32;
import tech.pegasys.teku.infrastructure.unsigned.UInt64;

public class TrackingReorgEventChannel implements ReorgEventChannel {
  private final List<ReorgEvent> reorgEvents = new ArrayList<>();

  @Override
  public void reorgOccurred(
      final Bytes32 bestBlockRoot, final UInt64 bestSlot, final UInt64 commonAncestorSlot) {
    reorgEvents.add(new ReorgEvent(bestBlockRoot, bestSlot, commonAncestorSlot));
  }

  public List<ReorgEvent> getReorgEvents() {
    return reorgEvents;
  }

  public static class ReorgEvent {
    private final Bytes32 bestBlockRoot;
    private final UInt64 bestSlot;
    private final UInt64 commonAncestorSlot;

    public ReorgEvent(
        final Bytes32 bestBlockRoot, final UInt64 bestSlot, final UInt64 commonAncestorSlot) {
      this.bestBlockRoot = bestBlockRoot;
      this.bestSlot = bestSlot;
      this.commonAncestorSlot = commonAncestorSlot;
    }

    public Bytes32 getBestBlockRoot() {
      return bestBlockRoot;
    }

    public UInt64 getBestSlot() {
      return bestSlot;
    }

    public UInt64 getCommonAncestorSlot() {
      return commonAncestorSlot;
    }

    @Override
    public boolean equals(final Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      final ReorgEvent that = (ReorgEvent) o;
      return Objects.equals(bestBlockRoot, that.bestBlockRoot)
          && Objects.equals(bestSlot, that.bestSlot)
          && Objects.equals(commonAncestorSlot, that.commonAncestorSlot);
    }

    @Override
    public int hashCode() {
      return Objects.hash(bestBlockRoot, bestSlot, commonAncestorSlot);
    }

    @Override
    public String toString() {
      return MoreObjects.toStringHelper(this)
          .add("bestBlockRoot", bestBlockRoot)
          .add("bestSlot", bestSlot)
          .add("commonAncestorSlot", commonAncestorSlot)
          .toString();
    }
  }
}