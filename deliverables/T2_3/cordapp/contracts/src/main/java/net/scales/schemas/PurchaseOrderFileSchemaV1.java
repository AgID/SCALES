package net.scales.schemas;

import com.google.common.collect.ImmutableList;

import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A PurchaseOrderFileState schema
 */
public class PurchaseOrderFileSchemaV1 extends MappedSchema {

    public PurchaseOrderFileSchemaV1() {
        super(PurchaseOrderFileSchema.class, 1, ImmutableList.of(PersistentPurchaseOrderFile.class));
    }

    @Entity
    @Table(name = "PURCHASE_ORDER_HASHES")
    public static class PersistentPurchaseOrderFile extends PersistentState {

        @Column(name = "ID") private final String id;
        @Column(name = "HASH") private final String hash;

        public PersistentPurchaseOrderFile(String id, String hash) {
            this.id = id;
            this.hash = hash;
        }

        /**
         * Default constructor required by hibernate
         */
        public PersistentPurchaseOrderFile() {
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