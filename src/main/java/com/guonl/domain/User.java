package com.guonl.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by guonl
 * Date: 2019-12-03 16:45
 * Description:
 */
@Data
public class User implements Serializable {

    private Integer id;

    private String name;

    private String password;
}
