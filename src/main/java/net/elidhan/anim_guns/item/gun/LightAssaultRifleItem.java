package net.elidhan.anim_guns.item.gun;

import net.elidhan.anim_guns.item.ModItems;
import net.elidhan.anim_guns.sound.ModSounds;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;

public class LightAssaultRifleItem extends GunTemplateItem
{
    public LightAssaultRifleItem(Settings settings)
    {
        super(settings);
    }
    @Override
    protected Item reqAmmo()
    {
        return ModItems.STANDARD_RIFLE_BULLET;
    }
    @Override
    protected float reloadCD()
    {
        return 44;
    }
    @Override
    protected int reloadStageOne()
    {
        return 6;
    }
    @Override
    protected int reloadStageTwo()
    {
        return 18;
    }
    @Override
    protected int reloadStageThree()
    {
        return 37;
    }
    @Override
    protected SoundEvent reload_p1()
    {
        return ModSounds.RELOAD_GENERIC_AR_P1;
    }
    @Override
    protected SoundEvent reload_p2()
    {
        return ModSounds.RELOAD_GENERIC_AR_P2;
    }
    @Override
    protected SoundEvent reload_p3()
    {
        return ModSounds.RELOAD_GENERIC_AR_P3;
    }
    @Override
    protected SoundEvent shootSound()
    {
        return ModSounds.ASSAULTRIFLE_LIGHT;
    }
    @Override
    protected int reloadCycles()
    {
        return 1;
    }
    @Override
    protected boolean hasScope()
    {
        return false;
    }
    @Override
    protected int useCD()
    {
        return 2;
    }
    @Override
    protected float dmg()
    {
        return 6;
    }
    @Override
    protected int rps()
    {
        return 1;
    }
    @Override
    protected int loadingType()
    {
        return 1;
    }
    @Override
    protected float spread()
    {
        return 0.25f;
    }
    @Override
    protected float recoil()
    {
        return 1.25f;
    }
    @Override
    protected int clipSize()
    {
        return 20;
    }
}