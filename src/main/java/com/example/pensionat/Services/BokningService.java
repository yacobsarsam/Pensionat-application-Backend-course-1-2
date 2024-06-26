package com.example.pensionat.Services;

import com.example.pensionat.Dtos.*;
import com.example.pensionat.Models.Bokning;
import com.example.pensionat.Models.Kund;
import com.example.pensionat.Models.Rum;
import org.springframework.ui.Model;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface BokningService {

    public BokningDto BokningToBokningDto(Bokning bokning);
    public DetailedBokningDto bokningToDetailedBokningDto(Bokning b);
    public List<DetailedBokningDto> getAllBokningar();
    public Bokning getBookingDetailsById(Long id);
    public String deleteBokning(long id);
    public Bokning newBokning(String namn, String tel, String email, LocalDate startdatum, LocalDate slutdatum, Long rumId, int numOfBeds) throws IOException;
    public List<BokningDto> getAllBokningarbyId(Long id);
    public String addBokning();
    public String getAllAvailableRooms(Long bokId, Long rumId, String namn, String telNr,
                                       String email, String startDate, String endDate,
                                       int antalPersoner, Model model);
    public Bokning updateBokning(Long id, LocalDate startDate, LocalDate endDate, int numOfBeds, Long rumId);

    public List<Bokning> getAllBokningarAsBokningById(Long id);
    public int checkBookingsPerCustomer(Long id);
    public int checkDiscountPrice(Bokning b);
    public void removeBookingByEmail(String email);
    public void addRoomModels(List<Rum> sortedRooms, Bokning booking, String roomType, Model model);
}
