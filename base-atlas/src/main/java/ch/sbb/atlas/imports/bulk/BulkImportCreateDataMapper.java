package ch.sbb.atlas.imports.bulk;

public abstract class BulkImportCreateDataMapper<T, V> extends BulkImportDataMapper {

    public V applyCreate(BulkImportUpdateContainer<T> container, V targetModel) {
        applyDefaultMapping(container.getObject(), targetModel);
        applySpecificCreate(container.getObject(), targetModel);

        return targetModel;
    }

    protected void applySpecificCreate(T create, V targetModel) {
        // Override if needed
    }

}
