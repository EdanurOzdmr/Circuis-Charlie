package com.circuscharlie;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;


public class Ates {
    Vector2 pozisyon=new Vector2();
    TextureRegion resim;

    public Ates(float x , float y, TextureRegion resim){
        this.pozisyon.x=x;
        this.pozisyon.y=y;
        this.resim=resim;

    }
}
