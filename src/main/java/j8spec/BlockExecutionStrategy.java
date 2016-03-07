package j8spec;

import static j8spec.BlockExecutionFlag.FOCUSED;
import static j8spec.BlockExecutionFlag.IGNORED;

@FunctionalInterface
interface BlockExecutionStrategy {

    BlockExecutionStrategy BLACK_LIST = (blockFlag, containerFlag) ->
        IGNORED.equals(blockFlag) || IGNORED.equals(containerFlag);

    BlockExecutionStrategy WHITE_LIST = (blockFlag, containerFlag) ->
        !FOCUSED.equals(blockFlag) && !FOCUSED.equals(containerFlag);

    boolean shouldBeIgnored(BlockExecutionFlag blockFlag, BlockExecutionFlag containerFlag);
}
