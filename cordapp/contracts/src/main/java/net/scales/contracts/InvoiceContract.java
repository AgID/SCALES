package net.scales.contracts;

import com.r3.corda.lib.tokens.contracts.EvolvableTokenContract;
import net.scales.states.InvoiceState;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

public class InvoiceContract extends EvolvableTokenContract implements Contract {

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        InvoiceState outputState = (InvoiceState) tx.getOutput(0);

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