/*
 * This file ("DamageSources.java") is part of the Actually Additions mod for Minecraft.
 * It is created and owned by Ellpeck and distributed
 * under the Actually Additions License to be found at
 * http://ellpeck.de/actaddlicense
 * View the source code at https://github.com/Ellpeck/ActuallyAdditions
 *
 * © 2015-2016 Ellpeck
 */

package de.ellpeck.actuallyadditions.mod.misc;

import de.ellpeck.actuallyadditions.mod.util.ModUtil;
import de.ellpeck.actuallyadditions.mod.util.StringUtil;
import de.ellpeck.actuallyadditions.mod.util.Util;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nonnull;

public class DamageSources extends DamageSource{

    public static final DamageSource DAMAGE_ATOMIC_RECONSTRUCTOR = new DamageSources("atomicReconstructor", 5).setDamageBypassesArmor();

    private final int messageCount;

    public DamageSources(String name, int messageCount){
        super(name);
        this.messageCount = messageCount;
    }


    @Override
    public ITextComponent getDeathMessage(EntityLivingBase entity){
        String locTag = "death."+ModUtil.MOD_ID+"."+this.damageType+"."+(Util.RANDOM.nextInt(this.messageCount)+1);
        return new TextComponentTranslation(String.format(locTag, entity.getName()));
    }
}
