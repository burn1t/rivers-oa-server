package com.ocean.query;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProcessInstanceQuery extends BasePage{

    private String processName;

    private String proposer;

}
