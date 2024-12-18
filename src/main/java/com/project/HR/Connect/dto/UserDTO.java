package com.project.HR.Connect.dto;

import com.project.HR.Connect.entitie.Address;
import com.project.HR.Connect.entitie.IdentityCard;
import com.project.HR.Connect.entitie.LoginDetails;
import com.project.HR.Connect.entitie.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private User user;

    private LoginDetails loginDetails;

    private Address address;

    private IdentityCard identityCard;
}
