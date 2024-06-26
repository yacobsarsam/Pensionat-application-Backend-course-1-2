package com.example.pensionat.Services.Imp;

import com.example.pensionat.Dtos.*;
import com.example.pensionat.Models.Bokning;
import com.example.pensionat.Models.Kund;
import com.example.pensionat.Models.Rum;
import com.example.pensionat.Repositories.BokningRepo;
import com.example.pensionat.Repositories.RumRepo;
import com.example.pensionat.Services.BokningService;
import com.example.pensionat.Services.KundService;
import com.example.pensionat.Services.RumService;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BokningServiceImp implements BokningService {

    private final BokningRepo br;
    private final RumRepo rumRepo;

    private final KundService kundService;
    private final RumService rumService;

    public String addBokning() {
        return "addBokning";
    }

    @Override
    public BokningDto BokningToBokningDto(Bokning b) {
        return BokningDto.builder().id(b.getId()).startdatum(String.valueOf(b.getStartdatum())).
                slutdatum(String.valueOf(b.getSlutdatum())).numOfBeds(b.getNumOfBeds()).build();
    }

    @Override
    public DetailedBokningDto bokningToDetailedBokningDto(Bokning b) {
        return DetailedBokningDto.builder().id(b.getId()).startdatum(String.valueOf(b.getStartdatum())).
                slutdatum(String.valueOf(b.getSlutdatum())).numOfBeds(b.getNumOfBeds())
                .kund(new KundDto(b.getKund().getId(), b.getKund().getNamn(), b.getKund().getTel(), b.getKund().getEmail()))
                .rum(new RumDto(b.getRum().getId(), b.getRum().getRumsnr())).totalPrice(b.getTotalPrice()).build();
    }

    @Override
    public Bokning getBookingDetailsById(Long id) {
        Optional<Bokning> optionalBokning = br.findById(id);
        if (optionalBokning.isPresent()) {
            Bokning bokning = optionalBokning.get();
            return bokning;
        } else {
            return null;
        }
    }

    @Override
    public List<DetailedBokningDto> getAllBokningar() {
        return br.findAll().stream().map(bok -> bokningToDetailedBokningDto(bok)).toList();
    }

    @Override
    public List<BokningDto> getAllBokningarbyId(Long id) {
        return br.findAll().stream().filter(b -> b.getKund().getId() == id).map(bok -> BokningToBokningDto(bok)).toList();
    }

    @Override
    public List<Bokning> getAllBokningarAsBokningById(Long id) {
        return br.findAll().stream().filter(bokning -> bokning.getKund().getId() == id).toList();
    }

    @Override
    public Bokning updateBokning(Long bokId, LocalDate startDate, LocalDate endDate, int numOfBeds, Long rumId) {
        int roomTypeSize;
        if (rumService.getRumById(rumId).isDubbelrum()) {
            roomTypeSize = 2;
        } else {
            roomTypeSize = 1;
        }
        Bokning b = getBookingDetailsById(bokId);
        Rum r = rumService.getRumById(rumId);
        b.setRum(r);
        b.setStartdatum(startDate);
        b.setSlutdatum(endDate);
        b.setNumOfBeds(numOfBeds + roomTypeSize);
        int price = checkDiscountPrice(b);
        b.setTotalPrice(price);
        br.save(b);
        return b;
    }

    @Override
    public String deleteBokning(long id) {
        Optional<Bokning> optionalBokning = br.findById(id);
        if (optionalBokning.isPresent()) {
            Bokning bokning = optionalBokning.get();
            br.delete(bokning);
            return "Bokningen borttagen";
        } else {
            return "Bokningen hittas inte";
        }
    }

    @Override
    public Bokning newBokning(String namn, String tel, String email, LocalDate startdatum, LocalDate slutdatum, Long rumId, int numOfBeds) throws IOException {
        int roomTypeSize;
        if (rumService.getRumById(rumId).isDubbelrum()) {
            roomTypeSize = 2;
        } else {
            roomTypeSize = 1;
        }

        KundDto kundDto = kundService.checkIfKundExistByEmail(namn, email, tel);
        Kund kund = kundService.kundDtoToKund(kundDto);
        Rum rum = rumService.getRumById(rumId);
        Bokning b = new Bokning(kund, rum, startdatum, slutdatum, numOfBeds + roomTypeSize);
        int price = checkDiscountPrice(b);
        b.setTotalPrice(price);
        br.save(b);
        return b;
    }


    //TODO Anropa metoden från rätt plats i koden för att avbryta bokningen.
    //Kollar om epost är blacklistad eller ej
    //Om ok = fale --> epost är blacklistad
    //TODO ska denna flyttas till blacklistService?
    private boolean CustomerIsBlackList(String email) throws IOException {
        JsonMapper jSonMapper = new JsonMapper();
        BlackListPersonDto[] blps = jSonMapper.readValue(new URL("https://javabl.systementor.se/api/stefan/blacklist")
                , BlackListPersonDto[].class);
        for (BlackListPersonDto bl : blps) {
            if (bl.email.equals(email) && !bl.ok)
                return true;
        }
        return false;
    }


    @Override
    public String getAllAvailableRooms(Long bokId, Long rumId, String name, String telNr, String email, String startDate, String endDate, int antalPersoner, Model model) {
        String felmeddelande;
        //Kontrollera kundfälten
        if (!isCustomerFieldsFilledAndCorrect(name, telNr, email)) {
            felmeddelande = "Fel i kund-fälten, kontrollera och försök igen";
            return addModelsAndReturn(name, telNr, email, startDate, endDate, antalPersoner, model, felmeddelande, "addBokning");
        }
        //Kolla vilken storlek på rum som kan visas
        boolean needsDoubleRoom = getNeedsDoubleRoom(getRequiredRoomType(antalPersoner));
        int neededSize = antalPersoner - 1;
        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            if (validateRequestedBookingDates(name, telNr, email, startDate, endDate, antalPersoner, model) != null){
                return validateRequestedBookingDates(name, telNr, email, startDate, endDate, antalPersoner, model);
            };
            addRoomModels(getFilteredRooms(needsDoubleRoom, neededSize, getValidatedBookingDateList(startDate, endDate)), getBookingDetailsById(bokId), getRequiredRoomType(antalPersoner), model);
            if (bokId != 0) {
                return addModelsAndReturn(name, telNr, email, startDate, endDate, antalPersoner, model, "", "updateBooking");
            } else {
                return addModelsAndReturn(name, telNr, email, startDate, endDate, antalPersoner, model, "", "addBokning");
            }

        } else {
            felmeddelande = "Inga eller bara ett datum valdes";
            model.addAttribute("felmeddelande", felmeddelande);
            return "addBokning";
        }

    }

    public List<Long> getNonAvailableRoomsId(List<Bokning> bokningar, LocalDate startDate, LocalDate endDate) {
        List<Long> nonAvailableRooms = new ArrayList<>();

        for (Bokning bokning : bokningar) {
            if (bokning.getStartdatum().isBefore(endDate) && bokning.getSlutdatum().isAfter(startDate)) {
                nonAvailableRooms.add(bokning.getRum().getId());
            }
        }
        return nonAvailableRooms;
    }

    public boolean isCustomerFieldsFilledAndCorrect(String name, String telnr, String email) {
        if (name.trim().isEmpty()) {
            return false;
        } else if (telnr.trim().length() < 10 && !telnr.trim().matches("[0-9+-}+]")) {
            return false;
        } else return !email.trim().isEmpty();
    }

    String addModelsAndReturn(String name, String telnr, String email, String startDate, String endDate, int antalPersoner, Model model, String felmeddelande, String template) {
        model.addAttribute("name", name);
        model.addAttribute("telNr", telnr);
        model.addAttribute("email", email);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("antalPersoner", antalPersoner);
        model.addAttribute("felmeddelande", felmeddelande);
        return template;
    }

    @Override
    public int checkBookingsPerCustomer(Long id) {
        //hitta alla bokningar med kundens ID och filtrera så bara senaste året är med
        List<Bokning> bl = br.findAll().stream().filter(bokning -> bokning.getKund().getId() == id).toList();
        List <Bokning> bokningsList = bl.stream().filter(bokning -> bokning.getStartdatum().isAfter(LocalDate.now().minusYears(1))).toList();

        Long totalNights = 0L;

        for (Bokning b : bokningsList) {
            LocalDate start = b.getStartdatum();
            LocalDate end = b.getSlutdatum();
            totalNights += ChronoUnit.DAYS.between(start, end);
        }
        return Math.toIntExact(totalNights);
    }

    @Override
    public int checkDiscountPrice(Bokning b) {
        double totalPrice = 0;
        int pricePerNight = b.getRum().getPrice();

        //hur många nätter är bokningen
        long nightsNow = ChronoUnit.DAYS.between(b.getStartdatum(), b.getSlutdatum());

        LocalDate date = b.getStartdatum();

        for (int i = 0; i < nightsNow; i++) {
            //kolla om natten är söndag till måndag
            if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                totalPrice = totalPrice + (pricePerNight * 0.98);
            } else {
                totalPrice = totalPrice + pricePerNight;
            }
            date = date.plusDays(1);
        }

        // bokat mer än två nätter
        if (nightsNow >= 2) {
            totalPrice *= 0.995;
        }

        // bokat minst 10 nätter senaste året
        if (checkBookingsPerCustomer(b.getKund().getId()) >= 10) {
            totalPrice *= 0.98;
        }
        b.setTotalPrice((int) Math.round(totalPrice));
        return (int) Math.round(totalPrice);
    }

    @Override
    public void removeBookingByEmail(String email) {
        List<Bokning> bookingsToRemove = br.findAll().stream()
                .filter(bokning -> bokning.getKund().getEmail().equals(email))
                .collect(Collectors.toList());
        for (Bokning booking : bookingsToRemove) {
            br.delete(booking);
        }
    }

    private String getRequiredRoomType(int antalPersoner){
        boolean needsDouble = antalPersoner > 1;
        if (needsDouble) {
            return  "Dubbelrum";
        } else {
            return "Enkelrum";
        }
    }

    private String validateRequestedBookingDates(String name, String telNr, String email, String startDate, String endDate, int antalPersoner, Model model){
        String felmeddelande;
        LocalDate from = LocalDate.parse(startDate);
        LocalDate until = LocalDate.parse(endDate);
        //kontroll för att slut datum är EFTER startdatum
        if (!from.isBefore(until)) {
            felmeddelande = "Fel i datumen, du har valt ett till-datum som är före från-datum";
            model.addAttribute("felmeddelande", felmeddelande);
            return addModelsAndReturn(name, telNr, email, startDate, endDate, antalPersoner, model, felmeddelande, "addBokning");
        } else if (from.isBefore(LocalDate.now())) {
            //kontroll att start datumet inte has passerat redan
            felmeddelande = "Fel i datumen, du har valt ett datum som redan passerat";
            model.addAttribute("felmeddelande", felmeddelande);
            return addModelsAndReturn(name, telNr, email, startDate, endDate, antalPersoner, model, felmeddelande, "addBokning");
        }
        return null;
    }

    private List<LocalDate> getValidatedBookingDateList(String startDate, String endDate){
        LocalDate from = LocalDate.parse(startDate);
        LocalDate until = LocalDate.parse(endDate);
        return Arrays.asList(from, until);
    }

    private boolean getNeedsDoubleRoom(String roomType){
        return roomType.trim().equalsIgnoreCase("dubbelrum");
    }

    public void addRoomModels(List<Rum> sortedRooms, Bokning booking, String roomType, Model model){
        model.addAttribute("allRooms", sortedRooms);
        model.addAttribute("booking", booking);
        model.addAttribute("rubrik", "Lediga rum");
        model.addAttribute("roomType", roomType);
    }

    private List<Rum> getFilteredRooms(boolean needsDoubleRoom, int neededSize, List<LocalDate> bookingDates){
        List<Long> notAva = getNonAvailableRoomsId(br.findAll(), bookingDates.get(0), bookingDates.get(1));
        return rumRepo.findAll().stream().filter(rum -> rum.isDubbelrum() == needsDoubleRoom)
                .filter(rum -> rum.getStorlek() >= neededSize)
                .filter(rum -> notAva.stream().noneMatch(notAvaRum -> notAvaRum.equals(rum.getId()))).toList();
    }
}
