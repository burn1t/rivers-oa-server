package com.ocean.query;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class LeaveQuery extends BasePage implements Serializable {

    private String username;

    private String title;

    private Integer status;

}
