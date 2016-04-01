package j8spec;

import java.util.Collection;

interface BlockDefinition {

    static void visitAll(BlockDefinitionVisitor visitor, Collection<BlockDefinition> blockDefinitions) {
        for (BlockDefinition blockDefinition : blockDefinitions) {
            blockDefinition.accept(visitor);
        }
    }

    void accept(BlockDefinitionVisitor visitor);
}
