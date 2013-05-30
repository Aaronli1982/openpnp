package org.openpnp.gui.importer;

import java.awt.Frame;

public abstract class AbstractBoardImporter implements BoardImporter {
    protected Frame owner;
    
    @Override
    public void setOwner(Frame owner) {
        this.owner = owner;
    }
}
