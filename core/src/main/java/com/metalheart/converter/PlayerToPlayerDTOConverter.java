package com.metalheart.converter;

import com.metalheart.model.logic.Player;
import com.metalheart.model.physic.Vector3d;
import com.metalheart.model.transport.PlayerDTO;
import com.metalheart.model.transport.Vector3;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PlayerToPlayerDTOConverter implements Converter<Player, PlayerDTO> {

    public PlayerDTO convert(Player src) {
        PlayerDTO dst = new PlayerDTO();
        dst.setPosition(convert(src.getTransform().getPosition()));
        dst.setRotation(convert(src.getTransform().getRotation()));
        dst.setSpeed(src.getSpeed());
        return dst;
    }

    private Vector3 convert(Vector3d vector) {
        return new Vector3(vector.d0, vector.d1, vector.d2);
    }
}
