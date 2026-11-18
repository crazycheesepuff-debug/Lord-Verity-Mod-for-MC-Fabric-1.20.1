package com.lordverity.client;

import com.lordverity.entity.VerityEntity;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

/**
 * Lord Verity's body, built by hand. 1 model unit = 1/16 block, so he is exactly 64 units = 4 blocks tall.
 * Feet are at y = 24 (vanilla convention). Texture is 128x128, see textures/entity/lord_verity.png.
 *
 *   hat top      y = -40
 *   head         y = -36 .. -24      (10 x 12 x 10)
 *   torso        y = -24 ..  -6      (10 x 18 x  6)
 *   legs + shoes y =  -6 ..  24      (4 wide, 30 tall)
 *   arms hang from the shoulders at y = -22 (3 wide, 26 long)
 */
public class VerityModel extends EntityModel<VerityEntity> {
    private static final float RAD = (float) (Math.PI / 180.0);

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public VerityModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static TexturedModelData createModelData() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();

        ModelPartData head = root.addChild("head",
                ModelPartBuilder.create().uv(0, 46).cuboid(-5, -12, -5, 10, 12, 10),
                ModelTransform.pivot(0, -24, 0));
        head.addChild("crown",
                ModelPartBuilder.create().uv(0, 28).cuboid(-6, -16, -6, 12, 5, 12),
                ModelTransform.NONE);
        head.addChild("brim",
                ModelPartBuilder.create().uv(0, 0).cuboid(-13, -12, -13, 26, 1, 26),
                ModelTransform.NONE);

        root.addChild("body",
                ModelPartBuilder.create().uv(52, 28).cuboid(-5, -18, -3, 10, 18, 6),
                ModelTransform.pivot(0, -6, 0));

        ModelPartBuilder arm = ModelPartBuilder.create()
                .uv(90, 28).cuboid(-1.5f, -2, -1.5f, 3, 20, 3)     // sleeve
                .uv(104, 28).cuboid(-1.5f, 18, -1.5f, 3, 6, 3);    // hand
        root.addChild("right_arm", arm, ModelTransform.pivot(-6.5f, -22, 0));
        root.addChild("left_arm", arm, ModelTransform.pivot(6.5f, -22, 0));

        ModelPartBuilder leg = ModelPartBuilder.create()
                .uv(0, 70).cuboid(-2, 0, -2, 4, 28, 4)             // trousers
                .uv(20, 70).cuboid(-2, 28, -4, 4, 2, 6);           // shoe
        root.addChild("right_leg", leg, ModelTransform.pivot(-2.5f, -6, 0));
        root.addChild("left_leg", leg, ModelTransform.pivot(2.5f, -6, 0));

        return TexturedModelData.of(data, 128, 128);
    }

    @Override
    public void setAngles(VerityEntity entity, float limbAngle, float limbDistance,
                          float animationProgress, float headYaw, float headPitch) {
        float swing = limbAngle * 0.6662f;

        // head follows where he's looking
        head.yaw = headYaw * RAD;
        head.pitch = headPitch * RAD;

        // run cycle (big, floppy swings: he is a long boy)
        rightLeg.pitch = MathHelper.cos(swing) * 1.4f * limbDistance;
        leftLeg.pitch = MathHelper.cos(swing + (float) Math.PI) * 1.4f * limbDistance;
        rightArm.pitch = MathHelper.cos(swing + (float) Math.PI) * 1.6f * limbDistance;
        leftArm.pitch = MathHelper.cos(swing) * 1.6f * limbDistance;
        rightArm.yaw = 0; leftArm.yaw = 0;
        rightArm.roll = 0; leftArm.roll = 0;

        // INTRO: head thrown back, arms in the air, shaking with laughter
        if (entity.getPhase() == VerityEntity.PHASE_INTRO) {
            float shake = MathHelper.sin(animationProgress * 1.4f);
            head.pitch = -0.55f + shake * 0.12f;
            rightArm.pitch = -2.6f + shake * 0.35f;
            leftArm.pitch = -2.6f - shake * 0.35f;
        }

        // SLAP: right arm whips across
        float slap = entity.getHandSwingProgress(animationProgress - entity.age);
        if (slap > 0.0f) {
            rightArm.pitch = -1.45f;
            rightArm.yaw = 1.3f - 2.8f * slap;
        }
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay,
                       float red, float green, float blue, float alpha) {
        root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}
