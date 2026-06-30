package meowmel.gtsteam.api.pattern;

import gregtech.api.block.VariantActiveBlock;
import gregtech.api.pattern.StructureEvaluationContext;
import gregtech.api.pattern.StructureSessionKey;
import gregtech.api.pattern.element.IStructureElement;
import gregtech.api.pattern.element.ITypedStructureElement;
import gregtech.api.pattern.element.StructureElementPreview;
import gregtech.api.util.BlockInfo;
import net.minecraft.block.state.IBlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class TraceabilityPredicate {

    private static final StructureSessionKey<Map<String, Boolean>> OPTIONAL_STATE_MARKS =
            StructureSessionKey.copying("gtsteam:optional_states", value -> new HashMap<>(value));

    public static IStructureElement optionalStates(String mark, IBlockState... allowedStates) {
        Set<IBlockState> allowed = new HashSet<>(Arrays.asList(allowedStates));
        Supplier<BlockInfo[]> candidates = getCandidates(allowedStates);
        return new ITypedStructureElement<Object>() {
            @Override
            public boolean check(@NotNull StructureEvaluationContext<Object> context) {
                IBlockState state = context.getBlockState();
                if (state.getBlock() instanceof VariantActiveBlock) {
                    context.getCollector().recordVariantActiveBlock(context.getPos());
                }
                boolean matches = allowed.contains(state);
                if (context.getSession() == null) {
                    return matches;
                }
                Map<String, Boolean> marks = context.getSession()
                        .getOrCreate(OPTIONAL_STATE_MARKS, HashMap::new);
                if (matches) {
                    marks.put(mark, true);
                    return true;
                }
                return !Boolean.TRUE.equals(marks.get(mark));
            }

            @Override
            public BlockInfo[] getCandidates() {
                return candidates.get();
            }

            @NotNull
            @Override
            public StructureElementPreview getPreview() {
                return StructureElementPreview.of(candidates);
            }
        };
    }

    public static Supplier<BlockInfo[]> getCandidates(IBlockState... allowedStates) {
        return () -> Arrays.stream(allowedStates).map(state -> new BlockInfo(state, null)).toArray(BlockInfo[]::new);
    }
}
