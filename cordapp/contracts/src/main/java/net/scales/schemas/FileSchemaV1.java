package net.scales.schemas;

import com.google.common.collect.ImmutableList;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * An FileState schema.
 */
public class FileSchemaV1 extends MappedSchema {

    public FileSchemaV1() {
        super(FileSchema.class, 1, ImmutableList.of(PersistentFile.class));
    }

    @Entity
    @Table(name = "INVOICE_HASHES")
    public static class PersistentFile extends PersistentState {

        @Column(name = "INVOICE_NUMBER") private final String id;
        @Column(name = "INVOICE_HASH") private final String hash;

        public PersistentFile(String id, String hash) {
            this.id = id;
            this.hash = hash;
        }

        /**
         * Default constructor required by hibernate.
         */
        public PersistentFile() {
            this.id = null;
            this.hash = null;
        }

        public String getId() {
            return id;
        }

        public String getHash() {
            return hash;
        }

    }

}