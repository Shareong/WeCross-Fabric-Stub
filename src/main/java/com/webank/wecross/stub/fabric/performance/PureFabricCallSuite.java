package com.webank.wecross.stub.fabric.performance;

import com.webank.wecross.stub.fabric.EndorsementPolicyAnalyzer;
import com.webank.wecross.stub.fabric.FabricConnection;
import com.webank.wecross.stub.fabric.FabricConnectionFactory;
import java.util.Collection;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;

public class PureFabricCallSuite implements PerformanceSuite {
    private Channel channel;
    private HFClient hfClient;

    public PureFabricCallSuite(String chainPath) throws Exception {
        FabricConnection fabricConnection = FabricConnectionFactory.build(chainPath);
        fabricConnection.start();

        this.channel = fabricConnection.getChannel();

        if (!fabricConnection.getChaincodeMap().containsKey("abac")) {
            throw new Exception(
                    "Resource abac has not been config, please check chains/fabric/stub.toml");
        }

        this.hfClient = fabricConnection.getChaincodeMap().get("abac").getHfClient();

        queryOnce();
    }

    @Override
    public String getName() {
        return "Pure Fabric Call Suite";
    }

    @Override
    public void call(PerformanceSuiteCallback callback) {
        try {
            Collection<ProposalResponse> responses = queryOnce();
            EndorsementPolicyAnalyzer analyzer = new EndorsementPolicyAnalyzer(responses);

            if (analyzer.allSuccess()) {
                callback.onSuccess("Success");
            } else {
                callback.onFailed("Failed");
            }

        } catch (Exception e) {
            callback.onFailed("mycc query failed: " + e);
        }
    }

    private Collection<ProposalResponse> queryOnce() throws Exception {
        QueryByChaincodeRequest request = hfClient.newQueryProposalRequest();
        String cc = "mycc";
        ChaincodeID ccid = ChaincodeID.newBuilder().setName(cc).build();
        request.setChaincodeID(ccid);
        request.setFcn("query");
        request.setArgs("a");
        request.setProposalWaitTime(3000);

        Collection<ProposalResponse> responses = channel.queryByChaincode(request);
        return responses;
    }
}
