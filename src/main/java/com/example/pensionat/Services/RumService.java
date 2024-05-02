package com.example.pensionat.Services;

import com.example.pensionat.Dtos.DetailedRumDto;
import com.example.pensionat.Dtos.RumDto;
import com.example.pensionat.Models.Bokning;
import com.example.pensionat.Models.Kund;
import com.example.pensionat.Models.Bokning;
import com.example.pensionat.Models.Rum;

import java.time.LocalDate;
import java.util.List;

import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.List;

public interface RumService {

    public RumDto rumToRumDto(Rum r);
    public Rum rumDtoToRum(RumDto r);
    public DetailedRumDto rumToDetailedRumDto(Rum r);
    public Rum DetailedRumDtoToRum(DetailedRumDto r);
    public List<DetailedRumDto> getAllRum();
    public String addRum(Rum r);
    public String updateRum(RumDto r);
    public String deleteRum(Long id);
    public Rum getRumById(Long id);
    public List<Rum> getAllRum2();
}
