//MIT License
//Author iristhepianist
//https://github.com/iristhepianist/ScalableStorageCEu/
package meowmel.gtsteam.common.item.storageupdate;

import meowmel.gtsteam.GTSteam;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemVoidUpgrade extends Item {

    public ItemVoidUpgrade(String name) {
        setRegistryName(new ResourceLocation(GTSteam.MODID, name));
        setTranslationKey(name);
        setCreativeTab(CreativeTabs.MISC);
        setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, @NotNull List<String> tooltip, ITooltipFlag flag) {
        tooltip.add(TextFormatting.GRAY + I18n.format("gtsteam.void_upgrade.tooltip"));
    }
}
