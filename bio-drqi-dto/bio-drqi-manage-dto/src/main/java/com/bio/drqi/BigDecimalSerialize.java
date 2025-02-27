package com.bio.drqi;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class BigDecimalSerialize extends JsonSerializer<BigDecimal> {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.00");

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        }else if(value.compareTo(new BigDecimal(0))==0){
            gen.writeString("0.00");
        } else {
            DecimalFormat df = new DecimalFormat("#0.00");
            gen.writeString(df.format(value));
        }
    }
}