package net.scales.contracts;

import com.r3.corda.lib.tokens.contracts.EvolvableTokenContract;
import net.scales.states.FileState;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

public class FileContract extends EvolvableTokenContract implements Contract {

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        FileState outputState = (FileState) tx.getOutput(0);

        if (!(tx.getCommand(0).getSigners().contains(outputState.getIssuer().getOwningKey()))) {
            throw new IllegalArgumentException("Issuer signature is required");
        }
    }

    @Override
    public void additionalCreateChecks(@NotNull LedgerTransaction tx) {
        // Write contract validation logic to be performed while creation of token
    }

    @Override
    public void additionalUpdateChecks(@NotNull LedgerTransaction tx) {
        // Write contract validation logic to be performed while updation of token
    }

}