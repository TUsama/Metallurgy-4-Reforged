package it.hurts.metallurgy_reforged.integration.mods.conarm.traits;

import c4.conarm.lib.traits.AbstractArmorTrait;
import it.hurts.metallurgy_reforged.integration.mods.conarm.MetallurgyConArmorStats;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.FoodStats;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import scala.util.Random;

public class TraitFoodly extends AbstractArmorTrait{

	public TraitFoodly() {
		super("foodly", TextFormatting.DARK_RED);
	}
	
	@SubscribeEvent
	public void onArmorTick(PlayerTickEvent event){	
		if(MetallurgyConArmorStats.isThatArmorTrait(event.player, "foodly")) {
			FoodStats foodStat = event.player.getFoodStats();
			int amount = 2;						
			//quantity experience to remove
			float removeTot = (float)amount / (float)event.player.xpBarCap();
			//check if the player needs food ,if he has enough experience and if the tick is a multiple of 20 (which means that the effect will be applied every second)
			if(event.player instanceof EntityPlayerMP && event.player.canEat(false) && 
					(event.player.experience >= removeTot || event.player.experienceLevel > 0) && 
					event.player.ticksExisted % 20 == 0)
			{
				EntityPlayerMP mp = (EntityPlayerMP) event.player;
				Random rand = new Random();				
				mp.experience -= removeTot;

				if(mp.experienceTotal - amount >= 0)
					mp.experienceTotal -= amount;

				if(mp.experience < 0.0F)
				{
					mp.experience = 1F - mp.experience;
					mp.addExperienceLevel(-1);
				}

				//add Food Level
				foodStat.addStats(1, 0.5F);
				//update experience count on the client side
				mp.connection.sendPacket(new SPacketSetExperience(mp.experience, mp.experienceTotal, mp.experienceLevel));
				//play generic eat sound
				mp.connection.sendPacket(new SPacketSoundEffect(SoundEvents.ENTITY_GENERIC_EAT,SoundCategory.PLAYERS,mp.posX,mp.posY + mp.getEyeHeight(),mp.posZ, 0.3F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F));

			}
		}
	}


}
