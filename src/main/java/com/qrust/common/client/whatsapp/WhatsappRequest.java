package com.qrust.common.client.whatsapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WhatsappRequest {
    @JsonProperty("messaging_product")
    private String messagingProduct;
    private String to;
    private String type;
    private Template template;

    @Data
    @Builder
    public static class Template {
        private String name;
        private Language language;
        private List<Component> components;
    }

    @Data
    @Builder
    public static class Language {
        private String code;
    }

    @Data
    @Builder
    public static class Component {
        private String type;
        @JsonProperty("sub_type")
        private String subType;
        private int index;
        private List<Parameter> parameters;
    }

    @Data
    @Builder
    public static class Parameter {
        private String type;
        @JsonProperty("parameter_name")
        private String parameterName;
        private String text;
    }
}
