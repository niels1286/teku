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

package tech.pegasys.teku.beaconrestapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.eventbus.EventBus;
import io.javalin.Javalin;
import io.javalin.core.JavalinServer;
import io.javalin.http.Handler;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tech.pegasys.teku.api.DataProvider;
import tech.pegasys.teku.beaconrestapi.handlers.v1.beacon.GetBlockHeader;
import tech.pegasys.teku.beaconrestapi.handlers.v1.beacon.GetGenesis;
import tech.pegasys.teku.beaconrestapi.handlers.v1.beacon.GetStateEpochCommittees;
import tech.pegasys.teku.beaconrestapi.handlers.v1.beacon.GetStateFinalityCheckpoints;
import tech.pegasys.teku.beaconrestapi.handlers.v1.beacon.GetStateFork;
import tech.pegasys.teku.beaconrestapi.handlers.v1.beacon.GetStateRoot;
import tech.pegasys.teku.beaconrestapi.handlers.v1.beacon.GetStateValidator;
import tech.pegasys.teku.beaconrestapi.handlers.v1.beacon.GetStateValidatorBalances;
import tech.pegasys.teku.beaconrestapi.handlers.v1.beacon.GetStateValidators;
import tech.pegasys.teku.beaconrestapi.handlers.v1.beacon.PostBlock;
import tech.pegasys.teku.beaconrestapi.handlers.v1.events.GetEvents;
import tech.pegasys.teku.beaconrestapi.handlers.v1.node.GetHealth;
import tech.pegasys.teku.beaconrestapi.handlers.v1.node.GetIdentity;
import tech.pegasys.teku.beaconrestapi.handlers.v1.node.GetPeerById;
import tech.pegasys.teku.beaconrestapi.handlers.v1.node.GetPeers;
import tech.pegasys.teku.beaconrestapi.handlers.v1.node.GetSyncing;
import tech.pegasys.teku.beaconrestapi.handlers.v1.node.GetVersion;
import tech.pegasys.teku.beaconrestapi.handlers.v1.validator.GetAggregateAttestation;
import tech.pegasys.teku.beaconrestapi.handlers.v1.validator.GetAttestationData;
import tech.pegasys.teku.beaconrestapi.handlers.v1.validator.GetAttesterDuties;
import tech.pegasys.teku.beaconrestapi.handlers.v1.validator.GetNewBlock;
import tech.pegasys.teku.beaconrestapi.handlers.v1.validator.GetProposerDuties;
import tech.pegasys.teku.beaconrestapi.handlers.v1.validator.PostAggregateAndProofs;
import tech.pegasys.teku.beaconrestapi.handlers.v1.validator.PostAttesterDuties;
import tech.pegasys.teku.beaconrestapi.handlers.v1.validator.PostSubscribeToBeaconCommitteeSubnet;
import tech.pegasys.teku.infrastructure.async.StubAsyncRunner;
import tech.pegasys.teku.infrastructure.events.EventChannels;
import tech.pegasys.teku.statetransition.attestation.AggregatingAttestationPool;
import tech.pegasys.teku.storage.client.CombinedChainDataClient;
import tech.pegasys.teku.storage.client.MemoryOnlyRecentChainData;
import tech.pegasys.teku.storage.client.RecentChainData;
import tech.pegasys.teku.sync.SyncService;
import tech.pegasys.teku.util.config.GlobalConfiguration;

public class BeaconRestApiV1Test {
  private final RecentChainData storageClient = MemoryOnlyRecentChainData.create(new EventBus());
  private final CombinedChainDataClient combinedChainDataClient =
      mock(CombinedChainDataClient.class);
  private final JavalinServer server = mock(JavalinServer.class);
  private final Javalin app = mock(Javalin.class);
  private final SyncService syncService = mock(SyncService.class);
  private final EventChannels eventChannels = mock(EventChannels.class);
  private static final Integer THE_PORT = 12345;
  private final AggregatingAttestationPool attestationPool = mock(AggregatingAttestationPool.class);

  @BeforeEach
  public void setup() {
    GlobalConfiguration config =
        GlobalConfiguration.builder().setRestApiPort(THE_PORT).setRestApiDocsEnabled(false).build();
    when(app.server()).thenReturn(server);
    new BeaconRestApi(
        new DataProvider(
            storageClient, combinedChainDataClient, null, syncService, null, attestationPool),
        config,
        eventChannels,
        new StubAsyncRunner(),
        app);
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("getParameters")
  void getRouteExists(final String route, final Class<Handler> type) {
    verify(app).get(eq(route), any(type));
  }

  static Stream<Arguments> getParameters() {
    Stream.Builder<Arguments> builder = Stream.builder();

    // beacon
    builder
        .add(Arguments.of(GetBlockHeader.ROUTE, GetBlockHeader.class))
        .add(Arguments.of(GetGenesis.ROUTE, GetGenesis.class))
        .add(Arguments.of(GetStateFork.ROUTE, GetStateFork.class))
        .add(Arguments.of(GetStateRoot.ROUTE, GetStateRoot.class))
        .add(Arguments.of(GetStateValidator.ROUTE, GetStateValidator.class))
        .add(Arguments.of(GetStateValidators.ROUTE, GetStateValidators.class))
        .add(Arguments.of(GetStateFinalityCheckpoints.ROUTE, GetStateFinalityCheckpoints.class))
        .add(Arguments.of(GetStateValidatorBalances.ROUTE, GetStateValidatorBalances.class))
        .add(
            Arguments.of(
                GetStateEpochCommittees.ROUTE_WITH_EPOCH_PARAM, GetStateEpochCommittees.class))
        .add(
            Arguments.of(
                GetStateEpochCommittees.ROUTE_WITHOUT_EPOCH_PARAM, GetStateEpochCommittees.class));

    // events
    builder.add(Arguments.of(GetEvents.ROUTE, GetEvents.class));

    // node
    builder
        .add(Arguments.of(GetHealth.ROUTE, GetHealth.class))
        .add(Arguments.of(GetIdentity.ROUTE, GetIdentity.class))
        .add(Arguments.of(GetPeerById.ROUTE, GetPeerById.class))
        .add(Arguments.of(GetPeers.ROUTE, GetPeers.class))
        .add(Arguments.of(GetSyncing.ROUTE, GetSyncing.class))
        .add(Arguments.of(GetVersion.ROUTE, GetVersion.class));

    // validator
    builder
        .add(Arguments.of(GetAggregateAttestation.ROUTE, GetAggregateAttestation.class))
        .add(Arguments.of(GetAttestationData.ROUTE, GetAttestationData.class))
        .add(Arguments.of(GetAttesterDuties.ROUTE, GetAttesterDuties.class))
        .add(Arguments.of(GetNewBlock.ROUTE, GetNewBlock.class))
        .add(Arguments.of(GetProposerDuties.ROUTE, GetProposerDuties.class));

    return builder.build();
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("postParameters")
  void postRouteExists(final String route, final Class<Handler> type) {
    verify(app).post(eq(route), any(type));
  }

  static Stream<Arguments> postParameters() {
    Stream.Builder<Arguments> builder = Stream.builder();

    // beacon
    builder
        .add(Arguments.of(PostAttesterDuties.ROUTE, PostAttesterDuties.class))
        .add(Arguments.of(PostBlock.ROUTE, PostBlock.class));

    // validator
    builder
        .add(Arguments.of(PostAggregateAndProofs.ROUTE, PostAggregateAndProofs.class))
        .add(Arguments.of(PostAttesterDuties.ROUTE, PostAttesterDuties.class))
        .add(
            Arguments.of(
                PostSubscribeToBeaconCommitteeSubnet.ROUTE,
                PostSubscribeToBeaconCommitteeSubnet.class));

    return builder.build();
  }
}