package com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageToImageRequestBody {
    private String key;
    /**
     * The ID of the model to be used. It can be public or your trained model.
     */
    private String modelId;

    /**
     * ControlNet model ID. It can be from the models list or user trained.
     */
    private String controlnetModel;

    /**
     * ControlNet model type. It can be from the models list.
     */
    private String controlnetType;

    /**
     * Auto hint image;options: yes/no
     */
    private String autoHint;

    /**
     * Set this to yes if you don't pass any prompt. The model will try to guess what's in the init_image and create best variations on its own. Options: yes/no
     */
    private String guessMode;

    /**
     * Text prompt with description of required image modifications. Make it as detailed as possible for best results.
     */
    private String prompt;

    /**
     * Items you don't want in the image
     */
    private String negativePrompt;

    /**
     * Link to the Initial Image
     */
    private String initImage;

    /**
     * Lint to the Controlne Image
     */
    private String controlImage;

    /**
     * Link to the mask image for inpainting
     */
    private String maskImage;

    /**
     * Max Height: Width:1024x1024
     */
    private String width;

    /**
     * Max Height: Width:1024x1024
     */
    private String height;

    /**
     * Number of images to be returned in response. The maximum value is 4.
     */
    private String samples;

    /**
     * Use it to set a scheduler
     */
    private String scheduler;

    /**
     * Enable tomesd to generate images: gives really fast results, default: yes, options: yes/no
     */
    private String tomesd;

    /**
     * Use keras sigmas to generate images. gives nice results, default: yes, options: yes/no
     */
    private String useKarrasSigmas;

    /**
     * Used in DPMSolverMultistepScheduler scheduler, default: none, options: dpmsolver+++
     */
    private String algorithmType;

    /**
     * use custom vae in generating images default: null
     */
    private String vae;

    /**
     * use different lora strengths default: null
     */
    private Float loraStrength;

    /**
     * multi lora is supported, pass comma saparated values. Example contrast-fix,yae-miko-genshin
     */
    private String loraModel;

    /**
     * Number of denoising steps, The value accepts 21,31,41.
     */
    private String numInferenceSteps;

    /**
     * A checker for NSFW images. If such an image is detected, it will be replaced by a blank image.
     */
    private String safetyChecker;

    /**
     * Use it to pass an embeddings model.
     */
    private String embeddingsModel;

    /**
     * 	Enhance prompts for better results; default: yes, options: yes/no
     */
    private String enhancePrompt;

    /**
     * Use different language then english; default: yes, options: yes/no
     */
    private String multiLingual;

    /**
     * Basically "guidance_scale". Scale for controlnet guidance. Accepts floating values from 0.1 to 5 (e.g. 0.5)
     */
    private Float controlnetConditioningScale;

    /**
     * 	Prompt strength when using init_image. 1.0 corresponds to full destruction of information in the init image
     */
    private Float strength;

    /**
     * Seed is used to reproduce results, same seed will give you same image in return again. Pass null for a random number.
     */
    private Long seed;

    /**
     * Set an URL to get a POST API call once the image generation is complete.
     */
    private String webhook;

    /**
     * This ID is returned in the response to the webhook API call. This will be used to identify the webhook request.
     */
    private String trackId;

    /**
     * 	Set this parameter to "yes" if you want to upscale the given image resolution two times (2x). If the requested resolution is 512 x 512 px, the generated image will be 1024 x 1024 px.
     */
    private String upscale;

    /**
     * Clip Skip (minimum: 1; maximum: 8)
     */
    private String clipSkip;

    /**
     * Get response as base64 string, pass init_image, mask_image , control_image as base64 string, to get base64 response. default: "no", options: yes/no
     */
    private String base64;

    /**
     * Create temp image link. This link is valid for 24 hours. temp: yes, options: yes/no
     */
    private String temp;

    /**
     * Instant response with fetch
     */
    @Builder.Default
    private String instantResponse = "yes";
}
