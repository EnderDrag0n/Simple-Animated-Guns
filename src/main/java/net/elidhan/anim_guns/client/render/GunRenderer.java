package net.elidhan.anim_guns.client.render;

import mod.azure.azurelib.cache.AzureLibCache;
import mod.azure.azurelib.cache.object.BakedGeoModel;
import mod.azure.azurelib.cache.object.GeoBone;
import mod.azure.azurelib.renderer.GeoItemRenderer;
import mod.azure.azurelib.renderer.GeoRenderer;
import mod.azure.azurelib.util.RenderUtils;
import net.elidhan.anim_guns.AnimatedGuns;
import net.elidhan.anim_guns.client.AttachmentRenderType;
import net.elidhan.anim_guns.client.MuzzleFlashRenderType;
import net.elidhan.anim_guns.client.model.GunModel;
import net.elidhan.anim_guns.item.GunItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Objects;


public class GunRenderer extends GeoItemRenderer<GunItem> implements GeoRenderer<GunItem>
{
    public GunRenderer(Identifier identifier)
    {
        super(new GunModel(identifier));
    }
    private VertexConsumerProvider bufferSource;
    private ModelTransformationMode renderType;
    //use getCurrentItemStack() to get the itemstack and the NBT data for the attachments

    float sightDefaultHeight = 0.0f;
    float sightAdjust = 0.0f;

    @Override
    protected void renderInGui(ModelTransformationMode transformType, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight, int packedOverlay)
    {}

    @Override
    public void render(ItemStack stack, ModelTransformationMode transformType, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight, int packedOverlay)
    {
        this.bufferSource = bufferSource;
        this.renderType = transformType;

        if(this.renderType == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND) super.render(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }

    @Override
    public void renderRecursively(MatrixStack poseStack, GunItem animatable, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        MinecraftClient client = MinecraftClient.getInstance();

        boolean renderArms = false;
        BakedGeoModel attachmentModel;
        GeoBone attachmentBone;
        VertexConsumer buffer1 = this.bufferSource.getBuffer(renderType);

        if (this.renderType == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND)
        {
            poseStack.push();

            //Attachments test
            int sightID = this.getAnimatable().getSightID(this.getCurrentItemStack());
            int gripID = this.getAnimatable().getGripID(this.getCurrentItemStack());
            int muzzleID = this.getAnimatable().getMuzzleID(this.getCurrentItemStack());

            switch(bone.getName())
            {
                case "leftArm", "rightArm" ->
                {
                    bone.setHidden(true);
                    bone.setChildrenHidden(false);
                    renderArms = true;
                }
                case "gunbody", "magazine2" ->
                {
                    if (sightID > 0) // && this.getCurrentItemStack().getOrCreateNbt().getBoolean("isAiming")
                    {
                        poseStack.translate(0,sightAdjust/16,0);
                    }
                }
                case "muzzleflash" ->
                {
                    buffer1 = this.bufferSource.getBuffer(MuzzleFlashRenderType.getMuzzleFlash());

                    if (sightID > 0) //  && this.getCurrentItemStack().getOrCreateNbt().getBoolean("isAiming")
                    {
                        poseStack.translate(0,sightAdjust/16,0);
                    }
                }
                case "sight_default" ->
                {
                    sightDefaultHeight = bone.getPivotY();
                    bone.setHidden(sightID > 0);
                }
                case "sightPos" ->
                {
                    buffer1 = this.bufferSource.getBuffer(AttachmentRenderType.getAttachment(1, sightID));
                    if (sightID > 0)
                    {
                        attachmentModel = AzureLibCache.getBakedModels().get(new Identifier(AnimatedGuns.MOD_ID, "geo/attach_si_"+sightID+".geo.json"));

                        attachmentBone = attachmentModel.getBone("sight").orElse(null);
                        GeoBone reticle = attachmentModel.getBone("reticlePos").orElse(null);

                        if(attachmentBone != null)
                        {
                            //position the attachment to where  attachment bone is on model file
                            //bone doesn't have any cubes = no positional values
                            //luckily, pivot values still exist

                            sightAdjust = sightDefaultHeight - bone.getPivotY() - attachmentModel.getBone("reticlePos").get().getPivotY();

                            poseStack.translate(bone.getPivotX()/16, bone.getPivotY()/16, bone.getPivotZ()/16);
                            super.renderCubesOfBone(poseStack, attachmentBone, buffer, packedLight, packedOverlay, red, green, blue, alpha);

                            if (reticle != null)
                                super.renderCubesOfBone(poseStack, reticle, this.bufferSource.getBuffer(AttachmentRenderType.getReticle()), packedLight, packedOverlay, red, green, blue, alpha);
                        }
                    }
                }
                case "gripPos" ->
                {
                    buffer1 = this.bufferSource.getBuffer(AttachmentRenderType.getAttachment(2, gripID));
                    if (gripID > 0)
                    {
                        attachmentModel = AzureLibCache.getBakedModels().get(new Identifier(AnimatedGuns.MOD_ID, "geo/attach_gr_"+gripID+".geo.json"));

                        attachmentBone = attachmentModel.getBone("grip").orElse(null);

                        if(attachmentBone != null)
                        {
                            poseStack.translate(bone.getPivotX()/16, bone.getPivotY()/16, bone.getPivotZ()/16);
                            super.renderCubesOfBone(poseStack, attachmentBone, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                        }
                    }
                }
                case "muzzlePos" ->
                {
                    buffer1 = this.bufferSource.getBuffer(AttachmentRenderType.getAttachment(3, muzzleID));
                    if (muzzleID > 0)
                    {
                        attachmentModel = AzureLibCache.getBakedModels().get(new Identifier(AnimatedGuns.MOD_ID, "geo/attach_mz_"+muzzleID+".geo.json"));

                        attachmentBone = attachmentModel.getBone("muzzle").orElse(null);

                        if(attachmentBone != null)
                        {
                            poseStack.translate(bone.getPivotX()/16, bone.getPivotY()/16, bone.getPivotZ()/16);
                            super.renderCubesOfBone(poseStack, attachmentBone, buffer, packedLight, packedOverlay, red, green, blue, alpha);
                        }
                    }
                }
            }

            if (renderArms)
            {
                //I just want the arms to show, why do we have to suffer just to get opposable thumbs
                PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer) client.getEntityRenderDispatcher().getRenderer(client.player);
                PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = playerEntityRenderer.getModel();

                RenderUtils.translateMatrixToBone(poseStack, bone);
                RenderUtils.translateToPivotPoint(poseStack, bone);
                RenderUtils.rotateMatrixAroundBone(poseStack, bone);
                RenderUtils.scaleMatrixForBone(poseStack, bone);
                RenderUtils.translateAwayFromPivotPoint(poseStack, bone);

                Identifier playerSkin = client.player.getSkinTexture();
                VertexConsumer arm = this.bufferSource.getBuffer(RenderLayer.getEntitySolid(playerSkin));
                VertexConsumer sleeve = this.bufferSource.getBuffer(RenderLayer.getEntityTranslucent(playerSkin));

                if (bone.getName().equals("leftArm"))
                {
                    poseStack.scale(0.67f, 1.33f, 0.67f);
                    poseStack.translate(-0.25, -0.43625, 0.1625);
                    playerEntityModel.leftArm.setPivot(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
                    playerEntityModel.leftArm.setAngles(0, 0, 0);
                    playerEntityModel.leftArm.render(poseStack, arm, packedLight, packedOverlay, 1, 1, 1, 1);

                    playerEntityModel.leftSleeve.setPivot(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
                    playerEntityModel.leftSleeve.setAngles(0, 0, 0);
                    playerEntityModel.leftSleeve.render(poseStack, sleeve, packedLight, packedOverlay, 1, 1, 1, 1);
                }
                else if (bone.getName().equals("rightArm"))
                {
                    poseStack.scale(0.67f, 1.33f, 0.67f);
                    poseStack.translate(0.25, -0.43625, 0.1625);
                    playerEntityModel.rightArm.setPivot(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
                    playerEntityModel.rightArm.setAngles(0, 0, 0);
                    playerEntityModel.rightArm.render(poseStack, arm, packedLight, packedOverlay, 1, 1, 1, 1);

                    playerEntityModel.rightSleeve.setPivot(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
                    playerEntityModel.rightSleeve.setAngles(0, 0, 0);
                    playerEntityModel.rightSleeve.render(poseStack, sleeve, packedLight, packedOverlay, 1, 1, 1, 1);

                }
            }
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer1, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        poseStack.pop();
    }
}
