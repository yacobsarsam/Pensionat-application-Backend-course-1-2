package com.example.pensionat.Pensionat.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Rum {

    @Id
    @GeneratedValue
    protected long id;
    protected int rumsnr;
    protected boolean dubbelrum;
    // storlek 1 - enkelrum, 2 - Dubbelrum med möjlighet till en extrasäng, 3 - Dubbelrum  med möjlighet till två extrasängar.
    protected int storlek;

    public Rum(boolean dubbelrum, int storlek, int rumsnr) {
        this.dubbelrum = dubbelrum;
        this.storlek = storlek;
        this.rumsnr = rumsnr;
    }

}
