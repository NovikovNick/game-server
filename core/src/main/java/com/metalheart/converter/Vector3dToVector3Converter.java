package com.metalheart.converter;

import com.metalheart.model.physic.Vector3d;
import com.metalheart.model.transport.Vector3;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class Vector3dToVector3Converter implements Converter<Vector3d, Vector3> {

    public Vector3 convert(Vector3d src) {
        return new Vector3(src.d0, src.d1, src.d2);
    }
}
