package me.wither.simple_error_tsting;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.IceAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.earthbending.EarthArmor;
import com.projectkorra.projectkorra.util.*;
import com.projectkorra.projectkorra.waterbending.util.WaterReturn;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Snow;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class IceSkates extends IceAbility implements AddonAbility, Listener{
    private Location location;
    private long duration;
    private int speed;
    private long cooldown;
    private boolean snow_trail;
    private Listener listener;
    private Permission perm;
    private final World origin;

    public IceSkates(Player player) {
        super(player);


        origin = player.getWorld();

        setField();

        Block source = BlockSource.getWaterSourceBlock(player, 2, ClickType.SHIFT_DOWN, false, true, false, false, true);

        if(source == null){
            if(WaterReturn.hasWaterBottle(player)){
                WaterReturn.emptyWaterBottle(player);
                start();
                bPlayer.addCooldown(this);
            }
            else {
                return;
            }
        }
        if(bPlayer.isOnCooldown(this)){
            return;
        }

        start();
        bPlayer.addCooldown(this);
    }
    public void setField() {
        cooldown = ConfigManager.getConfig().getLong("ExtraAbilities.Sc1_original.IceSkates.Cooldown");
        duration = ConfigManager.getConfig().getLong("ExtraAbilities.Sc1_original.IceSkates.Duration");
        speed = ConfigManager.getConfig().getInt("ExtraAbilities.Sc1_original.IceSkates.Speed(1,2,3,,)");
        snow_trail = ConfigManager.getConfig().getBoolean("ExtraAbilities.Sc1_original.IceSkates.SnowTrail");
    }

    @Override
    public void progress() {
        location = player.getLocation();
        if(bPlayer.canIcebend() && bPlayer.canBendInWorld()){

            sking();

            if(player.getWorld() != origin){
                remove();
                return;
            }
            if(!bPlayer.canBendIgnoreBindsCooldowns(this)){
                remove();
                return;
            }

            if(bPlayer.isBloodbent() || bPlayer.isChiBlocked() || bPlayer.isParalyzed() || bPlayer.isControlledByMetalClips()
            || player.isDead() || player.isInWater() || !player.isOnline() || hasAbility(player, EarthArmor.class)){
                remove();
                return;
            }

            if(this.getStartTime() + duration <= System.currentTimeMillis()){
                player.getWorld().spawnParticle(Particle.BLOCK_CRACK, location.add(1,0,.5), 190, 0.9, 0.9, 0.9, 0, Material.ICE.createBlockData());
                player.playSound(location, Sound.BLOCK_GLASS_BREAK, 1,1);
                remove();
            }
        }
    }

    public void sking() {
        GeneralMethods.displayColoredParticle("#abd4ff", location, 20, .5, 0.02, .25);
        GeneralMethods.displayColoredParticle("#e0efff", location, 20, .8, 0.02, .5);
        if(location.getBlock().getRelative(BlockFace.SOUTH).getType() != Material.SNOW
                && location.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.SOUTH).getType() != Material.SNOW && location.getBlock().getType() != Material.DIRT_PATH
                && location.getBlock().getRelative(BlockFace.SOUTH).getType() != Material.DIRT_PATH) {

            if(player.isSprinting() && player.isOnGround()){
                player.spawnParticle(Particle.BLOCK_CRACK, location.clone(), 54, 0.5,1,0.5, 0, Material.SNOW.createBlockData());
                player.playSound(location, Sound.BLOCK_GRASS_HIT, 0.2F,2F);
                if(snow_trail){
                    new TempBlock(location.getBlock(), Material.SNOW).setRevertTime(800);
                    Snow snow = (Snow) location.getBlock().getBlockData();
                    snow.setLayers(2);
                    new TempBlock(location.getBlock(), snow).setRevertTime(500);
                }
            }

        }
        if(!this.player.hasPotionEffect(PotionEffectType.SPEED) || (this.player.getPotionEffect(PotionEffectType.SPEED)).getAmplifier() <= this.speed) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5, speed, false, false, false));
        }
    }
    @Override
    public boolean isSneakAbility() {
        return true;
    }

    @Override
    public boolean isHarmlessAbility() {
        return false;
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public String getName() {
        return "IceSkates";
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void load() {
        listener = new IceSkatesListener();
        ProjectKorra.plugin.getServer().getPluginManager().registerEvents(listener, ProjectKorra.plugin);

        perm = new Permission("bending.ability.iceskates");
        perm.setDefault(PermissionDefault.OP);
        ProjectKorra.plugin.getServer().getPluginManager().addPermission(perm);

        ConfigManager.getConfig().addDefault("ExtraAbilities.Sc1_original.IceSkates.Cooldown", 8000);
        ConfigManager.getConfig().addDefault("ExtraAbilities.Sc1_original.IceSkates.Duration", 15000);
        ConfigManager.getConfig().addDefault("ExtraAbilities.Sc1_original.IceSkates.Speed(1,2,3,,)", 3);
        ConfigManager.getConfig().addDefault("ExtraAbilities.Sc1_original.IceSkates.SnowTrail", true);
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(listener);
        ProjectKorra.plugin.getServer().getPluginManager().removePermission(perm);
    }

    @Override
    public String getAuthor() {
        return "Sc1_original -- Idea by MaanGamer20";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return "Icebenders can have a great mobility by making their source into skates to use" +
                "Ice slippery nature, and move around by the skates";
    }

    @Override
    public String getInstructions() {
        return "Hold Sneak(Default:Shift) then wait for skates to create, you need to be near Ice" +
                "Additionally; you can use the ability with WaterBottle too";
    }

}

