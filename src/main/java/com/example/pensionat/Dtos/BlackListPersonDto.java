package com.example.pensionat.Dtos;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlackListPersonDto {
    @Id
    @GeneratedValue
    public int id;
    public String email;
    public String name;
    public String group;
    public String created;
    public boolean ok;


    public BlackListPersonDto(String name, String email, String group, LocalDateTime created, boolean ok) {
        this.email = email;
        this.name = name;
        this.group = group;
        this.created = created.toString();
        this.ok = ok;
    }
}