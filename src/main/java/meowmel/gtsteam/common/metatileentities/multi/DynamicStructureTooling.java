package meowmel.gtsteam.common.metatileentities.multi;

import gregtech.api.pattern.MultiblockShapeInfo;
import gregtech.api.pattern.PieceRuntimeState;
import gregtech.api.pattern.StructureElementPreviewEntry;
import gregtech.api.pattern.StructureOperationRequest;
import gregtech.api.pattern.StructurePreviewResult;
import gregtech.api.pattern.StructureRuntime;

import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class DynamicStructureTooling {

    private DynamicStructureTooling() {}

    public static int resolveChannel(@Nullable Map<String, Integer> channelValues,
                                     @NotNull String channelName,
                                     int defaultValue,
                                     int minValue,
                                     int maxValue) {
        int value = defaultValue;
        if (channelValues != null) {
            Integer requested = channelValues.get(channelName);
            if (requested != null && requested > 0) {
                value = requested;
            }
        }
        return Math.max(minValue, Math.min(maxValue, value));
    }

    public static int resolveOddChannel(@Nullable Map<String, Integer> channelValues,
                                        @NotNull String channelName,
                                        int defaultValue,
                                        int minValue,
                                        int maxValue) {
        int value = resolveChannel(channelValues, channelName, defaultValue, minValue, maxValue);
        if (value % 2 == 0) {
            value--;
        }
        return Math.max(minValue, value);
    }

    @NotNull
    public static int[] unitRepetitions(int aisleCount) {
        int[] repetitions = new int[Math.max(1, aisleCount)];
        Arrays.fill(repetitions, 1);
        return repetitions;
    }

    @NotNull
    public static MultiblockShapeInfo previewShape(@NotNull StructureRuntime runtime,
                                                   int aisleCount,
                                                   @Nullable Map<String, Integer> channelValues) {
        return new MultiblockShapeInfo(runtime.previewSingle(
                StructureOperationRequest.preview(unitRepetitions(aisleCount), channelValues)));
    }

    @NotNull
    public static Map<BlockPos, StructureElementPreviewEntry> buildPreviewEntries(
            @NotNull StructureRuntime runtime,
            int aisleCount,
            @Nullable Map<String, Integer> channelValues) {
        StructurePreviewResult result = runtime.previewSingleResult(
                StructureOperationRequest.preview(unitRepetitions(aisleCount), channelValues));
        PieceRuntimeState.PreviewCells cells = result.getSinglePieceCells();
        if (cells == null || cells.getPreviewEntries().isEmpty()) {
            return Collections.emptyMap();
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        for (BlockPos pos : cells.getBlocks().keySet()) {
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
        }

        Map<BlockPos, StructureElementPreviewEntry> normalized = new HashMap<>();
        for (Map.Entry<BlockPos, StructureElementPreviewEntry> entry : cells.getPreviewEntries().entrySet()) {
            BlockPos pos = entry.getKey();
            normalized.put(
                    new BlockPos(pos.getX() - minX, pos.getY() - minY, pos.getZ() - minZ),
                    entry.getValue());
        }
        return normalized;
    }
}
