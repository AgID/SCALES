package net.scales.flows.utils;

import java.util.ArrayList;
import java.util.List;

import net.corda.core.identity.Party;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.services.NetworkMapCache;

public class CommonUtils {

    /**
     * Gets all nodes that the node currently is aware of (including itself) but not include notary and sdi
     */
    public static List<Party> getAllNodes(NetworkMapCache network, Party notary, Party sdi) {
        List<Party> parties = new ArrayList<>();

        List<NodeInfo> nodes = network.getAllNodes();

        for (NodeInfo node : nodes) {
            Party party = node.getLegalIdentities().get(0);

            if (party.equals(notary) || party.equals(sdi)) {
                continue;
            }
            parties.add(party);
        }

        return parties;
    }

}