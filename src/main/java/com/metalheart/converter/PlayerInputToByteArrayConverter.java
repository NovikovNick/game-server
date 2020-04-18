package com.metalheart.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metalheart.model.PlayerInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PlayerInputToByteArrayConverter implements Converter<PlayerInput, byte[]> {

    @Autowired
    private ObjectMapper mapper;

    public byte[] convert(PlayerInput src) {
        try {
            return mapper.writeValueAsBytes(src);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
