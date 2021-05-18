package com.melon.kafkademoclient.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class ShellHelper {

    public static final Logger LOGGER = LoggerFactory.getLogger(ShellHelper.class);

    private ShellHelper() {

    }

    public static boolean linuxOperate(String command) {
        boolean result = false;
        try {
            Process proc = Runtime.getRuntime().exec(command);
            // 标准错误流（必须写在 waitFor 之前）
            String errStr = consumeInputStream(proc.getErrorStream());
            int retCode = proc.waitFor();
            if (retCode == 0 || !StringUtils.hasText(errStr)) {
                LOGGER.info("Command exec success : [{}]", command);
                result = true;
            }
        } catch (IOException e) {
            LOGGER.error("Shell command exec failed : [{}] ", command, e);
        } catch (InterruptedException e) {
            LOGGER.error("Shell command exec failed : [{}] ", command, e);
        }
        return result;
    }


    /**
     * 消费输入流信息
     *
     * @param is 输入流
     * @return 输出结果
     */
    public static String consumeInputStream(InputStream is) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String s;
            StringBuilder sb = new StringBuilder();
            while ((s = br.readLine()) != null) {
                System.out.println(s);
                sb.append(s);
            }
            br.close();
            is.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
