package com.particle.asset.manager.DTO;

import lombok.Data;

@Data
public class UserSummaryDTO
{
    private Long id;
    private String name, surname, email;

    public UserSummaryDTO(Long id, String name, String surname, String email) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }

    public UserSummaryDTO() {

    }
}
