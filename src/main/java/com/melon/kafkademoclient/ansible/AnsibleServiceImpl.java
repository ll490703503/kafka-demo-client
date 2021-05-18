package com.melon.kafkademoclient.ansible;

import com.melon.kafkademoclient.kafka.AppDto;
import com.melon.kafkademoclient.util.JsonHelper;
import com.melon.kafkademoclient.util.ShellHelper;
import jnr.posix.POSIXFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


@Service
public class AnsibleServiceImpl implements AnsibleService {

    public static final Logger LOGGER = LoggerFactory.getLogger(AnsibleServiceImpl.class);

    public static final String SUCCESS_TOPIC = "success";
    public static final String FAILURE_TOPIC = "failure";

    public static final String ANSIBLE_COMMAND = "ansible-playbook";
    public static final String ANSIBLE_ROOT_PATH = "/data1/everhomes-ansible/";

    private final KafkaTemplate<String, String> kafkaTemplate;

    public AnsibleServiceImpl(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void upgradeApplication(AppDto appDto) {
        Assert.notNull(appDto, "appDto must not be null");
        POSIXFactory.getPOSIX().chdir(ANSIBLE_ROOT_PATH);
        boolean success = ShellHelper.linuxOperate(generationUpgradeCommand(appDto));
        if (success) {
            kafkaTemplate.send(SUCCESS_TOPIC, String.valueOf(System.currentTimeMillis()), JsonHelper.obj2String(appDto));
            return;
        }
        kafkaTemplate.send(FAILURE_TOPIC, String.valueOf(System.currentTimeMillis()), JsonHelper.obj2String(appDto));

    }

    private String generationUpgradeCommand(AppDto appDto) {
        StringBuilder sb = new StringBuilder();
        sb.append(ANSIBLE_COMMAND).append(" -i ")
                .append("inventory/")
                .append(appDto.getEnvType())
                .append("/")
                .append(appDto.getEnvName())
                .append(".ini")
                .append(" -t ")
                .append(appDto.getAppName())
                .append(" update_microservice.yml ")
                .append(" -e ").append("branch=").append(appDto.getVersion())
                .append(" -e ").append("app_name=").append(appDto.getAppName())
                .append(" -e ").append("need_download=").append(appDto.getNeedDownload());
        final String ansible_command = sb.toString();
        LOGGER.info("Ansible command : [{}]", ansible_command);
        return ansible_command;
    }
}
