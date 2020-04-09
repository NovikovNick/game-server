package com.metalheart.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metalheart.model.PlayerRequest;
import io.netty.buffer.ByteBuf;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PlayerRequestConverter {

    ObjectMapper mapper = new ObjectMapper();

    public PlayerRequest convert(ByteBuf src) {

        PlayerRequest dst = null;
        try {
            dst = mapper.readValue(src.toString(UTF_8), PlayerRequest.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        /*
        dst.setPosition(new Vector3(src.getFloatLE(0), src.getFloatLE(4), src.getFloatLE(8)));
        dst.setDirection(new Vector3(src.getFloatLE(12), src.getFloatLE(16), src.getFloatLE(20)));
        dst.setPlayerId(src.getByte(24));
        */

        return dst;
    }

   /* public byte[] convert(PlayerRequest src) {

        byte[] dst = new byte[6 * Float.BYTES + 1];

        System.arraycopy(floatToByteArrayLE(src.getPosition().getX()), 0, dst, 0 * Float.BYTES, Float.BYTES);
        System.arraycopy(floatToByteArrayLE(src.getPosition().getY()), 0, dst, 1 * Float.BYTES, Float.BYTES);
        System.arraycopy(floatToByteArrayLE(src.getPosition().getZ()), 0, dst, 2 * Float.BYTES, Float.BYTES);

        System.arraycopy(floatToByteArrayLE(src.getDirection().getX()), 0, dst, 3 * Float.BYTES, Float.BYTES);
        System.arraycopy(floatToByteArrayLE(src.getDirection().getY()), 0, dst, 4 * Float.BYTES, Float.BYTES);
        System.arraycopy(floatToByteArrayLE(src.getDirection().getZ()), 0, dst, 5 * Float.BYTES, Float.BYTES);

        dst[6 * Float.BYTES] = src.getPlayerId();
        return dst;
    }*/

    public static byte[] floatToByteArrayLE(float value) {
        int bits = Float.floatToIntBits(value);
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (bits & 0xff);
        bytes[1] = (byte) ((bits >> 8) & 0xff);
        bytes[2] = (byte) ((bits >> 16) & 0xff);
        bytes[3] = (byte) ((bits >> 24) & 0xff);
        return bytes;
    }
}
