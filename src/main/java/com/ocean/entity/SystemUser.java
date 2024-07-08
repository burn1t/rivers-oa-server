package com.ocean.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class SystemUser implements Serializable {

    private String username;

    private String password;

    private String nickName;

    private String imageUrl;

    private String authority;

    private String token;

}
