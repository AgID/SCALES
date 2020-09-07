package net.scales.states;

import net.scales.contracts.InvoiceFileContract;
import net.scales.schemas.InvoiceFileSchemaV1;

import com.google.common.collect.ImmutableList;
import com.r3.corda.lib.tokens.contracts.states.EvolvableTokenType;
import com.r3.corda.lib.tokens.contracts.types.TokenPointer;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.LinearPointer;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@BelongsToContract(InvoiceFileContract.class)
public class InvoiceFileState extends EvolvableTokenType implements QueryableState {

    private final UniqueIdentifier linearId;
    private final List<Party> maintainers;
    private final Party issuer;
    private final int fractionDigits = 0;

    private final String id;
    private final String hash;

    public InvoiceFileState(UniqueIdentifier linearId, List<Party> maintainers, Party issuer, String id, String hash) {
        this.linearId = linearId;
        this.maintainers = maintainers;
        this.issuer = issuer;

        this.id = id;
        this.hash = hash;
    }

    @Override
    public PersistentState generateMappedObject(MappedSchema schema) {
        if (schema instanceof InvoiceFileSchemaV1) {
            return new InvoiceFileSchemaV1.PersistentInvoiceFile(id, hash);
        }

        throw new IllegalArgumentException("Unrecognized schema $schema");
    }

    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return ImmutableList.of(new InvoiceFileSchemaV1());
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    @Override
    public int getFractionDigits() {
        return fractionDigits;
    }

    @NotNull
    @Override
    public List<Party> getMaintainers() {
        return ImmutableList.copyOf(maintainers);
    }

    public TokenPointer<InvoiceFileState> toPointer() {
        return new TokenPointer<>(new LinearPointer<>(linearId, InvoiceFileState.class), fractionDigits);
    }

    public Party getIssuer() {
        return issuer;
    }

    public String getId() {
        return id;
    }

    public String getHash() {
        return hash;
    }

}