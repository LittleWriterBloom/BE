package com.pkg.littlewriter.domain.generativeAi.openAi.chat.common;
import com.pkg.littlewriter.domain.generativeAi.Jsonable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OneAttributeJsonable extends Jsonable {
    private final String attributeName;
    private final String value;

    public OneAttributeJsonable(String attributeName, String value) {
        this.attributeName = attributeName;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toJsonString() {
        return "{\"" + attributeName + "\" : \"" + value + "\"}";
    }
}
