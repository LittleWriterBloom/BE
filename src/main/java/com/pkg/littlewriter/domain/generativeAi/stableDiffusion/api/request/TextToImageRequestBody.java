package com.pkg.littlewriter.domain.generativeAi.stableDiffusion.api.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TextToImageRequestBody {
    private String key;

    /**
     * The id of the model. get model_id from <a href= https://modelslab.com/models>here.
     */
    private String modelId;

    /**
     * Text prompt with description of the things you want in the image to be generated
     */
    private String prompt;

    /**
     * Items you don't want in the image
     */
    private String negativePrompt;


    /**
     * Max Height: Width: 1024x1024
     */
    private String width;

    /**
     * Max Height: Width: 1024x1024
     */
    private String height;

    /**
     * Number of images to be returned in response. The maximum value is 4.
     */
    @Builder.Default
    private String samples = "1";

    /**
     * Number of denoising steps. The value accepts 21,31,41 and 51
     */
    @Builder.Default
    private String numInferenceSteps = "21";

    /**
     * A checker for NSFW images. If such an image is detected, it will be replaced by a blank image.
     */
    @Builder.Default
    private String safetyChecker = "no";
    /**
     * Modify image if NSFW images are found;
     * default: sensitive_content_text
     * options: blur/sensitive_content_text/pixelate/black
    */
    private String safetyCheckerType;

    /**
     * Enhance prompts for better results; default: yes, options: yes/no
     */
    @Builder.Default
    private String enhancePrompt = "no";

    /**
     * his allows you to set the style of the image for better result. The following are the styles that are available;
     * enhance,cinematic-diva,nude,nsfw,sex,abstract-expressionism,academia,
     * action-figure,adorable-3d-character,adorable-kawaii,art-deco,
     * art-nouveau,astral-aura,avant-garde,baroque,bauhaus-style-poster,
     * blueprint-schematic-drawing,caricature,cel-shaded-art,
     * character-design-sheet,classicism-art,color-field-painting,
     * colored-pencil-art,conceptual-art,constructivism,cubism,dadaism,
     * dark-fantasy,dark-moody-atmosphere,dmt-art,doodle-art,double-exposure,
     * dripping-paint-splatter,expressionism,faded-polaroid-photo,fauvism,flat-2d,
     * fortnite-style,futurism,glitchcore,glo-fi,googie-style,graffiti-art,
     * harlem-renaissance-art,high-fashion,idyllic,impressionism,infographic-drawing,
     * ink-dripping-drawing,japanese-ink-drawing,knolling-photography,
     * light-cheery-atmosphere,logo-design,luxurious-elegance,macro-photography,
     * mandola-art,marker-drawing,medievalism,minimalism,neo-baroque,neo-byzantine,
     * neo-futurism,neo-impressionism,neo-rococo,neoclassicism,op-art,ornate-and-intricate,
     * pencil-sketch-drawing,pop-art-2,rococo,silhouette-art,simple-vector-art,sketchup,
     * steampunk-2,surrealism,suprematism,terragen,tranquil-relaxing-atmosphere,sticker-designs,
     * vibrant-rim-light,volumetric-lighting,watercolor,whimsical-and-playful,sharp,masterpiece,
     * photograph,negative,cinematic,ads-advertising,ads-automotive,ads-corporate,
     * ads-fashion-editorial,ads-food-photography,ads-gourmet-food-photography,ads-luxury,
     * ads-real-estate,ads-retail,abstract,abstract-expressionism,art-deco,art-nouveau,
     * constructivist,cubist,expressionist,graffiti,hyperrealism,impressionist,pointillism,
     * pop-art,psychedelic,renaissance,steampunk,surrealist,typography,watercolor,
     * futuristic-biomechanical,futuristic-biomechanical-cyberpunk,futuristic-cybernetic,
     * futuristic-cybernetic-robot,futuristic-cyberpunk-cityscape,futuristic-futuristic,
     * futuristic-retro-cyberpunk,futuristic-retro,futuristic-sci-fi,futuristic-vaporwave,
     * game-bubble,game-cyberpunk,game-fighting,game-gta,game-mario,game-minecraft,game-pokemon,
     * game-retro-arcade,game-retro,game-rpg-fantasy,game-strategy,game-streetfighter,game-zelda,
     * misc-architectural,misc-disco,misc-dreamscape,misc-dystopian,misc-fairy-tale,misc-gothic,
     * misc-grunge,misc-horror,misc-kawaii,misc-lovecraftian,misc-macabre,misc-manga,misc-metropolis,
     * misc-minimalist,misc-monochrome,misc-nautical,misc-space,misc-stained-glass,
     * misc-techwear-fashion,misc-tribal,misc-zentangle,papercraft-collage,papercraft-flat-papercut,
     * papercraft-kirigami,papercraft-paper-mache,papercraft-paper-quilling,
     * papercraft-papercut-collage,papercraft-papercut-shadow-box,papercraft-stacked-papercut,
     * papercraft-thick-layered-papercut,photo-alien,photo-film-noir,photo-glamour,
     * photo-hdr,photo-iphone-photographic,photo-long-exposure,photo-neon-noir,photo-silhouette,
     * photo-tilt-shift,3d-model,analog-film,anime,cinematic,comic-book,craft-clay,digital-art,
     * fantasy-art,isometric,line-art,lowpoly,neonpunk,origami,photographic,pixel-art,texture,
     */
    private String enhanceStyle;

    /**
     * Seed is used to reproduce results, same seed will give you same image in return again. Pass null for a random number.
     */
    @Builder.Default
    private Long seed = null;

    /**
     * Scale for classifier-free guidance (minimum: 1; maximum: 20)
     */
    @Builder.Default
    private Float guidanceScale = 7.5f;

    /**
     *Enable tomesd to generate images: gives really fast results, default: yes, options: yes/no
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
     * Strength of lora model you are using.
     * If using multi lora, pass each values as comma separated
     */
    private Float loraStrength;

    /**
     * 	pass Lora model id, multi lora is supported, pass comma separated values
     */
    private String loraModel;

    /**
     * Allow multi-lingual prompt to generate images.
     * Set this to "yes" if you use a language different from English in your text prompts.
     */
    private String multiLingual;

    /**
     * Set this parameter to "yes" to generate a panorama image.
     */
    private String panorama;

    /**
     * If you want a high quality image, set this parameter to "yes". In this case the image generation will take more time.
     */
    private String selfAttention;

    /**
     * Set this parameter to "2" if you want to upscale the given image resolution two times (2x), options:: 1, 2, 3
     */
    private String upscale;

    /**
     * Clip Skip (minimum: 1; maximum: 8)
     */
    private String clipSkip;

    /**
     * Get response as base64 string, default: "no", options: yes/no
     */
    private String base64;

    /**
     * Use it to pass an embeddings model.
     */
    private String embeddingsModels;

    /**
     * Use it to set a <a href=https://docs.modelslab.com/image-generation/community-models/dreamboothtext2img#schedulers>scheduler</a>
     */
    private String scheduler;

    /**
     * Set an URL to get a POST API call once the image generation is complete.
     */
    private String webhook;

    /**
     * This ID is returned in the response to the webhook API call. This will be used to identify the webhook request.
     */
    private String trackId;

    /**
     * Create temp image link. This link is valid for 24 hours. temp: yes, options: yes/no
     */
    private String temp;
}
