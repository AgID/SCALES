package net.scales.contracts;

import com.r3.corda.lib.tokens.contracts.EvolvableTokenContract;
import net.corda.core.contracts.Contract;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

public class InvoiceFileContract extends EvolvableTokenContract implements Contract {

    @Override
    public void additionalCreateChecks(@NotNull LedgerTransaction tx) {
        // Writes contract validation logic to be performed while creation of token
    }

    @Override
    public void additionalUpdateChecks(@NotNull LedgerTransaction tx) {
        // Writes contract validation logic to be performed while updation of token
    }

}