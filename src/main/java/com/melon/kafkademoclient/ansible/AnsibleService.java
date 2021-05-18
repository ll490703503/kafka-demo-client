package com.melon.kafkademoclient.ansible;

import com.melon.kafkademoclient.kafka.AppDto;

public interface AnsibleService {

    void upgradeApplication(AppDto appDto);
}
