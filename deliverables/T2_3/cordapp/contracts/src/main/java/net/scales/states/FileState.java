package net.scales.states;

import net.scales.contracts.FileContract;
import net.scales.schemas.FileSchemaV1;

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

@BelongsToContract(FileContract.class)
public class FileState extends EvolvableTokenType implements QueryableState {

    private final UniqueIdentifier linearId;
    private final List<Party> maintainers;
    private final Party issuer;
    private final int fractionDigits = 0;

    private final String id;
    private final String hash;

    public FileState(UniqueIdentifier linearId, List<Party> maintainers, Party issuer, String id, String hash) {
        this.linearId = linearId;
        this.maintainers = maintainers;
        this.issuer = issuer;

        this.id = id;
        this.hash = hash;
    }

    @Override
    public PersistentState generateMappedObject(MappedSchema schema) {
        if (schema instanceof FileSchemaV1) {
            return new FileSchemaV1.PersistentFile(this.id, this.hash);
        }

        throw new IllegalArgumentException("Unrecognised schema $schema");
    }

    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return ImmutableList.of(new FileSchemaV1());
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

    public TokenPointer<FileState> toPointer() {
        final LinearPointer<FileState> linearPointer = new LinearPointer<>(linearId, FileState.class);
        return new TokenPointer<>(linearPointer, fractionDigits);
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