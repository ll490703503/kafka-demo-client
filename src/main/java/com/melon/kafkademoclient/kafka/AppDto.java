package com.melon.kafkademoclient.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppDto {
    private String envType;
    private String envName;
    private String appName;
    private String version;
    private String needDownload;

}
