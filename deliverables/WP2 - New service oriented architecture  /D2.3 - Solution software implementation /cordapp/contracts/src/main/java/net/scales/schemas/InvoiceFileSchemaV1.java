package net.scales.schemas;

import com.google.common.collect.ImmutableList;

import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A InvoiceFileState schema
 */
public class InvoiceFileSchemaV1 extends MappedSchema {

    public InvoiceFileSchemaV1() {
        super(InvoiceFileSchema.class, 1, ImmutableList.of(PersistentInvoiceFile.class));
    }

    @Entity
    @Table(name = "INVOICE_HASHES")
    public static class PersistentInvoiceFile extends PersistentState {

        @Column(name = "ID") private final String id;
        @Column(name = "HASH") private final String hash;

        public PersistentInvoiceFile(String id, String hash) {
            this.id = id;
            this.hash = hash;
        }

        /**
         * Default constructor required by hibernate
         */
        public PersistentInvoiceFile() {
            id = null;
            hash = null;
        }

        public String getId() {
            return id;
        }

        public String getHash() {
            return hash;
        }

    }

}