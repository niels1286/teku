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

package tech.pegasys.teku.validator.remote.apiclient;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.apache.tuweni.bytes.Bytes32;
import tech.pegasys.teku.api.response.GetForkResponse;
import tech.pegasys.teku.api.schema.Attestation;
import tech.pegasys.teku.api.schema.BLSSignature;
import tech.pegasys.teku.api.schema.BeaconBlock;
import tech.pegasys.teku.api.schema.SignedAggregateAndProof;
import tech.pegasys.teku.api.schema.SignedBeaconBlock;
import tech.pegasys.teku.api.schema.SubnetSubscription;
import tech.pegasys.teku.api.schema.ValidatorDuties;
import tech.pegasys.teku.api.schema.ValidatorDutiesRequest;
import tech.pegasys.teku.infrastructure.unsigned.UInt64;

public interface ValidatorRestApiClient {

  Optional<GetForkResponse> getFork();

  List<ValidatorDuties> getDuties(ValidatorDutiesRequest request);

  Optional<BeaconBlock> createUnsignedBlock(
      UInt64 slot, BLSSignature randaoReveal, Optional<Bytes32> graffiti);

  void sendSignedBlock(SignedBeaconBlock beaconBlock);

  Optional<Attestation> createUnsignedAttestation(UInt64 slot, int committeeIndex);

  void sendSignedAttestation(Attestation attestation);

  Optional<Attestation> createAggregate(Bytes32 attestationHashTreeRoot);

  void sendAggregateAndProof(SignedAggregateAndProof signedAggregateAndProof);

  void subscribeToBeaconCommitteeForAggregation(int committeeIndex, UInt64 aggregationSlot);

  void subscribeToPersistentSubnets(Set<SubnetSubscription> subnetSubscriptions);
}